package com.wixnetworks.isp.clientes.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar datos de un cliente existente.
 *
 * Todos los campos son opcionales: si vienen null o vacios, no se
 * actualizan. Si vienen con valor, se actualizan.
 */
@Schema(description = "Datos editables de un cliente. Campos null o vacios no se modifican")
public record ActualizarClienteRequest(

        @Schema(description = "Nombre completo", example = "Juan Carlos Perez")
        @Size(min = 3, max = 150)
        String nombreCompleto,

        @Schema(description = "Telefono WhatsApp", example = "3109876543")
        String telefonoWhatsApp,

        @Schema(description = "Correo electronico", example = "juan.nuevo@ejemplo.com")
        String email,

        @Schema(description = "Descripcion de la direccion", example = "Carrera 50 # 10-20")
        @Size(max = 200)
        String descripcionDireccion,

        @Schema(description = "Barrio", example = "El Poblado")
        @Size(max = 100)
        String barrio,

        @Schema(description = "Ciudad", example = "Medellin")
        @Size(max = 100)
        String ciudad
) {}
