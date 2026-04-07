# -----------------------------------------------------------------------------
# Stage 1 - Builder
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copiar wrapper y archivos de configuracion antes del codigo fuente para
# aprovechar el cache de capas de Docker.
COPY gradlew .
COPY gradle/ gradle/
RUN chmod +x gradlew

COPY build.gradle settings.gradle ./

# Precarga de dependencias (capa cacheada mientras no cambie el build).
RUN ./gradlew --no-daemon --console=plain dependencies

# Copiar fuentes y construir el fat-JAR de Spring Boot.
COPY src/ src/
RUN ./gradlew --no-daemon --console=plain bootJar -x test \
    && find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -exec cp {} app.jar \; \
    && test -f app.jar

# -----------------------------------------------------------------------------
# Stage 2 - Runtime
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

ENV SERVER_PORT=8080

# El servicio lee estas variables en runtime:
# DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET, ORS_API_KEY,
# JPA_DDL_AUTO y JPA_SHOW_SQL.

# Usuario sin privilegios (OWASP A05 - Security Misconfiguration)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder --chown=appuser:appgroup /workspace/app.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
