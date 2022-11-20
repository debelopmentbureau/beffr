echo "Backend-For-Frontend"

docker run -p 9090:8080 -d xyzassessment/backend-services

./mvnw clean install
java -jar ./target/backend-for-frontend-0.0.1.jar
