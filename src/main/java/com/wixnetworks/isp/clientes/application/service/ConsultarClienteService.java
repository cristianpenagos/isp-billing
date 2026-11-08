package com.wixnetworks.isp.clientes.application.service;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.EstadoCliente;
import com.wixnetworks.isp.clientes.domain.port.in.ConsultarClienteUseCase;
import com.wixnetworks.isp.clientes.domain.port.out.ClienteRepository;
import com.wixnetworks.isp.shared.domain.exception.RecursoNoEncontradoException;

import java.util.List;
import java.util.UUID;

public class ConsultarClienteService implements ConsultarClienteUseCase {

    private final ClienteRepository repository;

    public ConsultarClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente obtenerPorId(UUID id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe cliente con id " + id));
    }

    @Override
    public Cliente obtenerPorCodigoInterno(String codigoInterno) {
        return repository.buscarPorCodigoInterno(codigoInterno)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe cliente con codigo " + codigoInterno));
    }

    @Override
    public List<Cliente> listarTodos() {
        return repository.listarTodos();
    }

    @Override
    public List<Cliente> listarPorEstado(EstadoCliente estado) {
        return repository.listarPorEstado(estado);
    }
}
