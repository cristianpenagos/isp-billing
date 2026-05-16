package com.wixnetworks.isp.shared.domain.exception;
/**
 * Se lanza cuando una operacion entra en conflicto con el estado actual del sistema.
 *
 * Ejemplos de uso:
 * - Intentar registrar un cliente con una cedula ya existente
 * - Intentar pagar una factura que ya esta marcada como pagada
 * - Intentar cambiar el estado de un cliente retirado
 *
 * Esta excepcion debe atraparse en la capa de infraestructura (adaptador web)
 * y traducirse a un HTTP 409 Conflict.
 */
public class ConflictoEstadoException extends DomainException {

    public ConflictoEstadoException(String mensaje) {
        super(mensaje);
    }
}
