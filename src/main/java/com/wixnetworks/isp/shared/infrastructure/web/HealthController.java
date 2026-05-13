package com.wixnetworks.isp.shared.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Endpoint publico de health check.
 *
 * Sirve para:
 * - Verificar que la aplicacion esta corriendo (utiliza UptimeRobot para evitar cold starts)
 * - Validar el deployment despues de un release
 * - Punto de entrada para healthchecks de Render
 */
@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Endpoints de salud de la aplicacion")
public class HealthController {

    @GetMapping
    @Operation(summary = "Verifica que la aplicacion esta viva")
    public Map<String, Object> health() {
        return Map.of(
            "status", "ok",
            "service", "isp-billing",
            "timestamp", Instant.now().toString()
        );
    }
}
