# ── Stage 1: build ───────────────────────────────────────────────────
FROM eclipse-temurin:20-jdk-jammy AS builder
WORKDIR /app

# Copiar wrapper y POM primero para cachear dependencias
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copiar fuentes y construir el JAR
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# ── Stage 2: runtime ─────────────────────────────────────────────────
FROM eclipse-temurin:20-jre-jammy
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
