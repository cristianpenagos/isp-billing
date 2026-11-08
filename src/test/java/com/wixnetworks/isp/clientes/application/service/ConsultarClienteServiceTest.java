package com.wixnetworks.isp.clientes.application.service;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.EstadoCliente;
import com.wixnetworks.isp.clientes.domain.port.out.ClienteRepository;
import com.wixnetworks.isp.shared.domain.exception.RecursoNoEncontradoException;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;
import com.wixnetworks.isp.shared.domain.valueobject.Direccion;
import com.wixnetworks.isp.shared.domain.valueobject.Email;
import com.wixnetworks.isp.shared.domain.valueobject.Telefono;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConsultarClienteServiceTest {

    private ClienteRepository repository;
    private ConsultarClienteService service;

    @BeforeEach
    void setUp() {
        repository = mock(ClienteRepository.class);
        service = new ConsultarClienteService(repository);
    }

    private Cliente clienteEjemplo() {
        return Cliente.registrar("WX-00128", Cedula.de("12345678"),
                "Juan Perez", Telefono.de("3147748687"),
                Email.de("juan@test.com"),
                Direccion.de("Calle 10", "Laureles", "Medellin"));
    }

    @Test
    @DisplayName("Obtiene cliente por id cuando existe")
    void obtienePorIdExistente() {
        Cliente cliente = clienteEjemplo();
        when(repository.buscarPorId(cliente.id())).thenReturn(Optional.of(cliente));

        Cliente resultado = service.obtenerPorId(cliente.id());

        assertThat(resultado).isEqualTo(cliente);
    }

    @Test
    @DisplayName("Lanza excepcion al obtener cliente por id inexistente")
    void obtenerPorIdInexistenteFalla() {
        UUID idFalso = UUID.randomUUID();
        when(repository.buscarPorId(idFalso)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorId(idFalso))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Obtiene cliente por codigo interno")
    void obtienePorCodigoInterno() {
        Cliente cliente = clienteEjemplo();
        when(repository.buscarPorCodigoInterno("WX-00128"))
                .thenReturn(Optional.of(cliente));

        Cliente resultado = service.obtenerPorCodigoInterno("WX-00128");

        assertThat(resultado).isEqualTo(cliente);
    }

    @Test
    @DisplayName("Lanza excepcion al buscar codigo interno inexistente")
    void obtenerPorCodigoInexistenteFalla() {
        when(repository.buscarPorCodigoInterno("WX-99999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorCodigoInterno("WX-99999"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Lista todos los clientes del repositorio")
    void listaTodos() {
        List<Cliente> clientes = List.of(clienteEjemplo());
        when(repository.listarTodos()).thenReturn(clientes);

        List<Cliente> resultado = service.listarTodos();

        assertThat(resultado).isEqualTo(clientes);
    }

    @Test
    @DisplayName("Lista clientes por estado")
    void listaPorEstado() {
        List<Cliente> clientes = List.of(clienteEjemplo());
        when(repository.listarPorEstado(EstadoCliente.ACTIVO)).thenReturn(clientes);

        List<Cliente> resultado = service.listarPorEstado(EstadoCliente.ACTIVO);

        assertThat(resultado).hasSize(1);
    }
}
