package com.wixnetworks.isp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion de facturacion para WIX Networks ISP.
 *
 * Esta aplicacion sigue una arquitectura hexagonal modular:
 * - Cada modulo (clientes, planes, facturacion, pagos, etc.) tiene su propio
 *   nucleo de dominio, puertos y adaptadores.
 * - El dominio es Java puro, sin dependencias de Spring ni Hibernate.
 * - Los adaptadores de infraestructura conectan el dominio con el mundo exterior.
 */
@SpringBootApplication
public class IspBillingApplication {

    public static void main(String[] args) {
        SpringApplication.run(IspBillingApplication.class, args);
    }
}
