#!/usr/bin/env bash
# build_fullstack.sh
# Usage:
#   ./build_fullstack.sh dev    # Spring Boot + React dev server (hot reload @ :3000)
#   ./build_fullstack.sh prod   # Build React -> /static, package JAR, build Docker

set -euo pipefail

MODE="${1:-dev}"
FRONTEND_DIR="frontend"
STATIC_DIR="src/main/resources/static"
IMG_TAG="${IMG_TAG:-javatestapi:latest}"
SPRING_ARGS="${SPRING_ARGS:-"-Dspring.profiles.active=dev"}"
REACT_PORT="${REACT_PORT:-3000}"
SPRING_PORT="${SPRING_PORT:-8080}"

ensure_node_deps() {
  pushd "$FRONTEND_DIR" >/dev/null
  if [ -f package-lock.json ]; then npm ci; else npm install; fi
  popd >/dev/null
}

if [[ "$MODE" == "prod" ]]; then
  echo "➡️  Building React for production…"
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

  echo "🐳 Building Docker image: ${IMG_TAG}"
  docker build -t "$IMG_TAG" .

  echo "✅ prod build complete."
  exit 0
fi

# ===== DEV MODE =====
echo "⚙️  Dev mode: Spring @ :$SPRING_PORT + React @ :$REACT_PORT (hot reload)"

ensure_node_deps

echo "☕ Starting Spring Boot in background…"
./mvnw -q spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dserver.port=${SPRING_PORT}" \
  $SPRING_ARGS &
SPRING_PID=$!

cleanup() {
  echo; echo "🛑 Stopping Spring Boot (pid $SPRING_PID)…"
  kill "$SPRING_PID" 2>/dev/null || true
  wait "$SPRING_PID" 2>/dev/null || true
  echo "✅ Stopped."
}
trap cleanup INT TERM EXIT

echo "⚛️  Starting React Dev Server in foreground (hot reload)…"
cd "$FRONTEND_DIR"
# If file watching is flaky (VM/WSL/NFS), uncomment the next line:
# export CHOKIDAR_USEPOLLING=true
export PORT="$REACT_PORT"
# Keep or remove this; it just prevents auto-opening a browser tab:
export BROWSER=none
exec npm start
