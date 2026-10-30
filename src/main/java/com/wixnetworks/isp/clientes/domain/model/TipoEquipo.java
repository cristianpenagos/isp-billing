package com.wixnetworks.isp.clientes.domain.model;

/**
 * Tipos de equipos CPE (Customer Premise Equipment) que se instalan
 * en la vivienda del cliente para prestar el servicio de internet.
 */

public enum TipoEquipo{
    Router,
    ONU,
    MIKROTIK,
    ANTENA,
    TVBOX,
    MESH,
    OTRO
}