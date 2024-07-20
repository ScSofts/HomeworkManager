@echo off


cd frontend
cmd /c yarn
cmd /c yarn run build
cd ..

rmdir /S /Q .\backend\src\main\resources\static
move /Y .\frontend\build .\backend\src\main\resources\static


cd backend
cmd /c .\gradlew bootJar
cd ..

copy /Y .\backend\src\main\resources\application.yml .\config\

set DOCKER_BUILDKIT=0
docker compose up --build -d