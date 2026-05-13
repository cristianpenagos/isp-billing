# ISP Billing — WIX Networks

Sistema de facturación para ISP construido con arquitectura hexagonal en Spring Boot.

## Stack tecnológico

- Java 21 (LTS)
- Spring Boot 3.3.x
- PostgreSQL 16
- Hibernate / Spring Data JPA
- Flyway (migraciones de BD)
- Spring Security + JWT
- JUnit 5 + Testcontainers + JaCoCo
- Docker + Docker Compose
- GitHub Actions (CI/CD)
- Desplegado en Render + Supabase

## Estructura del proyecto

El proyecto sigue una arquitectura hexagonal modular. Cada módulo de negocio tiene su propio núcleo de dominio, puertos y adaptadores.

```
src/main/java/com/wixnetworks/isp/
├── shared/          → Value objects y utilidades compartidas
├── clientes/        → Gestión de clientes y equipos CPE
├── planes/          → Catálogo de planes de internet
├── facturacion/     → Generación de facturas
├── pagos/           → Registro y aplicación de pagos
├── notificaciones/  → Envío de WhatsApp y otros canales
├── documentos/      → Generación de imágenes JPG de facturas
└── iam/             → Autenticación y autorización
```

Dentro de cada módulo:
- `domain/model/` — entidades y aggregates de dominio (Java puro)
- `domain/port/in/` — interfaces de casos de uso
- `domain/port/out/` — interfaces de repositorios y servicios externos
- `application/service/` — implementaciones de casos de uso
- `infrastructure/` — adaptadores REST, JPA, WhatsApp, etc.

## Prerrequisitos

- Java 21+
- Maven 3.9+
- Docker Desktop
- Git

## Ejecutar localmente

### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/isp-billing.git
cd isp-billing
```

### 2. Levantar PostgreSQL local
```bash
docker compose up -d postgres
```

### 3. Compilar y ejecutar
```bash
mvn clean install
mvn spring-boot:run
```

La aplicación quedará corriendo en `http://localhost:8080`.

### 4. Verificar que funciona
```bash
curl http://localhost:8080/api/v1/health
```

Respuesta esperada:
```json
{"status":"ok","service":"isp-billing","timestamp":"2026-05-12T..."}
```

### 5. Ver la documentación de la API
Abrir en el navegador: `http://localhost:8080/swagger-ui.html`

## Tests

```bash
# Tests unitarios
mvn test

# Tests de integración (requiere Docker corriendo)
mvn verify

# Reporte de cobertura
mvn verify jacoco:report
# Ver: target/site/jacoco/index.html
```

## Variables de entorno (producción)

Render las configura automáticamente desde el dashboard:

| Variable | Descripción | Ejemplo |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring | `prod` |
| `PORT` | Puerto HTTP (Render lo asigna) | `10000` |
| `DATABASE_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://...` |
| `DATABASE_USERNAME` | Usuario de BD | `postgres` |
| `DATABASE_PASSWORD` | Contraseña de BD | `***` |
| `JWT_SECRET` | Secreto para firmar JWT (256 bits) | `***` |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos | `https://app.vercel.app` |

## Despliegue

El despliegue se hace automáticamente al hacer push a la rama `main`:
1. GitHub Actions ejecuta tests y análisis de calidad
2. Si todo pasa, Render detecta el cambio y reconstruye la imagen
3. La nueva versión queda disponible en pocos minutos

## Documentación adicional

- `docs/architecture/` — diagramas y decisiones arquitectónicas
- `docs/api/` — documentación de la API (también disponible en `/swagger-ui.html`)

## Licencia

Privado — WIX Networks
