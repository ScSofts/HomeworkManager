export DOCKER_BUILDKIT=0

cd backend || error_exit "The backend directory does not exist."
./gradlew bootJar

cd ..
docker-compose up --build -d