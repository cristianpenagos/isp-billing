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

class EmailTest {

    @Nested
    @DisplayName("Creacion valida y normalizacion")
    class CreacionValida {

        @ParameterizedTest
        @ValueSource(strings = {
                "juan@example.com",
                "juan.perez@example.com",
                "juan+filtro@example.com",
                "juan_perez@sub.example.com",
                "j@x.co",
                "usuario123@dominio-con-guion.com"
        })
        @DisplayName("Acepta emails con formatos validos")
        void aceptaFormatosValidos(String emailValido) {
            Email email = Email.de(emailValido);

            assertThat(email.valor()).isEqualTo(emailValido.toLowerCase());
        }

        @Test
        @DisplayName("Normaliza a minusculas")
        void normalizaAMinusculas() {
            Email email = Email.de("JUAN@Example.COM");

            assertThat(email.valor()).isEqualTo("juan@example.com");
        }

        @Test
        @DisplayName("Elimina espacios alrededor")
        void eliminaEspacios() {
            Email email = Email.de("  juan@example.com  ");

            assertThat(email.valor()).isEqualTo("juan@example.com");
        }
    }

    @Nested
    @DisplayName("Rechazo de emails invalidos")
    class RechazoInvalidos {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Rechaza valores nulos, vacios o espacios")
        void rechazaVacios(String invalido) {
            assertThatThrownBy(() -> Email.de(invalido))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("no puede estar vacio");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "sin-arroba.com",
                "@sin-local.com",
                "sin-dominio@",
                "doble@@arroba.com",
                "sin-tld@dominio",
                "espacios en@email.com",
                "comillas\"@email.com"
        })
        @DisplayName("Rechaza emails con formato invalido")
        void rechazaFormatosInvalidos(String invalido) {
            assertThatThrownBy(() -> Email.de(invalido))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("formato valido");
        }

        @Test
        @DisplayName("Rechaza emails que exceden 254 caracteres")
        void rechazaDemasiadoLargos() {
            String emailLargo = "a".repeat(250) + "@x.co";

            assertThatThrownBy(() -> Email.de(emailLargo))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("254 caracteres");
        }
    }

    @Nested
    @DisplayName("Acceso a partes del email")
    class PartesEmail {

        @Test
        @DisplayName("parteLocal retorna lo que esta antes del arroba")
        void parteLocalRetornaAntesDelArroba() {
            Email email = Email.de("juan.perez@example.com");

            assertThat(email.parteLocal()).isEqualTo("juan.perez");
        }

        @Test
        @DisplayName("dominio retorna lo que esta despues del arroba")
        void dominioRetornaDespuesDelArroba() {
            Email email = Email.de("juan.perez@example.com");

            assertThat(email.dominio()).isEqualTo("example.com");
        }
    }

    @Nested
    @DisplayName("Igualdad")
    class Igualdad {

        @Test
        @DisplayName("Dos emails iguales (con distintas mayusculas) son equivalentes")
        void emailsConDistintasMayusculasSonIguales() {
            Email e1 = Email.de("JUAN@example.com");
            Email e2 = Email.de("juan@EXAMPLE.com");

            assertThat(e1).isEqualTo(e2);
            assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
        }
    }
}