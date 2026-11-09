package com.wixnetworks.isp.clientes.infrastructure.web.dto;

import com.wixnetworks.isp.clientes.domain.model.EstadoCliente;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO para exponer un cliente completo en respuestas del API.
 *
 * Incluye los equipos CPE instalados.
 */
@Schema(description = "Datos completos de un cliente")
public record ClienteResponse(
        UUID id,
        String codigoInterno,
        String cedula,
        String nombreCompleto,
        String telefonoWhatsApp,
        String email,
        String descripcionDireccion,
        String barrio,
        String ciudad,
        EstadoCliente estado,
        LocalDate fechaRegistro,
        List<EquipoCpeResponse> equipos
) {}
