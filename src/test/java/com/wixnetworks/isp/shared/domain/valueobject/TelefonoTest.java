package com.wixnetworks.isp.shared.domain.valueobject;

import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TelefonoTest {

    @Nested
    @DisplayName("Creacion y normalizacion")
    class CreacionValida {

        @ParameterizedTest
        @CsvSource({
                "+573147748687, +573147748687",
                "+57 314 774 8687, +573147748687",
                "+57-314-774-8687, +573147748687",
                "3147748687, +573147748687",
                "573147748687, +573147748687",
                "(57) 314-774-8687, +573147748687"
        })
        @DisplayName("Normaliza distintos formatos al formato E.164")
        void normalizaFormatosVariadosAE164(String entrada, String esperado) {
            Telefono telefono = Telefono.de(entrada);

            assertThat(telefono.valor()).isEqualTo(esperado);
        }
    }

    @Nested
    @DisplayName("Rechazo de telefonos invalidos")
    class RechazoInvalidos {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Rechaza valores nulos, vacios o solo espacios")
        void rechazaVaciosONulos(String invalido) {
            assertThatThrownBy(() -> Telefono.de(invalido))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("no puede estar vacio");
        }

        @ParameterizedTest
        @ValueSource(strings = {"+57314abc8687", "abcdefghij", "+57314-abc-8687"})
        @DisplayName("Rechaza telefonos con caracteres no numericos")
        void rechazaCaracteresNoNumericos(String invalido) {
            assertThatThrownBy(() -> Telefono.de(invalido))
                    .isInstanceOf(DatoInvalidoException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"+57314774868", "+5731477486", "+57314"})
        @DisplayName("Rechaza telefonos con menos de 10 digitos despues del codigo de pais")
        void rechazaCortos(String invalido) {
            assertThatThrownBy(() -> Telefono.de(invalido))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("10 digitos");
        }

        @ParameterizedTest
        @ValueSource(strings = {"+5731477486870", "+573147748687123"})
        @DisplayName("Rechaza telefonos con mas de 10 digitos despues del codigo de pais")
        void rechazaLargos(String invalido) {
            assertThatThrownBy(() -> Telefono.de(invalido))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("10 digitos");
        }

        @ParameterizedTest
        @ValueSource(strings = {"+571234567890", "+574234567890", "+576234567890"})
        @DisplayName("Rechaza telefonos fijos (no empiezan con 3)")
        void rechazaNoCelulares(String invalido) {
            assertThatThrownBy(() -> Telefono.de(invalido))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("celular");
        }
    }

    @Nested
    @DisplayName("Formatos de salida")
    class FormatosSalida {

        @Test
        @DisplayName("paraWhatsApp retorna sin el signo +")
        void paraWhatsAppRetornaSinPlus() {
            Telefono telefono = Telefono.de("+573147748687");

            assertThat(telefono.paraWhatsApp()).isEqualTo("573147748687");
        }

        @Test
        @DisplayName("enmascarar oculta los digitos del medio")
        void enmascaraDigitosDelMedio() {
            Telefono telefono = Telefono.de("+573147748687");

            assertThat(telefono.enmascarar()).isEqualTo("+57 314 *** 8687");
        }
    }

    @Nested
    @DisplayName("Igualdad")
    class Igualdad {

        @Test
        @DisplayName("Dos telefonos del mismo numero normalizado son iguales")
        void mismoNumeroSonIguales() {
            Telefono t1 = Telefono.de("3147748687");
            Telefono t2 = Telefono.de("+57 314 774 8687");

            assertThat(t1).isEqualTo(t2);
            assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
        }

        @Test
        @DisplayName("Dos telefonos distintos NO son iguales")
        void distintosNoSonIguales() {
            Telefono t1 = Telefono.de("3147748687");
            Telefono t2 = Telefono.de("3201234567");

            assertThat(t1).isNotEqualTo(t2);
        }
    }
}