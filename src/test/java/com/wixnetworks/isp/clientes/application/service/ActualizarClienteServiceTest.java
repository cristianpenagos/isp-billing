package com.wixnetworks.isp.clientes.application.service;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.EstadoCliente;
import com.wixnetworks.isp.clientes.domain.port.in.ActualizarClienteUseCase.CambiarDatosCommand;
import com.wixnetworks.isp.clientes.domain.port.out.ClienteRepository;
import com.wixnetworks.isp.shared.domain.exception.ConflictoEstadoException;
import com.wixnetworks.isp.shared.domain.exception.RecursoNoEncontradoException;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;
import com.wixnetworks.isp.shared.domain.valueobject.Direccion;
import com.wixnetworks.isp.shared.domain.valueobject.Email;
import com.wixnetworks.isp.shared.domain.valueobject.Telefono;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActualizarClienteServiceTest {

    private ClienteRepository repository;
    private ActualizarClienteService service;
    private Cliente clienteExistente;

    @BeforeEach
    void setUp() {
        repository = mock(ClienteRepository.class);
        service = new ActualizarClienteService(repository);

        clienteExistente = Cliente.registrar("WX-00128", Cedula.de("12345678"),
                "Juan Perez", Telefono.de("3147748687"),
                Email.de("juan@test.com"),
                Direccion.de("Calle 10", "Laureles", "Medellin"));

        when(repository.buscarPorId(clienteExistente.id()))
                .thenReturn(Optional.of(clienteExistente));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Nested
    @DisplayName("Cambios de datos")
    class CambiosDatos {

        @Test
        @DisplayName("Cambia solo el nombre cuando el resto viene null")
        void cambiaSoloNombre() {
            CambiarDatosCommand comando = new CambiarDatosCommand(
                    clienteExistente.id(), "Juan Carlos Perez",
                    null, null, null, null, null);

            Cliente resultado = service.cambiarDatos(comando);

            assertThat(resultado.nombreCompleto()).isEqualTo("Juan Carlos Perez");
            // Los otros datos no cambian
            assertThat(resultado.email().valor()).isEqualTo("juan@test.com");
        }

        @Test
        @DisplayName("Cambia direccion completa cuando se envian los campos")
        void cambiaDireccionCompleta() {
            CambiarDatosCommand comando = new CambiarDatosCommand(
                    clienteExistente.id(), null, null, null,
                    "Carrera 50 # 10-20", "El Poblado", "Medellin");

            Cliente resultado = service.cambiarDatos(comando);

            assertThat(resultado.direccion().descripcionLibre())
                    .isEqualTo("Carrera 50 # 10-20");
            assertThat(resultado.direccion().barrio()).contains("El Poblado");
        }

        @Test
        @DisplayName("Falla si el cliente no existe")
        void fallaSiClienteNoExiste() {
            UUID idFalso = UUID.randomUUID();
            when(repository.buscarPorId(idFalso)).thenReturn(Optional.empty());

            CambiarDatosCommand comando = new CambiarDatosCommand(
                    idFalso, "Nombre Nuevo", null, null, null, null, null);

            assertThatThrownBy(() -> service.cambiarDatos(comando))
                    .isInstanceOf(RecursoNoEncontradoException.class);
        }
    }

    @Nested
    @DisplayName("Cambios de estado")
    class CambiosEstado {

        @Test
        @DisplayName("Suspende un cliente ACTIVO")
        void suspendeCliente() {
            Cliente resultado = service.suspender(clienteExistente.id());

            assertThat(resultado.estado()).isEqualTo(EstadoCliente.SUSPENDIDO);
        }

        @Test
        @DisplayName("Reactiva un cliente SUSPENDIDO")
        void reactivaCliente() {
            clienteExistente.suspender();

            Cliente resultado = service.reactivar(clienteExistente.id());

            assertThat(resultado.estado()).isEqualTo(EstadoCliente.ACTIVO);
        }

        @Test
        @DisplayName("Retira un cliente ACTIVO")
        void retiraCliente() {
            Cliente resultado = service.retirar(clienteExistente.id());

            assertThat(resultado.estado()).isEqualTo(EstadoCliente.RETIRADO);
        }

        @Test
        @DisplayName("Falla al reactivar un cliente ACTIVO")
        void fallaReactivarActivo() {
            assertThatThrownBy(() -> service.reactivar(clienteExistente.id()))
                    .isInstanceOf(ConflictoEstadoException.class);
        }

        @Test
        @DisplayName("Falla al cambiar estado de cliente inexistente")
        void fallaConClienteInexistente() {
            UUID idFalso = UUID.randomUUID();
            when(repository.buscarPorId(idFalso)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.suspender(idFalso))
                    .isInstanceOf(RecursoNoEncontradoException.class);
        }
    }
}
