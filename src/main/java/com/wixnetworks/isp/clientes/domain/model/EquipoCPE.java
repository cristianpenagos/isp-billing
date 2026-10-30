package com.wixnetworks.isp.clientes.domain.model;

import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity que representa un equipo CPE (Customer Premise Equipment)
 * instalado en la vivienda del cliente.
 *
 * A diferencia de un Value Object, EquipoCPE tiene IDENTIDAD PROPIA (id).
 * Dos equipos con los mismos datos (mismo modelo, misma MAC) son equipos
 * DISTINTOS si tienen ids distintos.
 *
 * Es una entity del aggregate Cliente: nadie modifica EquipoCPE directamente
 * desde fuera, todo pasa por el aggregate root.
 *
 * Reglas:
 * - id obligatorio (lo genera el sistema con UUID)
 * - tipo obligatorio
 * - marca y modelo opcionales pero recomendados
 * - propiedad obligatoria (quien es dueño del equipo)
 * - fechaInstalacion obligatoria
 * - macAddress opcional
 */
public final class EquipoCPE {

    private final UUID id;
    private final TipoEquipo tipo;
    private final String marca;
    private final String modelo;
    private final String macAddress;
    private final PropiedadEquipo propiedad;
    private final LocalDate fechaInstalacion;

    private EquipoCPE(UUID id, TipoEquipo tipo, String marca, String modelo,
                      String macAddress, PropiedadEquipo propiedad,
                      LocalDate fechaInstalacion) {
        this.id = id;
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.macAddress = macAddress;
        this.propiedad = propiedad;
        this.fechaInstalacion = fechaInstalacion;
    }

    /**
     * Crea un equipo nuevo con id generado automaticamente.
     */
    public static EquipoCPE nuevo(TipoEquipo tipo, String marca, String modelo,
                                  String macAddress, PropiedadEquipo propiedad,
                                  LocalDate fechaInstalacion) {
        validar(tipo, propiedad, fechaInstalacion);
        return new EquipoCPE(
                UUID.randomUUID(),
                tipo,
                limpiarOpcional(marca),
                limpiarOpcional(modelo),
                limpiarOpcional(macAddress),
                propiedad,
                fechaInstalacion
        );
    }

    /**
     * Reconstruye un equipo existente desde la base de datos.
     * Usado por el adaptador JPA, nunca por codigo de aplicacion.
     */
    public static EquipoCPE reconstruir(UUID id, TipoEquipo tipo, String marca,
                                        String modelo, String macAddress,
                                        PropiedadEquipo propiedad,
                                        LocalDate fechaInstalacion) {
        if (id == null) {
            throw new DatoInvalidoException("El id del equipo no puede ser nulo");
        }
        validar(tipo, propiedad, fechaInstalacion);
        return new EquipoCPE(
                id,
                tipo,
                limpiarOpcional(marca),
                limpiarOpcional(modelo),
                limpiarOpcional(macAddress),
                propiedad,
                fechaInstalacion
        );
    }

    private static void validar(TipoEquipo tipo, PropiedadEquipo propiedad,
                                LocalDate fechaInstalacion) {
        if (tipo == null) {
            throw new DatoInvalidoException("El tipo de equipo es obligatorio");
        }
        if (propiedad == null) {
            throw new DatoInvalidoException("La propiedad del equipo es obligatoria");
        }
        if (fechaInstalacion == null) {
            throw new DatoInvalidoException("La fecha de instalacion es obligatoria");
        }
        if (fechaInstalacion.isAfter(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha de instalacion no puede ser futura");
        }
    }

    private static String limpiarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    public UUID id() { return id; }
    public TipoEquipo tipo() { return tipo; }
    public String marca() { return marca; }
    public String modelo() { return modelo; }
    public String macAddress() { return macAddress; }
    public PropiedadEquipo propiedad() { return propiedad; }
    public LocalDate fechaInstalacion() { return fechaInstalacion; }

    /**
     * Dos equipos son iguales si tienen el mismo id.
     * Esto es la regla fundamental de una Entity: igualdad por identidad,
     * NO por valor como en los Value Objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EquipoCPE that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EquipoCPE[" + tipo + " " + marca + " " + modelo + " id=" + id + "]";
    }
}