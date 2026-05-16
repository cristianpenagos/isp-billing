package com.wixnetworks.isp.shared.domain.exception;

/**
 * Se lanza cuando se intenta acceder a un recurso que no existe.
 *
 * Ejemplos de uso:
 * - Buscar un cliente por ID que no esta registrado
 * - Buscar una factura por numero inexistente
 * - Buscar un plan que fue desactivado y eliminado
 *
 * Esta excepcion debe atraparse en la capa de infraestructura (adaptador web)
 * y traducirse a un HTTP 404 Not Found.
 */



public class RecursoNoEncontradoException extends DomainException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
