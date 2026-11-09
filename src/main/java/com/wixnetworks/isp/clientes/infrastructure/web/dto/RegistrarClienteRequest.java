package com.wixnetworks.isp.clientes.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para la peticion de registrar un cliente nuevo.
 *
 * Las anotaciones de validacion (@NotBlank, @Pattern, @Size) se activan
 * con @Valid en el controller. Si algun campo no cumple, Spring devuelve
 * 400 Bad Request automaticamente.
 */
@Schema(description = "Datos para registrar un cliente nuevo")
public record RegistrarClienteRequest(

        @Schema(description = "Codigo interno formato WX-NNNNN", example = "WX-00128")
        @NotBlank(message = "El codigo interno es obligatorio")
        @Pattern(regexp = "^WX-\\d{5}$", message = "El codigo interno debe tener formato WX-NNNNN")
        String codigoInterno,

        @Schema(description = "Cedula del cliente", example = "12345678")
        @NotBlank(message = "La cedula es obligatoria")
        @Pattern(regexp = "^\\d{6,10}$", message = "La cedula debe tener entre 6 y 10 digitos")
        String cedula,

        @Schema(description = "Nombre completo", example = "Juan Perez")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
        String nombreCompleto,

        @Schema(description = "Telefono WhatsApp formato E.164 colombiano", example = "3147748687")
        @NotBlank(message = "El telefono es obligatorio")
        String telefonoWhatsApp,

        @Schema(description = "Correo electronico", example = "juan@ejemplo.com")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @Schema(description = "Descripcion de la direccion", example = "Calle 10 # 20-30")
        @NotBlank(message = "La descripcion de la direccion es obligatoria")
        @Size(max = 200)
        String descripcionDireccion,

        @Schema(description = "Barrio (opcional)", example = "Laureles")
        @Size(max = 100)
        String barrio,

        @Schema(description = "Ciudad", example = "Medellin")
        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 100)
        String ciudad
) {}
