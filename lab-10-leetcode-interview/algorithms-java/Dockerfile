FROM maven:3.9-eclipse-temurin-21-alpine
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY . .
CMD ["mvn", "test", "--no-transfer-progress"]
