package com.wixnetworks.isp.clientes.infrastructure.persistence;

import com.wixnetworks.isp.clientes.domain.model.EstadoCliente;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity para la tabla clientes.
 *
 * Espejo del aggregate Cliente, adaptado para persistencia con Hibernate.
 *
 * Nota sobre la direccion: se guarda APLANADA en 3 columnas, no en una
 * tabla aparte. Esto porque Direccion es un value object que vive con
 * el aggregate, no tiene identidad propia.
 */
@Entity
@Table(name = "clientes")
public class ClienteJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "codigo_interno", nullable = false, unique = true, length = 20)
    private String codigoInterno;

    @Column(name = "cedula", nullable = false, unique = true, length = 10)
    private String cedula;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(name = "telefono_whatsapp", nullable = false, length = 20)
    private String telefonoWhatsapp;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Column(name = "direccion_descripcion", nullable = false, length = 200)
    private String direccionDescripcion;

    @Column(name = "direccion_barrio", length = 100)
    private String direccionBarrio;

    @Column(name = "direccion_ciudad", nullable = false, length = 100)
    private String direccionCiudad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCliente estado;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "cliente_id")
    private List<EquipoCpeJpaEntity> equipos = new ArrayList<>();

    protected ClienteJpaEntity() {
    }

    public ClienteJpaEntity(UUID id, String codigoInterno, String cedula,
                            String nombreCompleto, String telefonoWhatsapp, String email,
                            String direccionDescripcion, String direccionBarrio,
                            String direccionCiudad, EstadoCliente estado,
                            LocalDate fechaRegistro, List<EquipoCpeJpaEntity> equipos) {
        this.id = id;
        this.codigoInterno = codigoInterno;
        this.cedula = cedula;
        this.nombreCompleto = nombreCompleto;
        this.telefonoWhatsapp = telefonoWhatsapp;
        this.email = email;
        this.direccionDescripcion = direccionDescripcion;
        this.direccionBarrio = direccionBarrio;
        this.direccionCiudad = direccionCiudad;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.equipos = equipos != null ? new ArrayList<>(equipos) : new ArrayList<>();
    }

    public UUID getId() { return id; }
    public String getCodigoInterno() { return codigoInterno; }
    public String getCedula() { return cedula; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getTelefonoWhatsapp() { return telefonoWhatsapp; }
    public String getEmail() { return email; }
    public String getDireccionDescripcion() { return direccionDescripcion; }
    public String getDireccionBarrio() { return direccionBarrio; }
    public String getDireccionCiudad() { return direccionCiudad; }
    public EstadoCliente getEstado() { return estado; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public List<EquipoCpeJpaEntity> getEquipos() { return equipos; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClienteJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
