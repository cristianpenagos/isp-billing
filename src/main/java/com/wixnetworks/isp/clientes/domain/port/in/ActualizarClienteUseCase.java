package com.wixnetworks.isp.clientes.domain.port.in;

import com.wixnetworks.isp.clientes.domain.model.Cliente;

import java.util.UUID;

/**
 * Caso de uso: actualizar datos de un cliente existente.
 *
 * Incluye tanto cambios de datos (nombre, telefono, direccion) como
 * cambios de estado (suspender, reactivar, retirar).
 */
public interface ActualizarClienteUseCase {

    Cliente cambiarDatos(CambiarDatosCommand comando);

    Cliente suspender(UUID clienteId);

    Cliente reactivar(UUID clienteId);

    Cliente retirar(UUID clienteId);

    /**
     * Command con los datos editables del cliente.
     * Si un campo viene null, se interpreta como "no cambiar".
     * Si viene con valor, se actualiza.
     */
    record CambiarDatosCommand(
            UUID clienteId,
            String nombreCompleto,
            String telefonoWhatsApp,
            String email,
            String descripcionDireccion,
            String barrio,
            String ciudad
    ) {}
}