package com.wixnetworks.isp.clientes.infrastructure.web.mapper;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.EquipoCPE;
import com.wixnetworks.isp.clientes.infrastructure.web.dto.ClienteResponse;
import com.wixnetworks.isp.clientes.infrastructure.web.dto.EquipoCpeResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper entre el dominio y los DTOs web.
 *
 * Solo tiene metodos de salida (dominio → DTO) porque las entradas
 * (DTO → Command) se hacen directamente en el controller.
 */
@Component
public class ClienteWebMapper {

    public ClienteResponse aResponse(Cliente cliente) {
        List<EquipoCpeResponse> equiposResponse = cliente.equipos().stream()
                .map(this::aEquipoResponse)
                .toList();

        return new ClienteResponse(
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
                equiposResponse
        );
    }

    public EquipoCpeResponse aEquipoResponse(EquipoCPE equipo) {
        return new EquipoCpeResponse(
                equipo.id(),
                equipo.tipo(),
                equipo.marca(),
                equipo.modelo(),
                equipo.macAddress(),
                equipo.propiedad(),
                equipo.fechaInstalacion()
        );
    }
}
