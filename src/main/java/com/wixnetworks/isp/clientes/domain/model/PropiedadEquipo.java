package com.wixnetworks.isp.clientes.domain.model;

/**
 * Indica quien es el propietario del equipo CPE instalado en el cliente.
 *
 * - PROPIO: el cliente lo compro y es suyo
 * - COMODATO: WIX Networks lo prestó sin costo; se devuelve al retirarse
 * - ALQUILADO: WIX lo alquila por un valor mensual
 */

public enum PropiedadEquipo {

    PROPIO,
    COMODATO,
    ALQUILADO
}