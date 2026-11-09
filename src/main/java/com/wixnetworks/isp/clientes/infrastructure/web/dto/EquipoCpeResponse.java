package com.wixnetworks.isp.clientes.infrastructure.web.dto;

import com.wixnetworks.isp.clientes.domain.model.PropiedadEquipo;
import com.wixnetworks.isp.clientes.domain.model.TipoEquipo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para exponer un equipo CPE en respuestas del API.
 */

@Schema(description = "Equipo CPE instalado a un cliente")
public record EquipoCpeResponse(
        UUID id,
        TipoEquipo tipo,
        String marca,
        String modelo,
        String macAddres,
        PropiedadEquipo propiedad,
        LocalDate fechaInstalacion
){}