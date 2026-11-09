package com.wixnetworks.isp.clientes.infrastructure.persistence;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Clase base para tests de integracion que necesitan PostgreSQL.
 *
 * Doble modo de funcionamiento segun variable de entorno CI_POSTGRES_URL:
 *
 * - LOCAL (default): Testcontainers levanta un contenedor PostgreSQL 16 con
 *   Docker Desktop. Requiere Docker corriendo en la maquina.
 *
 * - CI (con variable CI_POSTGRES_URL): usa la URL externa provista por el
 *   pipeline. Util en GitHub Actions donde PostgreSQL se arranca como
 *   service container, sin necesidad de Docker-in-Docker.
 *
 * En ambos modos se corren las migraciones Flyway y Hibernate valida el
 * schema. El test de dominio es identico.
 */
public abstract class AbstractPostgresIT {

    private static final String CI_URL = System.getenv("CI_POSTGRES_URL");
    private static final String CI_USER = System.getenv("CI_POSTGRES_USER");
    private static final String CI_PASS = System.getenv("CI_POSTGRES_PASSWORD");

    private static final boolean MODO_CI = CI_URL != null && !CI_URL.isBlank();

    // Solo se inicializa si NO estamos en CI
    private static PostgreSQLContainer<?> postgres;

    static {
        if (!MODO_CI) {
            postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("isp_billing_test")
                    .withUsername("test")
                    .withPassword("test");
            postgres.start();
            // Shutdown hook para parar el contenedor al terminar
            Runtime.getRuntime().addShutdownHook(new Thread(postgres::stop));
        }
    }

    @DynamicPropertySource
    static void configurarPropiedades(DynamicPropertyRegistry registry) {
        if (MODO_CI) {
            registry.add("spring.datasource.url", () -> CI_URL);
            registry.add("spring.datasource.username", () -> CI_USER);
            registry.add("spring.datasource.password", () -> CI_PASS);
        } else {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
        }
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }
}