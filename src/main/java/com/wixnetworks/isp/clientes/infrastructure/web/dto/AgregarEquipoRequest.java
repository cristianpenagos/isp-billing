package com.wixnetworks.isp.clientes.infrastructure.web.dto;

import com.wixnetworks.isp.clientes.domain.model.PropiedadEquipo;
import com.wixnetworks.isp.clientes.domain.model.TipoEquipo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO para agregar un equipo CPE a un cliente existente.
 */
@Schema(description = "Datos para agregar un equipo CPE a un cliente")
public record AgregarEquipoRequest(

        @Schema(description = "Tipo de equipo", example = "ROUTER")
        @NotNull(message = "El tipo es obligatorio")
        TipoEquipo tipo,

        @Schema(description = "Marca", example = "TP-Link")
        @Size(max = 50)
        String marca,

        @Schema(description = "Modelo", example = "Archer C6")
        @Size(max = 50)
        String modelo,

        @Schema(description = "Direccion MAC", example = "AA:BB:CC:DD:EE:FF")
        @Size(max = 17)
        String macAddress,

        @Schema(description = "Propiedad del equipo", example = "COMODATO")
        @NotNull(message = "La propiedad es obligatoria")
        PropiedadEquipo propiedad,

        @Schema(description = "Fecha de instalacion", example = "2026-11-08")
        @NotNull(message = "La fecha de instalacion es obligatoria")
        LocalDate fechaInstalacion
) {}
