# ──────────────────────────────────────────────────────────────────────────────
# Stage 1 — Builder
# ──────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copiar wrapper y archivos de configuración antes del código fuente
# para aprovechar el caché de capas de Docker.
COPY gradlew .
COPY gradle/ gradle/
RUN chmod +x gradlew

COPY build.gradle settings.gradle ./

# Precarga de dependencias (capa cacheada mientras no cambie build.gradle)
RUN ./gradlew --no-daemon dependencies

# Copiar fuentes y construir el fat-JAR (sin tests — ya se ejecutaron en CI)
COPY src/ src/
RUN ./gradlew --no-daemon bootJar -x test

# ──────────────────────────────────────────────────────────────────────────────
# Stage 2 — Runtime
# ──────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario sin privilegios (OWASP A05 — Security Misconfiguration)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /workspace/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
