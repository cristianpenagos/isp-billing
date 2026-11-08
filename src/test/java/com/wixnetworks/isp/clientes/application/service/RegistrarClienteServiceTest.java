package com.wixnetworks.isp.clientes.application.service;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.EstadoCliente;
import com.wixnetworks.isp.clientes.domain.port.in.RegistrarClienteUseCase.RegistrarClienteCommand;
import com.wixnetworks.isp.clientes.domain.port.out.ClienteRepository;
import com.wixnetworks.isp.shared.domain.exception.ConflictoEstadoException;
import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegistrarClienteServiceTest {

    private ClienteRepository repository;
    private RegistrarClienteService service;

    @BeforeEach
    void setUp() {
        // Crea un mock: un ClienteRepository "falso" que podemos controlar
        repository = mock(ClienteRepository.class);
        service = new RegistrarClienteService(repository);
    }

    private RegistrarClienteCommand comandoValido() {
        return new RegistrarClienteCommand(
                "WX-00128",
                "12345678",
                "Juan Perez",
                "3147748687",
                "juan@test.com",
                "Calle 10 # 20-30",
                "Laureles",
                "Medellin"
        );
    }

    @Nested
    @DisplayName("Registro exitoso")
    class RegistroExitoso {

        @Test
        @DisplayName("Registra un cliente nuevo cuando todos los datos son validos")
        void registraClienteValido() {
            // Configuramos el mock: cedula y codigo NO existen
            when(repository.existeConCedula(any())).thenReturn(false);
            when(repository.existeConCodigoInterno(any())).thenReturn(false);
            // El guardar devuelve el mismo cliente que recibe
            when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

            Cliente resultado = service.ejecutar(comandoValido());

            assertThat(resultado).isNotNull();
            assertThat(resultado.codigoInterno()).isEqualTo("WX-00128");
            assertThat(resultado.nombreCompleto()).isEqualTo("Juan Perez");
            assertThat(resultado.estado()).isEqualTo(EstadoCliente.ACTIVO);
            assertThat(resultado.equipos()).isEmpty();

            // Verificamos que el servicio efectivamente llamo a guardar
            verify(repository).guardar(any(Cliente.class));
        }

        @Test
        @DisplayName("Crea direccion sin barrio cuando barrio viene vacio")
        void creaDireccionSinBarrioCuandoBarrioVacio() {
            when(repository.existeConCedula(any())).thenReturn(false);
            when(repository.existeConCodigoInterno(any())).thenReturn(false);
            when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

            RegistrarClienteCommand comando = new RegistrarClienteCommand(
                    "WX-00200", "99887766", "Maria Lopez", "3001234567",
                    "maria@test.com", "Vereda La Loma", "", "Rionegro"
            );

            Cliente resultado = service.ejecutar(comando);

            assertThat(resultado.direccion().barrio()).isEmpty();
            assertThat(resultado.direccion().ciudad()).isEqualTo("Rionegro");
        }
    }

    @Nested
    @DisplayName("Rechazos por unicidad")
    class RechazosUnicidad {

        @Test
        @DisplayName("Rechaza si la cedula ya existe")
        void rechazaCedulaDuplicada() {
            when(repository.existeConCedula(any(Cedula.class))).thenReturn(true);

            assertThatThrownBy(() -> service.ejecutar(comandoValido()))
                    .isInstanceOf(ConflictoEstadoException.class)
                    .hasMessageContaining("cedula");
        }

        @Test
        @DisplayName("Rechaza si el codigo interno ya existe")
        void rechazaCodigoDuplicado() {
            when(repository.existeConCedula(any())).thenReturn(false);
            when(repository.existeConCodigoInterno("WX-00128")).thenReturn(true);

            assertThatThrownBy(() -> service.ejecutar(comandoValido()))
                    .isInstanceOf(ConflictoEstadoException.class)
                    .hasMessageContaining("codigo");
        }
    }

    @Nested
    @DisplayName("Rechazos por datos invalidos")
    class RechazosDatos {

        @Test
        @DisplayName("Rechaza cedula con formato invalido")
        void rechazaCedulaInvalida() {
            RegistrarClienteCommand comando = new RegistrarClienteCommand(
                    "WX-00128", "abc", "Juan Perez", "3147748687",
                    "juan@test.com", "Calle 10", "Laureles", "Medellin"
            );

            assertThatThrownBy(() -> service.ejecutar(comando))
                    .isInstanceOf(DatoInvalidoException.class);
        }

        @Test
        @DisplayName("Rechaza email con formato invalido")
        void rechazaEmailInvalido() {
            RegistrarClienteCommand comando = new RegistrarClienteCommand(
                    "WX-00128", "12345678", "Juan Perez", "3147748687",
                    "no-es-email", "Calle 10", "Laureles", "Medellin"
            );

            assertThatThrownBy(() -> service.ejecutar(comando))
                    .isInstanceOf(DatoInvalidoException.class);
        }
    }
}
