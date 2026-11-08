package com.wixnetworks.isp.clientes.domain.model;

import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;
import com.wixnetworks.isp.shared.domain.exception.RecursoNoEncontradoException;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;
import com.wixnetworks.isp.shared.domain.valueobject.Direccion;
import com.wixnetworks.isp.shared.domain.valueobject.Email;
import com.wixnetworks.isp.shared.domain.valueobject.Telefono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
public final class Cliente {

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

    private Cliente(UUID id, String codigoInterno, Cedula cedula, String nombreCompleto,
                    Telefono telefonoWhatsApp, Email email, Direccion direccion,
                    EstadoCliente estado, List<EquipoCPE> equipos, LocalDate fechaRegistro) {
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
    public static Cliente reconstruir(UUID id, String codigoInterno, Cedula cedula,
                                      String nombreCompleto, Telefono telefonoWhatsApp,
                                      Email email, Direccion direccion,
                                      EstadoCliente estado, List<EquipoCPE> equipos,
                                      LocalDate fechaRegistro) {
        if (id == null) {
            throw new DatoInvalidoException("El id del cliente no puede ser nulo");
        }
        validarCodigoInterno(codigoInterno);
        validarNombre(nombreCompleto);
        validarObligatorio(cedula, "cedula");
        validarObligatorio(telefonoWhatsApp, "telefono");
        validarObligatorio(email, "email");
        validarObligatorio(direccion, "direccion");
        validarObligatorio(estado, "estado");
        validarObligatorio(fechaRegistro, "fechaRegistro");

        return new Cliente(
                id,
                codigoInterno.trim(),
                cedula,
                nombreCompleto.trim(),
                telefonoWhatsApp,
                email,
                direccion,
                estado,
                new ArrayList<>(equipos != null ? equipos : Collections.emptyList()),
                fechaRegistro
        );
    }

    // ========== Cambios de estado ==========

    public void suspender() {
        estado.validarTransicionA(EstadoCliente.SUSPENDIDO);
        this.estado = EstadoCliente.SUSPENDIDO;
    }

    public void reactivar() {
        estado.validarTransicionA(EstadoCliente.ACTIVO);
        this.estado = EstadoCliente.ACTIVO;
    }

    public void retirar() {
        estado.validarTransicionA(EstadoCliente.RETIRADO);
        this.estado = EstadoCliente.RETIRADO;
    }

    // ========== Cambios de datos ==========

    public void cambiarNombre(String nuevoNombre) {
        validarNombre(nuevoNombre);
        this.nombreCompleto = nuevoNombre.trim();
    }

    public void cambiarTelefono(Telefono nuevoTelefono) {
        validarObligatorio(nuevoTelefono, "telefono");
        this.telefonoWhatsApp = nuevoTelefono;
    }

    public void cambiarEmail(Email nuevoEmail) {
        validarObligatorio(nuevoEmail, "email");
        this.email = nuevoEmail;
    }

    public void cambiarDireccion(Direccion nuevaDireccion) {
        validarObligatorio(nuevaDireccion, "direccion");
        this.direccion = nuevaDireccion;
    }

    // ========== Gestion de equipos ==========

    public void agregarEquipo(EquipoCPE equipo) {
        validarObligatorio(equipo, "equipo");
        if (estado == EstadoCliente.RETIRADO) {
            throw new DatoInvalidoException(
                    "No se pueden agregar equipos a un cliente retirado");
        }
        if (equipos.contains(equipo)) {
            throw new DatoInvalidoException(
                    "El equipo ya esta registrado en este cliente");
        }
        equipos.add(equipo);
    }

    public void retirarEquipo(UUID equipoId) {
        EquipoCPE encontrado = equipos.stream()
                .filter(e -> e.id().equals(equipoId))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el equipo con id " + equipoId + " en este cliente"));
        equipos.remove(encontrado);
    }

    // ========== Validaciones privadas ==========

    private static void validarCodigoInterno(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new DatoInvalidoException("El codigo interno es obligatorio");
        }
        if (!codigo.trim().matches("WX-\\d{5,}")) {
            throw new DatoInvalidoException(
                    "El codigo interno debe seguir el patron WX-NNNNN (ej: WX-00128)");
        }
    }

    private static void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre completo es obligatorio");
        }
        if (nombre.trim().length() < 3) {
            throw new DatoInvalidoException("El nombre debe tener al menos 3 caracteres");
        }
        if (nombre.trim().length() > 150) {
            throw new DatoInvalidoException("El nombre no puede tener mas de 150 caracteres");
        }
    }

    private static void validarObligatorio(Object valor, String campo) {
        if (valor == null) {
            throw new DatoInvalidoException("El campo " + campo + " es obligatorio");
        }
    }

    // ========== Accesores ==========

    public UUID id() { return id; }
    public String codigoInterno() { return codigoInterno; }
    public Cedula cedula() { return cedula; }
    public String nombreCompleto() { return nombreCompleto; }
    public Telefono telefonoWhatsApp() { return telefonoWhatsApp; }
    public Email email() { return email; }
    public Direccion direccion() { return direccion; }
    public EstadoCliente estado() { return estado; }
    public LocalDate fechaRegistro() { return fechaRegistro; }

    /**
     * Retorna una copia inmutable de la lista de equipos.
     * NUNCA retorna la lista interna directamente para que nadie fuera del
     * aggregate pueda modificarla saltandose las reglas.
     */
    public List<EquipoCPE> equipos() {
        return Collections.unmodifiableList(equipos);
    }

    public Optional<EquipoCPE> buscarEquipo(UUID equipoId) {
        return equipos.stream()
                .filter(e -> e.id().equals(equipoId))
                .findFirst();
    }

    public boolean tieneEquipos() {
        return !equipos.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cliente[" + codigoInterno + " - " + nombreCompleto + " (" + estado + ")]";
    }
}