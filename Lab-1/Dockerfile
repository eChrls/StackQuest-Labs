FROM maven:3.9.9-eclipse-temurin-21
WORKDIR /workspace
COPY pom.xml ./
RUN mvn -q dependency:go-offline || true
COPY src ./src
EXPOSE 8080 5005
CMD ["mvn", "spring-boot:run"]
