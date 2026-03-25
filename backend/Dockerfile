# ==== Build Stage ====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml first (cache dependencies)
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src/ src/
RUN mvn package -DskipTests -B

# ==== Runtime Stage ====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create non-root user
RUN groupadd -r posapp && useradd -r -g posapp posapp

COPY --from=build /app/target/*.jar app.jar

RUN chown posapp:posapp app.jar
USER posapp

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
