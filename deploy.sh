./gradlew clean bootJar
docker build -t atm-service-v1 .
docker compose up
