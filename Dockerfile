# =========================================================================
# BUILD STAGE: Build the app using Maven Wrapper & Java 21
# =========================================================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven wrapper files (keeps your specific Maven version)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Pre-download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline

# Copy source code and build the JAR
COPY src ./src
RUN ./mvnw package -DskipTests


# =========================================================================
# RUN STAGE: Smaller final image (JRE only)
# =========================================================================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/Laundry-Management-System-0.0.1-SNAPSHOT.jar app.jar

# Expose app port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
