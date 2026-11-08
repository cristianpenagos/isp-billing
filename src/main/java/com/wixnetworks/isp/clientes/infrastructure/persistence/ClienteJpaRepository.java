package com.wixnetworks.isp.clientes.infrastructure.persistence;

import com.wixnetworks.isp.clientes.domain.model.EstadoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository para ClienteJpaEntity.
 *
 * NO es el ClienteRepository del dominio. Es un componente interno de
 * infraestructura que el adapter usa para acceder a la BD.
 *
 * Spring Data genera la implementacion automaticamente a partir de los
 * nombres de los metodos.
 */
@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, UUID> {

    Optional<ClienteJpaEntity> findByCodigoInterno(String codigoInterno);

    Optional<ClienteJpaEntity> findByCedula(String cedula);

    List<ClienteJpaEntity> findByEstado(EstadoCliente estado);

    boolean existsByCedula(String cedula);

    boolean existsByCodigoInterno(String codigoInterno);
}
