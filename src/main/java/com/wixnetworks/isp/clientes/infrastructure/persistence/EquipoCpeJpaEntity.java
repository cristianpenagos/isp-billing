package com.wixnetworks.isp.clientes.infrastructure.persistence;

import com.wixnetworks.isp.clientes.domain.model.PropiedadEquipo;
import com.wixnetworks.isp.clientes.domain.model.TipoEquipo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity para la tabla equipos_cpe.
 *
 * Esta clase es un "espejo" de EquipoCPE del dominio, pero adaptada
 * para que Hibernate la persista en PostgreSQL.
 *
 * NO contiene logica de dominio. Solo datos + anotaciones JPA.
 */
@Entity
@Table(name = "equipos_cpe")
public class EquipoCpeJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoEquipo tipo;

    @Column(name = "marca", length = 50)
    private String marca;

    @Column(name = "modelo", length = 50)
    private String modelo;

    @Column(name = "mac_address", length = 17)
    private String macAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "propiedad", nullable = false, length = 20)
    private PropiedadEquipo propiedad;

    @Column(name = "fecha_instalacion", nullable = false)
    private LocalDate fechaInstalacion;

    /**
     * Constructor sin argumentos requerido por JPA.
     */
    protected EquipoCpeJpaEntity() {
    }

    public EquipoCpeJpaEntity(UUID id, UUID clienteId, TipoEquipo tipo, String marca,
                              String modelo, String macAddress, PropiedadEquipo propiedad,
                              LocalDate fechaInstalacion) {
        this.id = id;
        this.clienteId = clienteId;
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.macAddress = macAddress;
        this.propiedad = propiedad;
        this.fechaInstalacion = fechaInstalacion;
    }

    public UUID getId() { return id; }
    public UUID getClienteId() { return clienteId; }
    public TipoEquipo getTipo() { return tipo; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getMacAddress() { return macAddress; }
    public PropiedadEquipo getPropiedad() { return propiedad; }
    public LocalDate getFechaInstalacion() { return fechaInstalacion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EquipoCpeJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
