package com.wixnetworks.isp.clientes.application.service;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.port.in.ActualizarClienteUseCase;
import com.wixnetworks.isp.clientes.domain.port.out.ClienteRepository;
import com.wixnetworks.isp.shared.domain.exception.RecursoNoEncontradoException;
import com.wixnetworks.isp.shared.domain.valueobject.Direccion;
import com.wixnetworks.isp.shared.domain.valueobject.Email;
import com.wixnetworks.isp.shared.domain.valueobject.Telefono;

import java.util.UUID;

public class ActualizarClienteService implements ActualizarClienteUseCase {

    private final ClienteRepository repository;

    public ActualizarClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente cambiarDatos(CambiarDatosCommand comando) {
        Cliente cliente = obtenerCliente(comando.clienteId());

        // Cada campo es opcional: si viene con valor, se actualiza.
        if (comando.nombreCompleto() != null && !comando.nombreCompleto().isBlank()) {
            cliente.cambiarNombre(comando.nombreCompleto());
        }
        if (comando.telefonoWhatsApp() != null && !comando.telefonoWhatsApp().isBlank()) {
            cliente.cambiarTelefono(Telefono.de(comando.telefonoWhatsApp()));
        }
        if (comando.email() != null && !comando.email().isBlank()) {
            cliente.cambiarEmail(Email.de(comando.email()));
        }
        if (comando.descripcionDireccion() != null && !comando.descripcionDireccion().isBlank()) {
            Direccion nueva = construirDireccion(comando);
            cliente.cambiarDireccion(nueva);
        }

        return repository.guardar(cliente);
    }

    @Override
    public Cliente suspender(UUID clienteId) {
        Cliente cliente = obtenerCliente(clienteId);
        cliente.suspender();
        return repository.guardar(cliente);
    }

    @Override
    public Cliente reactivar(UUID clienteId) {
        Cliente cliente = obtenerCliente(clienteId);
        cliente.reactivar();
        return repository.guardar(cliente);
    }

    @Override
    public Cliente retirar(UUID clienteId) {
        Cliente cliente = obtenerCliente(clienteId);
        cliente.retirar();
        return repository.guardar(cliente);
    }

    private Cliente obtenerCliente(UUID id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe cliente con id " + id));
    }

    private Direccion construirDireccion(CambiarDatosCommand comando) {
        if (comando.barrio() == null || comando.barrio().isBlank()) {
            return Direccion.sinBarrio(comando.descripcionDireccion(), comando.ciudad());
        }
        return Direccion.de(comando.descripcionDireccion(), comando.barrio(), comando.ciudad());
    }
}
