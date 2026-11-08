package com.wixnetworks.isp.clientes.domain.port.out;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.EstadoCliente;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida: define QUE necesita el dominio del mundo exterior
 * para persistir y recuperar clientes.
 *
 * NO dice COMO se hace (PostgreSQL, MongoDB, archivo). Esa decision
 * la toma el adaptador JPA en la capa de infraestructura.
 *
 * El dominio solo conoce esta interfaz. Nunca una implementacion concreta.
 */
public interface ClienteRepository {

    /**
     * Guarda un cliente nuevo o actualiza uno existente.
     * El id del cliente debe estar presente (lo asigna Cliente.registrar()).
     *
     * @return el mismo cliente guardado, para encadenar operaciones
     */
    Cliente guardar(Cliente cliente);

    /**
     * Busca un cliente por su id interno (UUID).
     */
    Optional<Cliente> buscarPorId(UUID id);

    /**
     * Busca un cliente por su codigo interno de WIX (ej: "WX-00128").
     */
    Optional<Cliente> buscarPorCodigoInterno(String codigoInterno);

    /**
     * Busca un cliente por su cedula.
     * La cedula debe ser unica en el sistema.
     */
    Optional<Cliente> buscarPorCedula(Cedula cedula);

    /**
     * Lista todos los clientes.
     * En el futuro aceptara paginacion y filtros.
     */
    List<Cliente> listarTodos();

    /**
     * Lista clientes filtrados por estado.
     * Util para generar facturas solo a los que pueden recibirlas.
     */
    List<Cliente> listarPorEstado(EstadoCliente estado);

    /**
     * Verifica si existe un cliente con esa cedula.
     * Mas eficiente que buscarPorCedula cuando solo interesa saber si existe.
     */
    boolean existeConCedula(Cedula cedula);

    /**
     * Verifica si existe un cliente con ese codigo interno.
     */
    boolean existeConCodigoInterno(String codigoInterno);
}
