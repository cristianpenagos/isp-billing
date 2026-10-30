package com.wixnetworks.isp.clientes.domain.model;

import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;
import com.wixnetworks.isp.shared.domain.exception.RecursoNoEncontradoException;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;
import com.wixnetworks.isp.shared.domain.valueobject.Direccion;
import com.wixnetworks.isp.shared.domain.valueobject.Email;
import com.wixnetworks.isp.shared.domain.valueobject.Telefono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Aggregate root del modulo Clientes.
 *
 * Un Cliente agrupa:
 * - Datos de identidad (cedula, nombre, telefono, email)
 * - Direccion de instalacion
 * - Estado (ACTIVO/SUSPENDIDO/RETIRADO) con reglas de transicion
 * - Lista de equipos CPE instalados
 *
 * Reglas (invariantes del aggregate):
 * - El codigo interno es unico y sigue el patron WX-NNNNN (6 digitos minimo tras WX-)
 * - La cedula es unica en el sistema (lo valida el repositorio, no esta clase)
 * - Un cliente nace ACTIVO
 * - Un cliente RETIRADO no puede recibir equipos nuevos
 * - Para cambios de estado se usan los metodos de dominio (suspender/reactivar/retirar)
 */

public class Cliente{

    private final UUID id;
    private final String codigoInterno;
    private final Cedula cedula;
    private String nombreCompleto;
    private Telefono telefonoWhatsApp;
    private Email email;
    private Direccion direccion;
    private EstadoCliente estado;
    private final List<EquipoCPE> equipos;
    private final LocalDate fechaRegistro;

    private Cliente(UUID id, String codigoInterno, Cedula cedula, String nombreCompleto, Telefono telefonoWhatsApp, Email email, Direccion direccion, EstadoCliente estado, List<EquipoCPE> equipos, LocalDate fechaRegistro) {

        this.id = id;
        this.codigoInterno = codigoInterno;
        this.cedula = cedula;
        this.nombreCompleto = nombreCompleto;
        this.telefonoWhatsApp = telefonoWhatsApp;
        this.email = email;
        this.direccion = direccion;
        this.estado = estado;
        this.equipos = equipos;
        this.fechaRegistro = fechaRegistro;

    }

    /**
     * Registra un cliente nuevo. Nace ACTIVO, sin equipos, con fecha de registro hoy.
     */

    public static Cliente registrar(String codigoInterno, Cedula cedula,
                                    String nombreCompleto, Telefono telefonoWhatsApp,
                                    Email email, Direccion direccion) {
        validarCodigoInterno(codigoInterno);
        validarNombre(nombreCompleto);
        validarObligatorio(cedula, "cedula");
        validarObligatorio(telefonoWhatsApp, "telefono");
        validarObligatorio(email, "email");
        validarObligatorio(direccion, "direccion");

        return new Cliente(
                UUID.randomUUID(),
                codigoInterno.trim(),
                cedula,
                nombreCompleto.trim(),
                telefonoWhatsApp,
                email,
                direccion,
                EstadoCliente.ACTIVO,
                new ArrayList<>(),
                LocalDate.now()
        );
    }


    /**
     * Reconstruye un cliente desde la base de datos.
     * Usado por el adaptador JPA.
     */






}