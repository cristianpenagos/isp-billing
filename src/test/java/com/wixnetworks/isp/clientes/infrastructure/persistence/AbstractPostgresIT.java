package com.wixnetworks.isp.clientes.infrastructure.persistence;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Clase base para tests de integracion que necesitan PostgreSQL.
 *
 * Testcontainers levanta un contenedor Docker con PostgreSQL 16, lo
 * configura con Spring Boot, corre las migraciones de Flyway, y lo
 * destruye al terminar los tests.
 *
 * Requiere Docker Desktop corriendo en la maquina donde se ejecutan
 * los tests (local) o en el runner de CI.
 */
@Testcontainers
public abstract class AbstractPostgresIT {

    @Container
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("isp_billing_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configurarPropiedades(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }
}