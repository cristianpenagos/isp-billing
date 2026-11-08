package com.wixnetworks.isp.clientes.domain.port.in;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.PropiedadEquipo;
import com.wixnetworks.isp.clientes.domain.model.TipoEquipo;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Caso de uso: gestionar los equipos CPE instalados en un cliente.
 */
public interface GestionarEquiposUseCase {

    Cliente agregarEquipo(AgregarEquipoCommand comando);

    Cliente retirarEquipo(UUID clienteId, UUID equipoId);

    record AgregarEquipoCommand(
            UUID clienteId,
            TipoEquipo tipo,
            String marca,
            String modelo,
            String macAddress,
            PropiedadEquipo propiedad,
            LocalDate fechaInstalacion
    ) {}
}
