package com.wixnetworks.isp.shared.domain.valueobject;

import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios del value object Cedula.
 *
 * Estos tests son rapidos (milisegundos) porque no levantan Spring ni BD.
 * Solo validan las reglas de negocio del dominio.
 */
class CedulaTest {

    @Nested
    @DisplayName("Creacion de cedula valida")
    class CreacionValida {

        @Test
        @DisplayName("Crea cedula con 8 digitos correctamente")
        void creaCedulaCon8Digitos() {
            Cedula cedula = Cedula.de("12345678");

            assertThat(cedula.valor()).isEqualTo("12345678");
        }

        @Test
        @DisplayName("Crea cedula con 10 digitos (maxima longitud)")
        void creaCedulaConLongitudMaxima() {
            Cedula cedula = Cedula.de("1234567890");

            assertThat(cedula.valor()).isEqualTo("1234567890");
        }

        @Test
        @DisplayName("Crea cedula con 6 digitos (minima longitud)")
        void creaCedulaConLongitudMinima() {
            Cedula cedula = Cedula.de("123456");

            assertThat(cedula.valor()).isEqualTo("123456");
        }

        @Test
        @DisplayName("Elimina espacios al inicio y al final")
        void eliminaEspaciosAlrededor() {
            Cedula cedula = Cedula.de("  12345678  ");

            assertThat(cedula.valor()).isEqualTo("12345678");
        }
    }

    @Nested
    @DisplayName("Rechazo de cedulas invalidas")
    class RechazoInvalidas {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Rechaza valores nulos, vacios o solo espacios")
        void rechazaValoresVaciosONulos(String numeroInvalido) {
            assertThatThrownBy(() -> Cedula.de(numeroInvalido))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("no puede estar vacia");
        }

        @ParameterizedTest
        @ValueSource(strings = {"12345", "1", "12"})
        @DisplayName("Rechaza cedulas con menos de 6 digitos")
        void rechazaDemasiadoCortas(String numeroCorto) {
            assertThatThrownBy(() -> Cedula.de(numeroCorto))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("entre 6 y 10 digitos");
        }

        @ParameterizedTest
        @ValueSource(strings = {"12345678901", "123456789012345"})
        @DisplayName("Rechaza cedulas con mas de 10 digitos")
        void rechazaDemasiadoLargas(String numeroLargo) {
            assertThatThrownBy(() -> Cedula.de(numeroLargo))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("entre 6 y 10 digitos");
        }

        @ParameterizedTest
        @ValueSource(strings = {"abc12345", "12345abc", "12-34-56", "12.345.678", "abcdefgh"})
        @DisplayName("Rechaza cedulas con caracteres no numericos")
        void rechazaCaracteresNoNumericos(String numeroInvalido) {
            assertThatThrownBy(() -> Cedula.de(numeroInvalido))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("solo puede contener digitos");
        }
    }

    @Nested
    @DisplayName("Enmascaramiento")
    class Enmascaramiento {

        @Test
        @DisplayName("Enmascara cedula de 10 digitos mostrando solo los ultimos 3")
        void enmascaraCedula10Digitos() {
            Cedula cedula = Cedula.de("1234567890");

            assertThat(cedula.enmascarar()).isEqualTo("*******890");
        }

        @Test
        @DisplayName("Enmascara cedula de 8 digitos")
        void enmascaraCedula8Digitos() {
            Cedula cedula = Cedula.de("12345678");

            assertThat(cedula.enmascarar()).isEqualTo("*****678");
        }

        @Test
        @DisplayName("Enmascara cedula de 6 digitos (minima)")
        void enmascaraCedula6Digitos() {
            Cedula cedula = Cedula.de("123456");

            assertThat(cedula.enmascarar()).isEqualTo("***456");
        }
    }

    @Nested
    @DisplayName("Igualdad y hashCode")
    class IgualdadValor {

        @Test
        @DisplayName("Dos cedulas con el mismo numero son iguales")
        void cedulasConMismoValorSonIguales() {
            Cedula cedula1 = Cedula.de("12345678");
            Cedula cedula2 = Cedula.de("12345678");

            assertThat(cedula1).isEqualTo(cedula2);
            assertThat(cedula1.hashCode()).isEqualTo(cedula2.hashCode());
        }

        @Test
        @DisplayName("Dos cedulas con distinto numero NO son iguales")
        void cedulasConDistintoValorNoSonIguales() {
            Cedula cedula1 = Cedula.de("12345678");
            Cedula cedula2 = Cedula.de("87654321");

            assertThat(cedula1).isNotEqualTo(cedula2);
        }
    }

    @Test
    @DisplayName("toString retorna el numero crudo (NO enmascarado)")
    void toStringRetornaNumeroCrudo() {
        Cedula cedula = Cedula.de("12345678");

        assertThat(cedula.toString()).isEqualTo("12345678");
    }
}