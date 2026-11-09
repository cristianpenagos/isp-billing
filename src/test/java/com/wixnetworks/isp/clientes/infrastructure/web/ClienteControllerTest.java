package com.wixnetworks.isp.clientes.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.port.in.ActualizarClienteUseCase;
import com.wixnetworks.isp.clientes.domain.port.in.ConsultarClienteUseCase;
import com.wixnetworks.isp.clientes.domain.port.in.GestionarEquiposUseCase;
import com.wixnetworks.isp.clientes.domain.port.in.RegistrarClienteUseCase;
import com.wixnetworks.isp.clientes.infrastructure.web.dto.RegistrarClienteRequest;
import com.wixnetworks.isp.clientes.infrastructure.web.mapper.ClienteWebMapper;
import com.wixnetworks.isp.shared.domain.exception.ConflictoEstadoException;
import com.wixnetworks.isp.shared.domain.exception.RecursoNoEncontradoException;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;
import com.wixnetworks.isp.shared.domain.valueobject.Direccion;
import com.wixnetworks.isp.shared.domain.valueobject.Email;
import com.wixnetworks.isp.shared.domain.valueobject.Telefono;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test web del ClienteController usando MockMvc.
 *
 * Excluimos la auto-configuracion de Spring Security porque no estamos
 * testeando autorizacion aqui, solo que los controllers respondan bien.
 * Los tests de Security vendran aparte cuando implementemos JWT en Fase 7.
 *
 * WebMvcTest carga SOLO la capa web (controllers + handlers + mapper),
 * sin JPA ni base de datos. Los use cases son mockeados.
 */
@WebMvcTest(
        value = ClienteController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@Import({ClienteWebMapper.class, GlobalExceptionHandler.class})
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegistrarClienteUseCase registrarUseCase;

    @MockBean
    private ConsultarClienteUseCase consultarUseCase;

    @MockBean
    private ActualizarClienteUseCase actualizarUseCase;

    @MockBean
    private GestionarEquiposUseCase gestionarEquiposUseCase;

    private Cliente clienteEjemplo() {
        return Cliente.registrar("WX-00128", Cedula.de("12345678"),
                "Juan Perez", Telefono.de("3147748687"),
                Email.de("juan@test.com"),
                Direccion.de("Calle 10", "Laureles", "Medellin"));
    }

    @Test
    @DisplayName("POST /api/v1/clientes devuelve 201 cuando el registro es exitoso")
    void registroExitoso() throws Exception {
        Cliente cliente = clienteEjemplo();
        when(registrarUseCase.ejecutar(any())).thenReturn(cliente);

        RegistrarClienteRequest request = new RegistrarClienteRequest(
                "WX-00128", "12345678", "Juan Perez", "3147748687",
                "juan@test.com", "Calle 10", "Laureles", "Medellin"
        );

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoInterno").value("WX-00128"))
                .andExpect(jsonPath("$.nombreCompleto").value("Juan Perez"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    @DisplayName("POST /api/v1/clientes devuelve 400 cuando los datos son invalidos")
    void registroConDatosInvalidos() throws Exception {
        RegistrarClienteRequest request = new RegistrarClienteRequest(
                "SIN-FORMATO", "abc", "", "3147748687",
                "juan@test.com", "Calle 10", "Laureles", "Medellin"
        );

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/clientes devuelve 409 cuando la cedula ya existe")
    void registroConCedulaDuplicada() throws Exception {
        when(registrarUseCase.ejecutar(any()))
                .thenThrow(new ConflictoEstadoException("Ya existe cliente con cedula"));

        RegistrarClienteRequest request = new RegistrarClienteRequest(
                "WX-00128", "12345678", "Juan Perez", "3147748687",
                "juan@test.com", "Calle 10", "Laureles", "Medellin"
        );

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /api/v1/clientes/{id} devuelve 200 cuando existe")
    void obtenerExistente() throws Exception {
        Cliente cliente = clienteEjemplo();
        when(consultarUseCase.obtenerPorId(cliente.id())).thenReturn(cliente);

        mockMvc.perform(get("/api/v1/clientes/" + cliente.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoInterno").value("WX-00128"));
    }

    @Test
    @DisplayName("GET /api/v1/clientes/{id} devuelve 404 cuando no existe")
    void obtenerInexistente() throws Exception {
        UUID idFalso = UUID.randomUUID();
        when(consultarUseCase.obtenerPorId(idFalso))
                .thenThrow(new RecursoNoEncontradoException("No existe"));

        mockMvc.perform(get("/api/v1/clientes/" + idFalso))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/clientes retorna lista de todos los clientes")
    void listarTodos() throws Exception {
        when(consultarUseCase.listarTodos()).thenReturn(List.of(clienteEjemplo()));

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigoInterno").value("WX-00128"));
    }

    @Test
    @DisplayName("POST /api/v1/clientes/{id}/suspender suspende el cliente")
    void suspender() throws Exception {
        Cliente cliente = clienteEjemplo();
        cliente.suspender();
        when(actualizarUseCase.suspender(any())).thenReturn(cliente);

        mockMvc.perform(post("/api/v1/clientes/" + cliente.id() + "/suspender"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("SUSPENDIDO"));
    }
}