package com.wixnetworks.isp.clientes.infrastructure.config;

import com.wixnetworks.isp.clientes.application.service.ActualizarClienteService;
import com.wixnetworks.isp.clientes.application.service.ConsultarClienteService;
import com.wixnetworks.isp.clientes.application.service.GestionarEquiposService;
import com.wixnetworks.isp.clientes.application.service.RegistrarClienteService;
import com.wixnetworks.isp.clientes.domain.port.in.ActualizarClienteUseCase;
import com.wixnetworks.isp.clientes.domain.port.in.ConsultarClienteUseCase;
import com.wixnetworks.isp.clientes.domain.port.in.GestionarEquiposUseCase;
import com.wixnetworks.isp.clientes.domain.port.in.RegistrarClienteUseCase;
import com.wixnetworks.isp.clientes.domain.port.out.ClienteRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de beans del modulo Clientes.
 *
 * Los services del dominio son Java puro (sin @Service) para mantener
 * el dominio independiente de Spring. Esta clase los expone como beans
 * para que Spring pueda inyectarlos en los controllers.
 */
@Configuration
public class ClientesBeansConfig {

    @Bean
    public RegistrarClienteUseCase registrarClienteUseCase(ClienteRepository repository) {
        return new RegistrarClienteService(repository);
    }

    @Bean
    public ConsultarClienteUseCase consultarClienteUseCase(ClienteRepository repository) {
        return new ConsultarClienteService(repository);
    }

    @Bean
    public ActualizarClienteUseCase actualizarClienteUseCase(ClienteRepository repository) {
        return new ActualizarClienteService(repository);
    }

    @Bean
    public GestionarEquiposUseCase gestionarEquiposUseCase(ClienteRepository repository) {
        return new GestionarEquiposService(repository);
    }
}
