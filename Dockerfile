# Dockerfile multi-stage: optimizado para producccion en Render
# Etapa 1: Build con Maven y JDK completo
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copiar archivos de configuracion de Maven primero (mejor uso del cache)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Descargar dependencias (capa cacheable)
RUN ./mvnw dependency:go-offline -B

# Copiar codigo fuente y compilar
COPY src ./src

# Compilar saltando tests (los tests corren en CI, no en build de produccion)
RUN ./mvnw clean package -DskipTests -B

# Etapa 2: Runtime con JRE liviano
FROM eclipse-temurin:21-jre-alpine AS runtime

# Usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copiar solo el JAR final
COPY --from=builder /build/target/*.jar app.jar

# Variables de entorno por defecto (Render las sobrescribe)
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Render asigna PORT dinamicamente
EXPOSE 8080

# ENTRYPOINT con sh -c para que JAVA_OPTS se expanda
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
