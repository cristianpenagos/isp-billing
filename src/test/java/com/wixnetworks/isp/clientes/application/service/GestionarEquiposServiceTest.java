package com.wixnetworks.isp.clientes.application.service;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.EquipoCPE;
import com.wixnetworks.isp.clientes.domain.model.PropiedadEquipo;
import com.wixnetworks.isp.clientes.domain.model.TipoEquipo;
import com.wixnetworks.isp.clientes.domain.port.in.GestionarEquiposUseCase.AgregarEquipoCommand;
import com.wixnetworks.isp.clientes.domain.port.out.ClienteRepository;
import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;
import com.wixnetworks.isp.shared.domain.exception.RecursoNoEncontradoException;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;
import com.wixnetworks.isp.shared.domain.valueobject.Direccion;
import com.wixnetworks.isp.shared.domain.valueobject.Email;
import com.wixnetworks.isp.shared.domain.valueobject.Telefono;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GestionarEquiposServiceTest {

    private ClienteRepository repository;
    private GestionarEquiposService service;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        repository = mock(ClienteRepository.class);
        service = new GestionarEquiposService(repository);

        cliente = Cliente.registrar("WX-00128", Cedula.de("12345678"),
                "Juan Perez", Telefono.de("3147748687"),
                Email.de("juan@test.com"),
                Direccion.de("Calle 10", "Laureles", "Medellin"));

        when(repository.buscarPorId(cliente.id())).thenReturn(Optional.of(cliente));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private AgregarEquipoCommand comandoAgregarRouter() {
        return new AgregarEquipoCommand(
                cliente.id(), TipoEquipo.ROUTER, "TP-Link", "Archer C6",
                "AA:BB:CC:DD:EE:FF", PropiedadEquipo.COMODATO, LocalDate.now());
    }

    @Test
    @DisplayName("Agrega equipo a cliente ACTIVO")
    void agregaEquipo() {
        Cliente resultado = service.agregarEquipo(comandoAgregarRouter());

        assertThat(resultado.equipos()).hasSize(1);
        assertThat(resultado.equipos().get(0).tipo()).isEqualTo(TipoEquipo.ROUTER);
    }

    @Test
    @DisplayName("Falla al agregar equipo a cliente RETIRADO")
    void fallaAgregarAClienteRetirado() {
        cliente.retirar();

        assertThatThrownBy(() -> service.agregarEquipo(comandoAgregarRouter()))
                .isInstanceOf(DatoInvalidoException.class);
    }

    @Test
    @DisplayName("Falla al agregar equipo a cliente inexistente")
    void fallaConClienteInexistente() {
        UUID idFalso = UUID.randomUUID();
        when(repository.buscarPorId(idFalso)).thenReturn(Optional.empty());

        AgregarEquipoCommand comando = new AgregarEquipoCommand(
                idFalso, TipoEquipo.ROUTER, "TP-Link", "C6", null,
                PropiedadEquipo.COMODATO, LocalDate.now());

        assertThatThrownBy(() -> service.agregarEquipo(comando))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Retira equipo existente del cliente")
    void retiraEquipo() {
        EquipoCPE equipo = EquipoCPE.nuevo(TipoEquipo.ROUTER, "TP-Link", "C6",
                null, PropiedadEquipo.COMODATO, LocalDate.now());
        cliente.agregarEquipo(equipo);

        Cliente resultado = service.retirarEquipo(cliente.id(), equipo.id());

        assertThat(resultado.equipos()).isEmpty();
    }

    @Test
    @DisplayName("Falla al retirar equipo inexistente")
    void fallaRetirarEquipoInexistente() {
        UUID equipoIdFalso = UUID.randomUUID();

        assertThatThrownBy(() -> service.retirarEquipo(cliente.id(), equipoIdFalso))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
