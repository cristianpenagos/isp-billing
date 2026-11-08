package com.wixnetworks.isp.clientes.domain.port.in;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.EstadoCliente;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: consultar clientes existentes.
 *
 * Agrupa todas las operaciones de lectura (consultas).
 */

public interface ConsultarClienteUseCase{
    /**
     * Obtiene un cliente por su id interno.
     * Lanza RecursoNoEncontradoException si no existe.
     */
    Cliente obtenerPorId(UUID id);

    /**
     * Obtiene un cliente por su codigo interno (ej: "WX-00128").
     * Lanza RecursoNoEncontradoException si no existe.
     */
    Cliente obtenerPorCodigoInterno(String codigoInterno);

    /**
     * Lista todos los clientes del sistema.
     */
    List<Cliente> listarTodos();

    /**
     * Lista clientes en el estado indicado.
     */
    List<Cliente> listarPorEstado(EstadoCliente estado);
}