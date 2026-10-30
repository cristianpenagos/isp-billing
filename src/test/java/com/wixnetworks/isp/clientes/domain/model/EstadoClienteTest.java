package com.wixnetworks.isp.clientes.domain.model;

import com.wixnetworks.isp.shared.domain.exception.ConflictoEstadoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

class EstadoClienteTest {

    @Nested
    @DisplayName("Transiciones validas")
    class TransicionesValidas {

        @Test
        @DisplayName("ACTIVO puede ir a SUSPENDIDO")
        void activoASuspendido() {
            assertThatCode(() ->
                    EstadoCliente.ACTIVO.validarTransicionA(EstadoCliente.SUSPENDIDO))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("ACTIVO puede ir a RETIRADO")
        void activoARetirado() {
            assertThatCode(() ->
                    EstadoCliente.ACTIVO.validarTransicionA(EstadoCliente.RETIRADO))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("SUSPENDIDO puede volver a ACTIVO")
        void suspendidoAActivo() {
            assertThatCode(() ->
                    EstadoCliente.SUSPENDIDO.validarTransicionA(EstadoCliente.ACTIVO))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("SUSPENDIDO puede ir a RETIRADO")
        void suspendidoARetirado() {
            assertThatCode(() ->
                    EstadoCliente.SUSPENDIDO.validarTransicionA(EstadoCliente.RETIRADO))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Transiciones invalidas")
    class TransicionesInvalidas {

        @Test
        @DisplayName("RETIRADO no puede ir a ACTIVO")
        void retiradoNoVaAActivo() {
            assertThatThrownBy(() ->
                    EstadoCliente.RETIRADO.validarTransicionA(EstadoCliente.ACTIVO))
                    .isInstanceOf(ConflictoEstadoException.class);
        }

        @Test
        @DisplayName("RETIRADO no puede ir a SUSPENDIDO")
        void retiradoNoVaASuspendido() {
            assertThatThrownBy(() ->
                    EstadoCliente.RETIRADO.validarTransicionA(EstadoCliente.SUSPENDIDO))
                    .isInstanceOf(ConflictoEstadoException.class);
        }

        @Test
        @DisplayName("ACTIVO no puede transicionar a si mismo")
        void activoAActivoFalla() {
            assertThatThrownBy(() ->
                    EstadoCliente.ACTIVO.validarTransicionA(EstadoCliente.ACTIVO))
                    .isInstanceOf(ConflictoEstadoException.class)
                    .hasMessageContaining("ya se encuentra");
        }

        @Test
        @DisplayName("SUSPENDIDO no puede transicionar a si mismo")
        void suspendidoASuspendidoFalla() {
            assertThatThrownBy(() ->
                    EstadoCliente.SUSPENDIDO.validarTransicionA(EstadoCliente.SUSPENDIDO))
                    .isInstanceOf(ConflictoEstadoException.class);
        }
    }

    @Nested
    @DisplayName("Metodos de consulta")
    class MetodosConsulta {

        @Test
        @DisplayName("Solo RETIRADO es terminal")
        void soloRetiradoEsTerminal() {
            assertThat(EstadoCliente.ACTIVO.esTerminal()).isFalse();
            assertThat(EstadoCliente.SUSPENDIDO.esTerminal()).isFalse();
            assertThat(EstadoCliente.RETIRADO.esTerminal()).isTrue();
        }

        @Test
        @DisplayName("ACTIVO y SUSPENDIDO pueden recibir facturas")
        void activoYSuspendidoRecibenFacturas() {
            assertThat(EstadoCliente.ACTIVO.puedeRecibirFacturas()).isTrue();
            assertThat(EstadoCliente.SUSPENDIDO.puedeRecibirFacturas()).isTrue();
        }

        @Test
        @DisplayName("RETIRADO NO puede recibir facturas")
        void retiradoNoRecibeFacturas() {
            assertThat(EstadoCliente.RETIRADO.puedeRecibirFacturas()).isFalse();
        }
    }
}
