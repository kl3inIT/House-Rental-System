# ------------ Build Stage ------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Chỉ copy pom.xml và resolve dependency trước để tận dụng cache layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Sau đó mới copy source code
COPY src ./src
RUN mvn clean package -DskipTests

# ------------ Run Stage ------------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Expose cổng chạy Spring Boot
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 