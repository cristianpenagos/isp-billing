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

class DireccionTest {

    @Nested
    @DisplayName("Creacion valida")
    class CreacionValida {

        @Test
        @DisplayName("Crea direccion con todos los campos")
        void creaDireccionCompleta() {
            Direccion direccion = Direccion.de(
                    "Vegas del Rio, Bloque 1, Apto 512",
                    "El Poblado",
                    "Medellin"
            );

            assertThat(direccion.descripcionLibre()).isEqualTo("Vegas del Rio, Bloque 1, Apto 512");
            assertThat(direccion.barrio()).contains("El Poblado");
            assertThat(direccion.ciudad()).isEqualTo("Medellin");
        }

        @Test
        @DisplayName("Crea direccion sin barrio usando metodo especifico")
        void creaDireccionSinBarrioConMetodoEspecifico() {
            Direccion direccion = Direccion.sinBarrio(
                    "Vereda La Cabaña, casa azul",
                    "Rionegro"
            );

            assertThat(direccion.barrio()).isEmpty();
            assertThat(direccion.ciudad()).isEqualTo("Rionegro");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Crea direccion sin barrio cuando se pasa nulo, vacio o espacios")
        void creaDireccionSinBarrioConValoresVacios(String barrioVacio) {
            Direccion direccion = Direccion.de(
                    "Vereda La Cabaña",
                    barrioVacio,
                    "Rionegro"
            );

            assertThat(direccion.barrio()).isEmpty();
        }

        @Test
        @DisplayName("Elimina espacios al inicio y final de los campos")
        void eliminaEspaciosAlrededor() {
            Direccion direccion = Direccion.de(
                    "  Calle 10 # 20-30  ",
                    "  Laureles  ",
                    "  Medellin  "
            );

            assertThat(direccion.descripcionLibre()).isEqualTo("Calle 10 # 20-30");
            assertThat(direccion.barrio()).contains("Laureles");
            assertThat(direccion.ciudad()).isEqualTo("Medellin");
        }
    }

    @Nested
    @DisplayName("Rechazo de descripcion invalida")
    class RechazoDescripcion {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Rechaza descripcion nula, vacia o solo espacios")
        void rechazaDescripcionVacia(String descripcionInvalida) {
            assertThatThrownBy(() ->
                    Direccion.de(descripcionInvalida, "El Poblado", "Medellin"))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("descripcion");
        }

        @Test
        @DisplayName("Rechaza descripcion con mas de 200 caracteres")
        void rechazaDescripcionMuyLarga() {
            String descripcionLarga = "a".repeat(201);

            assertThatThrownBy(() ->
                    Direccion.de(descripcionLarga, "El Poblado", "Medellin"))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("200");
        }
    }

    @Nested
    @DisplayName("Rechazo de ciudad invalida")
    class RechazoCiudad {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Rechaza ciudad nula, vacia o solo espacios")
        void rechazaCiudadVacia(String ciudadInvalida) {
            assertThatThrownBy(() ->
                    Direccion.de("Calle 10", "El Poblado", ciudadInvalida))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("ciudad");
        }

        @Test
        @DisplayName("Rechaza ciudad con mas de 100 caracteres")
        void rechazaCiudadMuyLarga() {
            String ciudadLarga = "a".repeat(101);

            assertThatThrownBy(() ->
                    Direccion.de("Calle 10", "El Poblado", ciudadLarga))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("100");
        }
    }

    @Nested
    @DisplayName("Rechazo de barrio invalido")
    class RechazoBarrio {

        @Test
        @DisplayName("Rechaza barrio con mas de 100 caracteres")
        void rechazaBarrioMuyLargo() {
            String barrioLargo = "a".repeat(101);

            assertThatThrownBy(() ->
                    Direccion.de("Calle 10", barrioLargo, "Medellin"))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("barrio");
        }
    }

    @Nested
    @DisplayName("Texto completo")
    class TextoCompleto {

        @Test
        @DisplayName("Texto completo incluye barrio cuando existe")
        void textoCompletoConBarrio() {
            Direccion direccion = Direccion.de(
                    "Vegas del Rio Apto 512",
                    "El Poblado",
                    "Medellin"
            );

            assertThat(direccion.textoCompleto())
                    .isEqualTo("Vegas del Rio Apto 512, El Poblado, Medellin");
        }

        @Test
        @DisplayName("Texto completo omite barrio cuando no existe")
        void textoCompletoSinBarrio() {
            Direccion direccion = Direccion.sinBarrio(
                    "Vereda La Cabaña",
                    "Rionegro"
            );

            assertThat(direccion.textoCompleto())
                    .isEqualTo("Vereda La Cabaña, Rionegro");
        }
    }

    @Nested
    @DisplayName("Igualdad")
    class Igualdad {

        @Test
        @DisplayName("Dos direcciones con los mismos datos son iguales")
        void direccionesConMismosDatosSonIguales() {
            Direccion d1 = Direccion.de("Calle 10", "Laureles", "Medellin");
            Direccion d2 = Direccion.de("Calle 10", "Laureles", "Medellin");

            assertThat(d1).isEqualTo(d2);
            assertThat(d1.hashCode()).isEqualTo(d2.hashCode());
        }

        @Test
        @DisplayName("Direcciones con datos distintos NO son iguales")
        void direccionesConDatosDistintosNoSonIguales() {
            Direccion d1 = Direccion.de("Calle 10", "Laureles", "Medellin");
            Direccion d2 = Direccion.de("Calle 20", "Laureles", "Medellin");

            assertThat(d1).isNotEqualTo(d2);
        }
    }
}