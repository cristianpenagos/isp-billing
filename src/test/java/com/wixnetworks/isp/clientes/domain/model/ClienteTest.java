package com.wixnetworks.isp.clientes.domain.model;

import com.wixnetworks.isp.shared.domain.exception.ConflictoEstadoException;
import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;
import com.wixnetworks.isp.shared.domain.exception.RecursoNoEncontradoException;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;
import com.wixnetworks.isp.shared.domain.valueobject.Direccion;
import com.wixnetworks.isp.shared.domain.valueobject.Email;
import com.wixnetworks.isp.shared.domain.valueobject.Telefono;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClienteTest {

    private Cedula cedula;
    private Telefono telefono;
    private Email email;
    private Direccion direccion;

    @BeforeEach
    void setUp() {
        cedula = Cedula.de("12345678");
        telefono = Telefono.de("3147748687");
        email = Email.de("cliente@test.com");
        direccion = Direccion.de("Calle 10 # 20-30", "Laureles", "Medellin");
    }

    private Cliente clienteValido() {
        return Cliente.registrar("WX-00128", cedula, "Juan Perez",
                telefono, email, direccion);
    }

    @Nested
    @DisplayName("Registro de cliente nuevo")
    class RegistroNuevo {

        @Test
        @DisplayName("Un cliente registrado nace ACTIVO sin equipos")
        void clienteNaceActivoSinEquipos() {
            Cliente cliente = clienteValido();

            assertThat(cliente.estado()).isEqualTo(EstadoCliente.ACTIVO);
            assertThat(cliente.equipos()).isEmpty();
            assertThat(cliente.tieneEquipos()).isFalse();
            assertThat(cliente.id()).isNotNull();
            assertThat(cliente.fechaRegistro()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("Cliente registrado conserva todos sus datos")
        void conservaDatos() {
            Cliente cliente = clienteValido();

            assertThat(cliente.codigoInterno()).isEqualTo("WX-00128");
            assertThat(cliente.cedula()).isEqualTo(cedula);
            assertThat(cliente.nombreCompleto()).isEqualTo("Juan Perez");
            assertThat(cliente.telefonoWhatsApp()).isEqualTo(telefono);
            assertThat(cliente.email()).isEqualTo(email);
            assertThat(cliente.direccion()).isEqualTo(direccion);
        }
    }

    @Nested
    @DisplayName("Validaciones de registro")
    class ValidacionesRegistro {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "WX-123", "WX", "128", "wx-00128"})
        @DisplayName("Rechaza codigo interno invalido")
        void rechazaCodigoInvalido(String codigoMalo) {
            assertThatThrownBy(() ->
                    Cliente.registrar(codigoMalo, cedula, "Juan Perez",
                            telefono, email, direccion))
                    .isInstanceOf(DatoInvalidoException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "ab"})
        @DisplayName("Rechaza nombre invalido")
        void rechazaNombreInvalido(String nombreMalo) {
            assertThatThrownBy(() ->
                    Cliente.registrar("WX-00128", cedula, nombreMalo,
                            telefono, email, direccion))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("nombre");
        }

        @Test
        @DisplayName("Rechaza nombre de mas de 150 caracteres")
        void rechazaNombreMuyLargo() {
            String nombreLargo = "a".repeat(151);
            assertThatThrownBy(() ->
                    Cliente.registrar("WX-00128", cedula, nombreLargo,
                            telefono, email, direccion))
                    .isInstanceOf(DatoInvalidoException.class);
        }

        @Test
        @DisplayName("Rechaza cedula nula")
        void rechazaCedulaNula() {
            assertThatThrownBy(() ->
                    Cliente.registrar("WX-00128", null, "Juan Perez",
                            telefono, email, direccion))
                    .isInstanceOf(DatoInvalidoException.class);
        }
    }

    @Nested
    @DisplayName("Transiciones de estado")
    class TransicionesEstado {

        @Test
        @DisplayName("Cliente ACTIVO puede suspenderse")
        void activoPuedeSuspenderse() {
            Cliente cliente = clienteValido();
            cliente.suspender();
            assertThat(cliente.estado()).isEqualTo(EstadoCliente.SUSPENDIDO);
        }

        @Test
        @DisplayName("Cliente SUSPENDIDO puede reactivarse")
        void suspendidoPuedeReactivarse() {
            Cliente cliente = clienteValido();
            cliente.suspender();
            cliente.reactivar();
            assertThat(cliente.estado()).isEqualTo(EstadoCliente.ACTIVO);
        }

        @Test
        @DisplayName("Cliente ACTIVO puede retirarse")
        void activoPuedeRetirarse() {
            Cliente cliente = clienteValido();
            cliente.retirar();
            assertThat(cliente.estado()).isEqualTo(EstadoCliente.RETIRADO);
        }

        @Test
        @DisplayName("Cliente RETIRADO no puede reactivarse")
        void retiradoNoPuedeReactivarse() {
            Cliente cliente = clienteValido();
            cliente.retirar();
            assertThatThrownBy(cliente::reactivar)
                    .isInstanceOf(ConflictoEstadoException.class);
        }

        @Test
        @DisplayName("Cliente ACTIVO no puede reactivarse (ya esta activo)")
        void activoNoPuedeReactivarse() {
            Cliente cliente = clienteValido();
            assertThatThrownBy(cliente::reactivar)
                    .isInstanceOf(ConflictoEstadoException.class);
        }
    }

    @Nested
    @DisplayName("Cambios de datos")
    class CambiosDatos {

        @Test
        @DisplayName("Puede cambiar nombre")
        void cambiarNombre() {
            Cliente cliente = clienteValido();
            cliente.cambiarNombre("Juan Carlos Perez Gomez");
            assertThat(cliente.nombreCompleto()).isEqualTo("Juan Carlos Perez Gomez");
        }

        @Test
        @DisplayName("Puede cambiar direccion")
        void cambiarDireccion() {
            Cliente cliente = clienteValido();
            Direccion nueva = Direccion.sinBarrio("Vereda La Loma", "Rionegro");
            cliente.cambiarDireccion(nueva);
            assertThat(cliente.direccion()).isEqualTo(nueva);
        }

        @Test
        @DisplayName("Puede cambiar telefono")
        void cambiarTelefono() {
            Cliente cliente = clienteValido();
            Telefono nuevo = Telefono.de("3001234567");
            cliente.cambiarTelefono(nuevo);
            assertThat(cliente.telefonoWhatsApp()).isEqualTo(nuevo);
        }

        @Test
        @DisplayName("Rechaza cambios con valores nulos")
        void rechazaCambiosNulos() {
            Cliente cliente = clienteValido();
            assertThatThrownBy(() -> cliente.cambiarNombre(null))
                    .isInstanceOf(DatoInvalidoException.class);
            assertThatThrownBy(() -> cliente.cambiarTelefono(null))
                    .isInstanceOf(DatoInvalidoException.class);
            assertThatThrownBy(() -> cliente.cambiarEmail(null))
                    .isInstanceOf(DatoInvalidoException.class);
            assertThatThrownBy(() -> cliente.cambiarDireccion(null))
                    .isInstanceOf(DatoInvalidoException.class);
        }
    }

    @Nested
    @DisplayName("Gestion de equipos")
    class GestionEquipos {

        private EquipoCPE equipoRouter() {
            return EquipoCPE.nuevo(TipoEquipo.ROUTER, "TP-Link", "C6",
                    null, PropiedadEquipo.COMODATO, LocalDate.now());
        }

        @Test
        @DisplayName("Puede agregar equipo a cliente ACTIVO")
        void agregarEquipoActivo() {
            Cliente cliente = clienteValido();
            EquipoCPE router = equipoRouter();

            cliente.agregarEquipo(router);

            assertThat(cliente.equipos()).hasSize(1);
            assertThat(cliente.equipos()).contains(router);
            assertThat(cliente.tieneEquipos()).isTrue();
        }

        @Test
        @DisplayName("Puede agregar equipo a cliente SUSPENDIDO")
        void agregarEquipoSuspendido() {
            Cliente cliente = clienteValido();
            cliente.suspender();

            cliente.agregarEquipo(equipoRouter());

            assertThat(cliente.equipos()).hasSize(1);
        }

        @Test
        @DisplayName("NO puede agregar equipo a cliente RETIRADO")
        void noAgregarEquipoRetirado() {
            Cliente cliente = clienteValido();
            cliente.retirar();

            assertThatThrownBy(() -> cliente.agregarEquipo(equipoRouter()))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("retirado");
        }

        @Test
        @DisplayName("Rechaza agregar mismo equipo dos veces")
        void noAgregarDosVeces() {
            Cliente cliente = clienteValido();
            EquipoCPE router = equipoRouter();
            cliente.agregarEquipo(router);

            assertThatThrownBy(() -> cliente.agregarEquipo(router))
                    .isInstanceOf(DatoInvalidoException.class);
        }

        @Test
        @DisplayName("Puede retirar equipo existente")
        void retirarEquipoExistente() {
            Cliente cliente = clienteValido();
            EquipoCPE router = equipoRouter();
            cliente.agregarEquipo(router);

            cliente.retirarEquipo(router.id());

            assertThat(cliente.equipos()).isEmpty();
        }

        @Test
        @DisplayName("Rechaza retirar equipo inexistente")
        void retirarEquipoInexistente() {
            Cliente cliente = clienteValido();
            UUID idFalso = UUID.randomUUID();

            assertThatThrownBy(() -> cliente.retirarEquipo(idFalso))
                    .isInstanceOf(RecursoNoEncontradoException.class);
        }

        @Test
        @DisplayName("Puede buscar equipo por id")
        void buscarEquipo() {
            Cliente cliente = clienteValido();
            EquipoCPE router = equipoRouter();
            cliente.agregarEquipo(router);

            assertThat(cliente.buscarEquipo(router.id())).contains(router);
            assertThat(cliente.buscarEquipo(UUID.randomUUID())).isEmpty();
        }

        @Test
        @DisplayName("La lista de equipos retornada es inmutable")
        void listaEquiposInmutable() {
            Cliente cliente = clienteValido();
            cliente.agregarEquipo(equipoRouter());

            assertThatThrownBy(() -> cliente.equipos().add(equipoRouter()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("Igualdad por identidad")
    class IgualdadIdentidad {

        @Test
        @DisplayName("Dos clientes distintos con mismos datos NO son iguales")
        void clientesDistintosNoSonIguales() {
            Cliente c1 = clienteValido();
            Cliente c2 = clienteValido();
            assertThat(c1).isNotEqualTo(c2);
        }

        @Test
        @DisplayName("Dos clientes con mismo id son iguales")
        void mismoIdSonIguales() {
            UUID idCompartido = UUID.randomUUID();
            Cliente c1 = Cliente.reconstruir(idCompartido, "WX-00128", cedula,
                    "Juan Perez", telefono, email, direccion,
                    EstadoCliente.ACTIVO, java.util.List.of(), LocalDate.now());
            Cliente c2 = Cliente.reconstruir(idCompartido, "WX-00129", cedula,
                    "Otro Nombre", telefono, email, direccion,
                    EstadoCliente.SUSPENDIDO, java.util.List.of(), LocalDate.now());

            assertThat(c1).isEqualTo(c2);
        }
    }
}
