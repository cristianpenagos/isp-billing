package com.wixnetworks.isp.clientes.domain.port.in;

import com.wixnetworks.isp.clientes.domain.model.Cliente;

/**
 * Caso de uso: registrar un cliente nuevo en el sistema.
 *
 * Reglas:
 * - La cedula debe ser unica (no puede haber dos clientes con la misma)
 * - El codigo interno debe ser unico
 * - El cliente nace en estado ACTIVO
 */
public interface RegistrarClienteUseCase{
    Cliente ejecutar(RegistrarClienteCommand comando);

    /**
     * Command: agrupa los datos crudos que llegan desde el controller.
     * Son Strings porque vienen de JSON/formularios. El service los
     * convierte a value objects (que validan).
     */
    record RegistrarClienteCommand(
            String codigoInterno,
            String cedula,
            String nombreCompleto,
            String telefonoWhatsApp,
            String email,
            String descripcionDireccion,
            String barrio,
            String ciudad
    ){}
}