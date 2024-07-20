


cd frontend || error_exit "The frontend directory does not exist."
yarn
yarn build

rm -rf backend/src/main/resources/static
mv -f frontend/build backend/src/main/resources/static

cd ..
cd backend || error_exit "The backend directory does not exist."
./gradlew bootJar

cd ..

cp -f backend/src/main/resources/application.yml config/application.yml


export DOCKER_BUILDKIT=0
docker-compose up --build -d