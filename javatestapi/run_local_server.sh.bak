#!/usr/bin/env bash

# chmod +x run-local.sh
# ./run-local.sh           # uses mvn spring-boot:run
# ./run-local.sh jar       # builds and runs the jar

set -euo pipefail

# Run from repo root
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# APP="$ROOT/javatestapi"
APP="$ROOT"

# Prefer module-specific .env, fallback to root .env
ENV_FILE=""
if [[ -f "$APP/.env" ]]; then ENV_FILE="$APP/.env"
elif [[ -f "$ROOT/.env" ]]; then ENV_FILE="$ROOT/.env"; fi

if [[ -n "${ENV_FILE}" ]]; then
  echo "Loading env from: $ENV_FILE"
  set -a
  # shellcheck disable=SC1090
  source "$ENV_FILE"
  set +a
fi

# Rebuild everything first
# ./build_fullstack.sh


echo "Starting with:"
echo "  JDBC: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
echo "  User: ${DB_USER}"
echo "  Profile: ${SPRING_PROFILES_ACTIVE}"
echo "  Port: ${PORT}"
echo

cd "$APP"

case "${1:-mvn}" in
  mvn)
    mvn spring-boot:run \
      -Dspring-boot.run.profiles="${SPRING_PROFILES_ACTIVE}" \
      -Dspring-boot.run.jvmArguments="${JAVA_OPTS:-}"
    ;;
  jar)
    mvn -q -DskipTests package
    exec java ${JAVA_OPTS:-} -jar "target/javatestapi-0.0.1-SNAPSHOT.jar"
    ;;
  *)
    echo "Usage: $0 [mvn|jar]"
    exit 1
    ;;
esac

# Run the Spring Boot app
# set -a          # tells bash to export all variables
# source .env     # loads and exports each variable from .env
# set +a
# mvn clean package   # builds jar
# java -jar target/javatestapi-0.0.1-SNAPSHOT.jar
mvn spring-boot:run

# docker compose up -d --build
# docker compose logs -f api
