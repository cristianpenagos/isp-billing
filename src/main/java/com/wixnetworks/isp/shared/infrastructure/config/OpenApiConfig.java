package com.wixnetworks.isp.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de la documentacion OpenAPI/Swagger.
 *
 * Una vez la app esta corriendo, la documentacion interactiva estara disponible en:
 * - http://localhost:8080/swagger-ui.html (desarrollo)
 * - https://tu-app.onrender.com/swagger-ui.html (produccion)
 *
 * Desde esta interfaz se pueden probar todos los endpoints directamente.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ispOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ISP Billing API")
                .description("API REST para gestion de facturacion de WIX Networks ISP")
                .version("v0.0.1-SNAPSHOT")
                .contact(new Contact()
                    .name("WIX Networks")
                    .email("soporte@wixnetworks.example"))
                .license(new License()
                    .name("Privada")));
    }
}
