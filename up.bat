@echo off

cd backend
cmd /c .\gradlew bootJar

cd ..
docker compose up --build -d