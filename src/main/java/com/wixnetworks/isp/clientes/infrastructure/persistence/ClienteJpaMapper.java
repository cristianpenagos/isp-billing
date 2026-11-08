package com.wixnetworks.isp.clientes.infrastructure.persistence;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.EquipoCPE;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;
import com.wixnetworks.isp.shared.domain.valueobject.Direccion;
import com.wixnetworks.isp.shared.domain.valueobject.Email;
import com.wixnetworks.isp.shared.domain.valueobject.Telefono;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper que traduce entre el aggregate Cliente (dominio) y
 * ClienteJpaEntity (persistencia).
 *
 * Esta clase es el "puente" entre dos mundos:
 * - Dominio: Cliente usa value objects (Cedula, Email, etc.)
 * - JPA: ClienteJpaEntity usa Strings crudos
 *
 * Al cargar de BD: aEntity → aDominio (con factory reconstruir())
 * Al guardar: aDominio → aEntity
 */
@Component
public class ClienteJpaMapper {

    /**
     * Convierte un aggregate Cliente a su entity JPA correspondiente.
     */
    public ClienteJpaEntity aEntity(Cliente cliente) {
        List<EquipoCpeJpaEntity> equiposEntity = cliente.equipos().stream()
                .map(equipo -> aEquipoEntity(equipo, cliente.id()))
                .toList();

        return new ClienteJpaEntity(
                cliente.id(),
                cliente.codigoInterno(),
                cliente.cedula().valor(),
                cliente.nombreCompleto(),
                cliente.telefonoWhatsApp().valor(),
                cliente.email().valor(),
                cliente.direccion().descripcionLibre(),
                cliente.direccion().barrio().orElse(null),
                cliente.direccion().ciudad(),
                cliente.estado(),
                cliente.fechaRegistro(),
                equiposEntity
        );
    }

    /**
     * Convierte una entity JPA a su aggregate Cliente correspondiente.
     * Usa Cliente.reconstruir() porque viene de persistencia.
     */
    public Cliente aDominio(ClienteJpaEntity entity) {
        Direccion direccion = construirDireccion(entity);

        List<EquipoCPE> equipos = entity.getEquipos().stream()
                .map(this::aEquipoDominio)
                .toList();

        return Cliente.reconstruir(
                entity.getId(),
                entity.getCodigoInterno(),
                Cedula.de(entity.getCedula()),
                entity.getNombreCompleto(),
                Telefono.de(entity.getTelefonoWhatsapp()),
                Email.de(entity.getEmail()),
                direccion,
                entity.getEstado(),
                equipos,
                entity.getFechaRegistro()
        );
    }

    private EquipoCpeJpaEntity aEquipoEntity(EquipoCPE equipo, java.util.UUID clienteId) {
        return new EquipoCpeJpaEntity(
                equipo.id(),
                clienteId,
                equipo.tipo(),
                equipo.marca(),
                equipo.modelo(),
                equipo.macAddress(),
                equipo.propiedad(),
                equipo.fechaInstalacion()
        );
    }

    private EquipoCPE aEquipoDominio(EquipoCpeJpaEntity entity) {
        return EquipoCPE.reconstruir(
                entity.getId(),
                entity.getTipo(),
                entity.getMarca(),
                entity.getModelo(),
                entity.getMacAddress(),
                entity.getPropiedad(),
                entity.getFechaInstalacion()
        );
    }

    private Direccion construirDireccion(ClienteJpaEntity entity) {
        if (entity.getDireccionBarrio() == null) {
            return Direccion.sinBarrio(
                    entity.getDireccionDescripcion(),
                    entity.getDireccionCiudad()
            );
        }
        return Direccion.de(
                entity.getDireccionDescripcion(),
                entity.getDireccionBarrio(),
                entity.getDireccionCiudad()
        );
    }
}
