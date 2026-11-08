package com.wixnetworks.isp.clientes.application.service;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.EquipoCPE;
import com.wixnetworks.isp.clientes.domain.port.in.GestionarEquiposUseCase;
import com.wixnetworks.isp.clientes.domain.port.out.ClienteRepository;
import com.wixnetworks.isp.shared.domain.exception.RecursoNoEncontradoException;

import java.util.UUID;

public class GestionarEquiposService implements GestionarEquiposUseCase {

    private final ClienteRepository repository;

    public GestionarEquiposService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente agregarEquipo(AgregarEquipoCommand comando) {
        Cliente cliente = obtenerCliente(comando.clienteId());

        EquipoCPE equipo = EquipoCPE.nuevo(
                comando.tipo(),
                comando.marca(),
                comando.modelo(),
                comando.macAddress(),
                comando.propiedad(),
                comando.fechaInstalacion()
        );

        cliente.agregarEquipo(equipo);
        return repository.guardar(cliente);
    }

    @Override
    public Cliente retirarEquipo(UUID clienteId, UUID equipoId) {
        Cliente cliente = obtenerCliente(clienteId);
        cliente.retirarEquipo(equipoId);
        return repository.guardar(cliente);
    }

    private Cliente obtenerCliente(UUID id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe cliente con id " + id));
    }
}
