# Backend For Frontend

**Documentation: [backend-for-frontend](https://learn.microsoft.com/en-us/azure/architecture/patterns/backends-for-frontends)**

# GOALS

**Application aggregates requests from different clients, and dispatch to backend-services API when specific conditions
are meet**

**Backend-for-Frontend API**

- Full documented api: http://localhost:8080/swagger-ui/index.html#/aggregation-controller/aggregate

- 'GET' 'http://host:8080/aggregation?pricing=&track&shipments='




**Backend-Services API:**

- GET http://{host}:8090/shipments?q= <p>
- GET http://{host}:8090/pricing?q=
- GET http://{host}:8090/track?q=

**When application accumulates a configurable amount of request (by default 5)
or after a configurable amount of time (by default 5 sec), It will forward all
client requests and forward them to backend-services and dispatch result properly according to each client request** 


# SETUP

-  run.sh

- docker run --name backend-services -p 8090:8080 -d xyzassessment/backend-services
  <p>./mvnw clean install
  <p>java -jar ./target/backend-for-frontend-0.0.1.jar 

# Technologies

- Java 17
- Spring Boot 2.7.5
- Maven
- Swagger
- Mockito
- Junit
