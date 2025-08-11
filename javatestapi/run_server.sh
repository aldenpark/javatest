#!/usr/bin/env bash
# build_fullstack.sh
# Usage:
#   ./build_fullstack.sh dev          # hot-reload: React dev server + Spring Boot
#   ./build_fullstack.sh prod         # full build: React -> /static, JAR, Docker
# Env (optional):
#   IMG_TAG=javatestapi:latest
#   SPRING_ARGS="-Dspring.profiles.active=dev"
#   REACT_PORT=3000

set -euo pipefail

MODE="${1:-dev}"
FRONTEND_DIR="frontend"
STATIC_DIR="src/main/resources/static"
IMG_TAG="${IMG_TAG:-javatestapi:latest}"
SPRING_ARGS="${SPRING_ARGS:-"-Dspring.profiles.active=dev"}"
REACT_PORT="${REACT_PORT:-3000}"

ensure_node_deps() {
  pushd "$FRONTEND_DIR" >/dev/null
  if [ -f package-lock.json ]; then npm ci; else npm install; fi
  popd >/dev/null
}

if [[ "$MODE" == "prod" ]]; then
  echo "Building React frontend for production…"
  ensure_node_deps
  pushd "$FRONTEND_DIR" >/dev/null
  npm run build
  popd >/dev/null

  echo "🧹 Cleaning old static files…"
  rm -rf "${STATIC_DIR:?}"/*
  mkdir -p "$STATIC_DIR"
  cp -r "$FRONTEND_DIR/build/"* "$STATIC_DIR/"

  echo "☕ Packaging Spring Boot JAR…"
  ./mvnw -q clean package -DskipTests

  echo "Building Docker image: ${IMG_TAG}"
  docker build -t "$IMG_TAG" .

  echo "prod build complete."

elif [[ "$MODE" == "dev" ]]; then
  echo "Starting Dev mode: React dev server (hot reload) + Spring Boot…"

  ensure_node_deps

  SPRING_LOG="target/dev-spring.log"
  REACT_LOG="${FRONTEND_DIR}/dev-react.log"

  echo "☕ Starting Spring Boot (logs → ${SPRING_LOG})…"
  ./mvnw -q spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8080" $SPRING_ARGS \
    >"$SPRING_LOG" 2>&1 &
  SPRING_PID=$!

  echo "Starting React dev server on http://localhost:${REACT_PORT} (logs → ${REACT_LOG})…"
  pushd "$FRONTEND_DIR" >/dev/null
  PORT="$REACT_PORT" npm start >"$REACT_LOG" 2>&1 &
  REACT_PID=$!
  popd >/dev/null

  cleanup() {
    echo ""
    echo "Stopping dev services…"
    kill ${REACT_PID} 2>/dev/null || true
    kill ${SPRING_PID} 2>/dev/null || true
    wait ${REACT_PID} 2>/dev/null || true
    wait ${SPRING_PID} 2>/dev/null || true
    echo "Stopped."
  }
  trap cleanup INT TERM EXIT

  echo "Dev URLs:"
  echo "   UI:   http://localhost:${REACT_PORT}"
  echo "   API:  http://localhost:8080"
  echo
  echo "Tailing logs (Ctrl-C to stop)…"
  tail -n 20 -f "$SPRING_LOG" "$REACT_LOG"

else
  echo "Unknown mode: $MODE"
  echo "Usage: $0 [dev|prod]"
  exit 1
fi
