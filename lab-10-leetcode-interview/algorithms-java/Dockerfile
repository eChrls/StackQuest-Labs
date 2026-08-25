FROM maven:3.9.11-eclipse-temurin-21
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY . .
CMD ["mvn", "test", "--no-transfer-progress"]
