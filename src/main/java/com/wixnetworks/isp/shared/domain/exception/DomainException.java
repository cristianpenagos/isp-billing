package com.wixnetworks.isp.shared.domain.exception;

/**
 * Excepcion base para todas las excepciones del dominio.
 *
 * Las excepciones de dominio representan violaciones de invariantes o reglas
 * de negocio detectadas en el nucleo de la aplicacion. Son distintas de las
 * excepciones de infraestructura (BD, red, etc.).
 *
 * Esta clase es abstracta para forzar a los desarrolladores a crear excepciones
 * especificas (DatoInvalidoException, RecursoNoEncontradoException, etc.) en
 * lugar de lanzar DomainException directamente.
 */



public abstract class DomainException extends RuntimeException {

    protected DomainException(String mensaje) {
        super(mensaje);
    }
    protected DomainException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}