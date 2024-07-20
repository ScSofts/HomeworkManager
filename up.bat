@echo off

cd backend
cmd /c .\gradlew bootJar

cd ..

cd frontend
cmd /c yarn
cmd /c yarn run build
cd ..

move .\frontend\build .\backend\src\main\resources\static

docker compose up --build -d