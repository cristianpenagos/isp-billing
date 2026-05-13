package com.wixnetworks.isp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Test de humo: valida que el contexto de Spring carga correctamente.
 *
 * Usa Testcontainers para levantar un PostgreSQL real en un contenedor Docker,
 * lo cual es mas confiable que H2 porque usamos features de PostgreSQL
 * (UUIDs, JSONB, etc.) que H2 no soporta.
 *
 * Para que este test funcione, Docker Desktop debe estar corriendo en tu maquina.
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class IspBillingApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("isp_billing_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void contextLoads() {
        // Si llegamos aqui sin excepciones, Spring esta correctamente configurado.
    }
}
