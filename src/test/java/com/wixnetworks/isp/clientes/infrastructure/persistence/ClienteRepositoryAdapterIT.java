package com.wixnetworks.isp.clientes.infrastructure.persistence;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.EquipoCPE;
import com.wixnetworks.isp.clientes.domain.model.EstadoCliente;
import com.wixnetworks.isp.clientes.domain.model.PropiedadEquipo;
import com.wixnetworks.isp.clientes.domain.model.TipoEquipo;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;
import com.wixnetworks.isp.shared.domain.valueobject.Direccion;
import com.wixnetworks.isp.shared.domain.valueobject.Email;
import com.wixnetworks.isp.shared.domain.valueobject.Telefono;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integracion del ClienteRepositoryAdapter.
 *
 * Usa Testcontainers + PostgreSQL real + Flyway + JPA completo.
 *
 * Verifica que:
 * - Las migraciones Flyway corren bien
 * - Las entidades JPA mapean correctamente
 * - El mapper convierte de ida y vuelta sin perder datos
 * - El adapter respeta el contrato del puerto ClienteRepository
 */
@SpringBootTest
@Transactional
class ClienteRepositoryAdapterIT extends AbstractPostgresIT {

    @Autowired
    private ClienteRepositoryAdapter adapter;

    private Cliente clienteEjemplo;

    @BeforeEach
    void setUp() {
        clienteEjemplo = Cliente.registrar(
                "WX-00128",
                Cedula.de("12345678"),
                "Juan Perez",
                Telefono.de("3147748687"),
                Email.de("juan@test.com"),
                Direccion.de("Calle 10 # 20-30", "Laureles", "Medellin")
        );
    }

    @Test
    @DisplayName("Guarda y recupera un cliente")
    void guardaYRecupera() {
        adapter.guardar(clienteEjemplo);

        Optional<Cliente> encontrado = adapter.buscarPorId(clienteEjemplo.id());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().codigoInterno()).isEqualTo("WX-00128");
        assertThat(encontrado.get().nombreCompleto()).isEqualTo("Juan Perez");
        assertThat(encontrado.get().estado()).isEqualTo(EstadoCliente.ACTIVO);
    }

    @Test
    @DisplayName("Busca por codigo interno")
    void buscaPorCodigoInterno() {
        adapter.guardar(clienteEjemplo);

        Optional<Cliente> encontrado = adapter.buscarPorCodigoInterno("WX-00128");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().id()).isEqualTo(clienteEjemplo.id());
    }

    @Test
    @DisplayName("Busca por cedula")
    void buscaPorCedula() {
        adapter.guardar(clienteEjemplo);

        Optional<Cliente> encontrado = adapter.buscarPorCedula(Cedula.de("12345678"));

        assertThat(encontrado).isPresent();
    }

    @Test
    @DisplayName("Retorna vacio cuando el cliente no existe")
    void retornaVacioSiNoExiste() {
        Optional<Cliente> encontrado = adapter.buscarPorCodigoInterno("WX-99999");
        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("Verifica existencia por cedula")
    void verificaExistenciaPorCedula() {
        adapter.guardar(clienteEjemplo);

        assertThat(adapter.existeConCedula(Cedula.de("12345678"))).isTrue();
        assertThat(adapter.existeConCedula(Cedula.de("99999999"))).isFalse();
    }

    @Test
    @DisplayName("Verifica existencia por codigo interno")
    void verificaExistenciaPorCodigoInterno() {
        adapter.guardar(clienteEjemplo);

        assertThat(adapter.existeConCodigoInterno("WX-00128")).isTrue();
        assertThat(adapter.existeConCodigoInterno("WX-99999")).isFalse();
    }

    @Test
    @DisplayName("Lista clientes por estado")
    void listaPorEstado() {
        adapter.guardar(clienteEjemplo);

        Cliente suspendido = Cliente.registrar("WX-00200",
                Cedula.de("99887766"), "Pedro Gomez",
                Telefono.de("3001234567"), Email.de("pedro@test.com"),
                Direccion.de("Carrera 50", "El Poblado", "Medellin"));
        suspendido.suspender();
        adapter.guardar(suspendido);

        List<Cliente> activos = adapter.listarPorEstado(EstadoCliente.ACTIVO);
        List<Cliente> suspendidos = adapter.listarPorEstado(EstadoCliente.SUSPENDIDO);

        assertThat(activos).hasSize(1);
        assertThat(suspendidos).hasSize(1);
        assertThat(activos.get(0).codigoInterno()).isEqualTo("WX-00128");
        assertThat(suspendidos.get(0).codigoInterno()).isEqualTo("WX-00200");
    }

    @Test
    @DisplayName("Persiste direccion sin barrio")
    void persisteDireccionSinBarrio() {
        Cliente cliente = Cliente.registrar("WX-00300",
                Cedula.de("11112222"), "Maria Lopez",
                Telefono.de("3009876543"), Email.de("maria@test.com"),
                Direccion.sinBarrio("Vereda La Loma", "Rionegro"));

        adapter.guardar(cliente);
        Optional<Cliente> recuperado = adapter.buscarPorId(cliente.id());

        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().direccion().barrio()).isEmpty();
        assertThat(recuperado.get().direccion().ciudad()).isEqualTo("Rionegro");
    }

    @Test
    @DisplayName("Persiste equipos CPE junto al cliente")
    void persisteEquiposCpe() {
        EquipoCPE router = EquipoCPE.nuevo(
                TipoEquipo.ROUTER, "TP-Link", "Archer C6",
                "AA:BB:CC:DD:EE:FF", PropiedadEquipo.COMODATO, LocalDate.now());

        clienteEjemplo.agregarEquipo(router);
        adapter.guardar(clienteEjemplo);

        Optional<Cliente> recuperado = adapter.buscarPorId(clienteEjemplo.id());

        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().equipos()).hasSize(1);
        assertThat(recuperado.get().equipos().get(0).marca()).isEqualTo("TP-Link");
    }

    @Test
    @DisplayName("Actualiza estado al guardar de nuevo")
    void actualizaEstado() {
        adapter.guardar(clienteEjemplo);

        Cliente cargado = adapter.buscarPorId(clienteEjemplo.id()).orElseThrow();
        cargado.suspender();
        adapter.guardar(cargado);

        Cliente recargado = adapter.buscarPorId(clienteEjemplo.id()).orElseThrow();
        assertThat(recargado.estado()).isEqualTo(EstadoCliente.SUSPENDIDO);
    }
}
