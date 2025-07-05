# Stage 1: Build the Spring Boot app
FROM gradle:8.4-jdk17 AS build
WORKDIR /app

# Copy source code 
COPY . .

# Build a fat JAR (executable)
RUN gradle bootJar --no-daemon

# Stage 2: Run the Spring Boot app
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Port that Spring Boot exposes (as defined in application.yml)
EXPOSE 8080

# Run the app
ENTRYPOINT [ "java", "-jar", "app.jar" ]