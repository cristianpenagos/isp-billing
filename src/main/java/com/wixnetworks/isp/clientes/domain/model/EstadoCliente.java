package com.wixnetworks.isp.clientes.domain.model;

import com.wixnetworks.isp.shared.domain.exception.ConflictoEstadoException;


/**
 * Estados posibles de un cliente en el sistema.
 *
 * Transiciones validas:
 * - ACTIVO -> SUSPENDIDO (por mora)
 * - ACTIVO -> RETIRADO (deja de ser cliente)
 * - SUSPENDIDO -> ACTIVO (al pagar)
 * - SUSPENDIDO -> RETIRADO (no paga y se retira)
 * - RETIRADO -> (terminal, no sale de aqui)
 *
 * Si se intenta una transicion invalida, se lanza ConflictoEstadoException.
 */

public enum EstadoCliente {

    ACTIVO,
    SUSPENDIDO,
    RETIRADO;

    /**
     * Verifica si la transicion al estado destino es valida desde el estado actual.
     * Lanza ConflictoEstadoException si no lo es.
     */
    public void validarTransicionA(EstadoCliente destino){
        if (this == destino){
            throw new ConflictoEstadoException("El Cliente ya se encuentra en estado "+ destino);
        }

        boolean valida = switch (this){
            case ACTIVO -> destino == SUSPENDIDO || destino == RETIRADO;
            case SUSPENDIDO -> destino == ACTIVO || destino == RETIRADO;
            case RETIRADO -> false;
        };

        if (!valida){
            throw new ConflictoEstadoException("No se puede cambiar de "+ this + " a "+destino);
        }
    }
    public boolean esTerminal(){
        return this == RETIRADO;
    }
    public boolean puedeRecibirFacturas(){
        return this == ACTIVO || this == SUSPENDIDO;
    }


}