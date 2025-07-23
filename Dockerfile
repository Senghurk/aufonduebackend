# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:24-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Add wait-for-it script to handle database connection delays
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]
#ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/app.jar"]
# Use wait-for-it to check database connection
ENTRYPOINT ["/wait-for-it.sh", "aufondue-db.postgres.database.azure.com:5432", "--timeout=30", "--", "java", "-jar", "-Dspring.profiles.active=prod", "/app/app.jar"]