package com.wixnetworks.isp.clientes.infrastructure.persistence;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.EstadoCliente;
import com.wixnetworks.isp.clientes.domain.port.out.ClienteRepository;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter que implementa el puerto de salida ClienteRepository usando JPA.
 *
 * Esta es la pieza que conecta el dominio con PostgreSQL:
 * - El dominio llama a ClienteRepository (interfaz)
 * - Spring inyecta esta implementacion en tiempo de ejecucion
 * - Esta clase traduce usando el mapper y delega a Spring Data
 */
@Component
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final ClienteJpaRepository jpaRepository;
    private final ClienteJpaMapper mapper;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository,
                                    ClienteJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteJpaEntity entity = mapper.aEntity(cliente);
        ClienteJpaEntity guardado = jpaRepository.save(entity);
        return mapper.aDominio(guardado);
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::aDominio);
    }

    @Override
    public Optional<Cliente> buscarPorCodigoInterno(String codigoInterno) {
        return jpaRepository.findByCodigoInterno(codigoInterno).map(mapper::aDominio);
    }

    @Override
    public Optional<Cliente> buscarPorCedula(Cedula cedula) {
        return jpaRepository.findByCedula(cedula.valor()).map(mapper::aDominio);
    }

    @Override
    public List<Cliente> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(mapper::aDominio)
                .toList();
    }

    @Override
    public List<Cliente> listarPorEstado(EstadoCliente estado) {
        return jpaRepository.findByEstado(estado).stream()
                .map(mapper::aDominio)
                .toList();
    }

    @Override
    public boolean existeConCedula(Cedula cedula) {
        return jpaRepository.existsByCedula(cedula.valor());
    }

    @Override
    public boolean existeConCodigoInterno(String codigoInterno) {
        return jpaRepository.existsByCodigoInterno(codigoInterno);
    }
}
