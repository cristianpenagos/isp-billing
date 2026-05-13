# Dockerfile multi-stage: optimizado para producccion en Render
# Etapa 1: Build con imagen oficial de Maven (incluye Maven 3.9 + JDK 21)
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copiar pom.xml primero para aprovechar el cache de Docker
COPY pom.xml .

# Descargar dependencias (capa cacheable). Si el pom.xml no cambia,
# esta capa se reusa en builds futuros y ahorra varios minutos.
RUN mvn dependency:go-offline -B

# Copiar codigo fuente y compilar
COPY src ./src

# Compilar saltando tests (los tests corren en CI, no en build de produccion)
RUN mvn clean package -DskipTests -B

# Etapa 2: Runtime con JRE liviano (imagen final mas pequena)
FROM eclipse-temurin:21-jre-alpine AS runtime

# Usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copiar solo el JAR final desde la etapa de build
COPY --from=builder /build/target/*.jar app.jar

# Variables de entorno por defecto (Render las sobrescribe)
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Render asigna PORT dinamicamente
EXPOSE 8080

# ENTRYPOINT con sh -c para que JAVA_OPTS se expanda
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]