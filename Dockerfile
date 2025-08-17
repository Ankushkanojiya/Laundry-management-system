# =========================================================================
# BUILD STAGE: Build the app using Maven Wrapper & Java 21
# =========================================================================
# Use a specific, stable base image tag
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven wrapper files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw

# Download dependencies first to leverage Docker cache
# This layer is only invalidated if pom.xml changes
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application. 'clean' is good practice for consistency.
RUN ./mvnw clean package -DskipTests

# =========================================================================
# RUN STAGE: Smaller final image (JRE only)
# =========================================================================
# Use a specific, stable base image tag
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
