package com.wixnetworks.isp.clientes.domain.model;

import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EquipoCPETest {

    @Nested
    @DisplayName("Creacion de equipo nuevo")
    class CreacionNuevo {

        @Test
        @DisplayName("Crea equipo con todos los campos")
        void creaEquipoCompleto() {
            EquipoCPE equipo = EquipoCPE.nuevo(
                    TipoEquipo.ROUTER,
                    "TP-Link",
                    "Archer C6",
                    "AA:BB:CC:DD:EE:FF",
                    PropiedadEquipo.COMODATO,
                    LocalDate.of(2025, 3, 15)
            );

            assertThat(equipo.id()).isNotNull();
            assertThat(equipo.tipo()).isEqualTo(TipoEquipo.ROUTER);
            assertThat(equipo.marca()).isEqualTo("TP-Link");
            assertThat(equipo.modelo()).isEqualTo("Archer C6");
            assertThat(equipo.macAddress()).isEqualTo("AA:BB:CC:DD:EE:FF");
            assertThat(equipo.propiedad()).isEqualTo(PropiedadEquipo.COMODATO);
            assertThat(equipo.fechaInstalacion()).isEqualTo(LocalDate.of(2025, 3, 15));
        }

        @Test
        @DisplayName("Cada equipo nuevo tiene id diferente")
        void cadaEquipoTieneIdUnico() {
            EquipoCPE eq1 = EquipoCPE.nuevo(
                    TipoEquipo.ROUTER, null, null, null,
                    PropiedadEquipo.PROPIO, LocalDate.now());
            EquipoCPE eq2 = EquipoCPE.nuevo(
                    TipoEquipo.ROUTER, null, null, null,
                    PropiedadEquipo.PROPIO, LocalDate.now());

            assertThat(eq1.id()).isNotEqualTo(eq2.id());
        }

        @Test
        @DisplayName("Campos opcionales en blanco se guardan como null")
        void camposOpcionalesVaciosQuedanNull() {
            EquipoCPE equipo = EquipoCPE.nuevo(
                    TipoEquipo.ONU,
                    "   ",
                    "",
                    null,
                    PropiedadEquipo.ALQUILADO,
                    LocalDate.now()
            );

            assertThat(equipo.marca()).isNull();
            assertThat(equipo.modelo()).isNull();
            assertThat(equipo.macAddress()).isNull();
        }
    }

    @Nested
    @DisplayName("Rechazo de datos invalidos")
    class RechazoInvalidos {

        @Test
        @DisplayName("Rechaza tipo nulo")
        void rechazaTipoNulo() {
            assertThatThrownBy(() -> EquipoCPE.nuevo(
                    null, "TP-Link", "C6", null,
                    PropiedadEquipo.PROPIO, LocalDate.now()))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("tipo");
        }

        @Test
        @DisplayName("Rechaza propiedad nula")
        void rechazaPropiedadNula() {
            assertThatThrownBy(() -> EquipoCPE.nuevo(
                    TipoEquipo.ROUTER, "TP-Link", "C6", null,
                    null, LocalDate.now()))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("propiedad");
        }

        @Test
        @DisplayName("Rechaza fecha de instalacion nula")
        void rechazaFechaNula() {
            assertThatThrownBy(() -> EquipoCPE.nuevo(
                    TipoEquipo.ROUTER, "TP-Link", "C6", null,
                    PropiedadEquipo.PROPIO, null))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("fecha");
        }

        @Test
        @DisplayName("Rechaza fecha de instalacion futura")
        void rechazaFechaFutura() {
            LocalDate manana = LocalDate.now().plusDays(1);

            assertThatThrownBy(() -> EquipoCPE.nuevo(
                    TipoEquipo.ROUTER, "TP-Link", "C6", null,
                    PropiedadEquipo.PROPIO, manana))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("futura");
        }
    }

    @Nested
    @DisplayName("Reconstruccion desde BD")
    class Reconstruccion {

        @Test
        @DisplayName("Reconstruye equipo con id dado")
        void reconstruyeConIdExistente() {
            UUID idExistente = UUID.randomUUID();

            EquipoCPE equipo = EquipoCPE.reconstruir(
                    idExistente,
                    TipoEquipo.MIKROTIK,
                    "MikroTik",
                    "hAP ac2",
                    "11:22:33:44:55:66",
                    PropiedadEquipo.PROPIO,
                    LocalDate.of(2024, 1, 10)
            );

            assertThat(equipo.id()).isEqualTo(idExistente);
            assertThat(equipo.tipo()).isEqualTo(TipoEquipo.MIKROTIK);
        }

        @Test
        @DisplayName("Rechaza reconstruccion con id nulo")
        void rechazaReconstruccionConIdNulo() {
            assertThatThrownBy(() -> EquipoCPE.reconstruir(
                    null, TipoEquipo.ROUTER, "TP-Link", "C6",
                    null, PropiedadEquipo.PROPIO, LocalDate.now()))
                    .isInstanceOf(DatoInvalidoException.class)
                    .hasMessageContaining("id");
        }
    }

    @Nested
    @DisplayName("Igualdad por identidad")
    class IgualdadPorIdentidad {

        @Test
        @DisplayName("Dos equipos con mismo id son iguales aunque datos difieran")
        void mismoIdSonIguales() {
            UUID idCompartido = UUID.randomUUID();

            EquipoCPE eq1 = EquipoCPE.reconstruir(
                    idCompartido, TipoEquipo.ROUTER, "TP-Link", "C6",
                    null, PropiedadEquipo.PROPIO, LocalDate.now());
            EquipoCPE eq2 = EquipoCPE.reconstruir(
                    idCompartido, TipoEquipo.ONU, "Huawei", "HG8010",
                    null, PropiedadEquipo.ALQUILADO, LocalDate.now());

            assertThat(eq1).isEqualTo(eq2);
            assertThat(eq1.hashCode()).isEqualTo(eq2.hashCode());
        }

        @Test
        @DisplayName("Dos equipos con mismos datos pero diferente id NO son iguales")
        void diferenteIdNoSonIguales() {
            EquipoCPE eq1 = EquipoCPE.nuevo(
                    TipoEquipo.ROUTER, "TP-Link", "C6", null,
                    PropiedadEquipo.PROPIO, LocalDate.now());
            EquipoCPE eq2 = EquipoCPE.nuevo(
                    TipoEquipo.ROUTER, "TP-Link", "C6", null,
                    PropiedadEquipo.PROPIO, LocalDate.now());

            assertThat(eq1).isNotEqualTo(eq2);
        }
    }
}
