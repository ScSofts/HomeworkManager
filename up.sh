export DOCKER_BUILDKIT=0

cd backend || error_exit "The backend directory does not exist."
./gradlew bootJar

cd ..

cd frontend || error_exit "The frontend directory does not exist."
yarn
yarn build
cd ..

mv backend/build/libs/backend-0.0.1-SNAPSHOT.jar docker/backend.jar

docker-compose up --build -d