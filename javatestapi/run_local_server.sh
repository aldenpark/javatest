#!/bin/bash

# Rebuild everything first
./build_fullstack.sh

# Run the Spring Boot app
cd javatestapi
java -jar target/javatestapi-0.0.1-SNAPSHOT.jar
