package com.wixnetworks.isp.shared.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuracion de seguridad de la aplicacion.
 *
 * ESTADO ACTUAL (Fase 1 - Modulo Clientes):
 * Los endpoints de negocio estan abiertos para poder trabajar la API desde
 * Swagger, Postman y la UI mientras no exista autenticacion JWT.
 *
 * Endpoints publicos:
 *  - /api/v1/health y /actuator/health (Render y UptimeRobot)
 *  - /swagger-ui y /api-docs (documentacion interactiva)
 *  - /api/v1/** (negocio - temporalmente abierto)
 *
 * TODO FASE 7: implementar autenticacion JWT:
 *  - JWT HS256 60min + refresh 7 dias
 *  - BCrypt 12 rondas para passwords
 *  - Modulo IAM con usuarios y roles
 *  - Reemplazar .requestMatchers("/api/v1/**").permitAll() por:
 *      .requestMatchers("/api/v1/auth/**").permitAll()
 *      .requestMatchers("/api/v1/**").authenticated()
 */
@Configuration
public class SecurityConfig {

    @Value("${isp.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/health").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
                        // TODO FASE 7: estos endpoints deben requerir autenticacion JWT.
                        // Por ahora publicos para poder trabajar la API desde Swagger y Postman.
                        .requestMatchers("/api/v1/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}