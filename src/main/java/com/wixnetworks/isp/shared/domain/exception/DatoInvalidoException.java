package com.wixnetworks.isp.shared.domain.exception;


/**
 * Se lanza cuando un valor de entrada no cumple las reglas de validacion del dominio.
 *
 * Ejemplos de uso:
 * - Cedula con formato incorrecto
 * - Email mal formado
 * - Telefono que no respeta el formato E.164
 * - Dinero con valor negativo
 *
 * Esta excepcion debe atraparse en la capa de infraestructura (adaptador web)
 * y traducirse a un HTTP 400 Bad Request.
 */



public class DatoInvalidoException extends DomainException {

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}