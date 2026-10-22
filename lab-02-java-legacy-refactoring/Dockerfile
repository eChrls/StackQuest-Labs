FROM maven:3.9-eclipse-temurin-21-alpine
WORKDIR /workspace
COPY pom.xml .
COPY . .
CMD ["mvn", "spring-boot:run"]
