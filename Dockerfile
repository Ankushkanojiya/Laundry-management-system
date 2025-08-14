# =========================================================================
# BUILD STAGE: This stage builds the application using Maven and Java 21.
# =========================================================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper files. This ensures we use the project's specific Maven version.
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download all project dependencies. This layer is cached, so it only re-runs if pom.xml changes.
RUN ./mvnw dependency:go-offline

# Copy the rest of your source code
COPY src ./src

# Package the application, skipping the tests. This creates the JAR file.
RUN ./mvnw package -DskipTests


# =========================================================================
# RUN STAGE: This is the final, small image that just runs the application.
# =========================================================================
FROM eclipse-temurin:21-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the JAR file from the 'build' stage and rename it to the simple 'app.jar'
COPY --from=build /app/target/Laundry-Management-System-0.0.1-SNAPSHOT.jar app.jar

# Tell Docker that the container will listen on this port
EXPOSE 8080

# This is the command that will run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]