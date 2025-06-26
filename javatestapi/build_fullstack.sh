#!/bin/bash

set -e

FRONTEND_DIR="frontend"
STATIC_DIR="src/main/resources/static"

printf "\n➡️ Building React frontend..."
cd $FRONTEND_DIR
npm install
npm run build
cd ..

echo "🧹 Cleaning old static files..."
rm -rf $STATIC_DIR/*
cp -r $FRONTEND_DIR/build/* $STATIC_DIR/

echo "☕ Rebuilding Spring Boot JAR..."
./mvnw clean package
docker build -t javatestapi:latest .
