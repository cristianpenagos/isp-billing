package com.wixnetworks.isp.clientes.infrastructure.web;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.model.EstadoCliente;
import com.wixnetworks.isp.clientes.domain.port.in.ActualizarClienteUseCase;
import com.wixnetworks.isp.clientes.domain.port.in.ActualizarClienteUseCase.CambiarDatosCommand;
import com.wixnetworks.isp.clientes.domain.port.in.ConsultarClienteUseCase;
import com.wixnetworks.isp.clientes.domain.port.in.GestionarEquiposUseCase;
import com.wixnetworks.isp.clientes.domain.port.in.GestionarEquiposUseCase.AgregarEquipoCommand;
import com.wixnetworks.isp.clientes.domain.port.in.RegistrarClienteUseCase;
import com.wixnetworks.isp.clientes.domain.port.in.RegistrarClienteUseCase.RegistrarClienteCommand;
import com.wixnetworks.isp.clientes.infrastructure.web.dto.ActualizarClienteRequest;
import com.wixnetworks.isp.clientes.infrastructure.web.dto.AgregarEquipoRequest;
import com.wixnetworks.isp.clientes.infrastructure.web.dto.ClienteResponse;
import com.wixnetworks.isp.clientes.infrastructure.web.dto.RegistrarClienteRequest;
import com.wixnetworks.isp.clientes.infrastructure.web.mapper.ClienteWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para gestion de clientes.
 *
 * Expone los casos de uso del dominio como endpoints HTTP.
 * Los endpoints se documentan automaticamente en Swagger UI
 * (accesible en /swagger-ui.html).
 */
@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Gestion de clientes y equipos CPE")
public class ClienteController {

    private final RegistrarClienteUseCase registrarUseCase;
    private final ConsultarClienteUseCase consultarUseCase;
    private final ActualizarClienteUseCase actualizarUseCase;
    private final GestionarEquiposUseCase gestionarEquiposUseCase;
    private final ClienteWebMapper mapper;

    public ClienteController(RegistrarClienteUseCase registrarUseCase,
                             ConsultarClienteUseCase consultarUseCase,
                             ActualizarClienteUseCase actualizarUseCase,
                             GestionarEquiposUseCase gestionarEquiposUseCase,
                             ClienteWebMapper mapper) {
        this.registrarUseCase = registrarUseCase;
        this.consultarUseCase = consultarUseCase;
        this.actualizarUseCase = actualizarUseCase;
        this.gestionarEquiposUseCase = gestionarEquiposUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un cliente nuevo")
    public ClienteResponse registrar(@Valid @RequestBody RegistrarClienteRequest request) {
        RegistrarClienteCommand comando = new RegistrarClienteCommand(
                request.codigoInterno(),
                request.cedula(),
                request.nombreCompleto(),
                request.telefonoWhatsApp(),
                request.email(),
                request.descripcionDireccion(),
                request.barrio(),
                request.ciudad()
        );
        Cliente cliente = registrarUseCase.ejecutar(comando);
        return mapper.aResponse(cliente);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por id")
    public ClienteResponse obtenerPorId(@PathVariable UUID id) {
        Cliente cliente = consultarUseCase.obtenerPorId(id);
        return mapper.aResponse(cliente);
    }

    @GetMapping("/codigo/{codigoInterno}")
    @Operation(summary = "Obtener cliente por codigo interno (WX-NNNNN)")
    public ClienteResponse obtenerPorCodigo(@PathVariable String codigoInterno) {
        Cliente cliente = consultarUseCase.obtenerPorCodigoInterno(codigoInterno);
        return mapper.aResponse(cliente);
    }

    @GetMapping
    @Operation(summary = "Listar clientes, opcionalmente filtrados por estado")
    public List<ClienteResponse> listar(@RequestParam(required = false) EstadoCliente estado) {
        List<Cliente> clientes = (estado != null)
                ? consultarUseCase.listarPorEstado(estado)
                : consultarUseCase.listarTodos();

        return clientes.stream().map(mapper::aResponse).toList();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un cliente")
    public ClienteResponse actualizar(@PathVariable UUID id,
                                      @Valid @RequestBody ActualizarClienteRequest request) {
        CambiarDatosCommand comando = new CambiarDatosCommand(
                id,
                request.nombreCompleto(),
                request.telefonoWhatsApp(),
                request.email(),
                request.descripcionDireccion(),
                request.barrio(),
                request.ciudad()
        );
        Cliente cliente = actualizarUseCase.cambiarDatos(comando);
        return mapper.aResponse(cliente);
    }

    @PostMapping("/{id}/suspender")
    @Operation(summary = "Suspender un cliente ACTIVO")
    public ClienteResponse suspender(@PathVariable UUID id) {
        Cliente cliente = actualizarUseCase.suspender(id);
        return mapper.aResponse(cliente);
    }

    @PostMapping("/{id}/reactivar")
    @Operation(summary = "Reactivar un cliente SUSPENDIDO")
    public ClienteResponse reactivar(@PathVariable UUID id) {
        Cliente cliente = actualizarUseCase.reactivar(id);
        return mapper.aResponse(cliente);
    }

    @PostMapping("/{id}/retirar")
    @Operation(summary = "Retirar un cliente (estado terminal)")
    public ClienteResponse retirar(@PathVariable UUID id) {
        Cliente cliente = actualizarUseCase.retirar(id);
        return mapper.aResponse(cliente);
    }

    @PostMapping("/{id}/equipos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agregar un equipo CPE al cliente")
    public ClienteResponse agregarEquipo(@PathVariable UUID id,
                                         @Valid @RequestBody AgregarEquipoRequest request) {
        AgregarEquipoCommand comando = new AgregarEquipoCommand(
                id,
                request.tipo(),
                request.marca(),
                request.modelo(),
                request.macAddress(),
                request.propiedad(),
                request.fechaInstalacion()
        );
        Cliente cliente = gestionarEquiposUseCase.agregarEquipo(comando);
        return mapper.aResponse(cliente);
    }

    @DeleteMapping("/{id}/equipos/{equipoId}")
    @Operation(summary = "Retirar un equipo CPE del cliente")
    public ResponseEntity<ClienteResponse> retirarEquipo(@PathVariable UUID id,
                                                         @PathVariable UUID equipoId) {
        Cliente cliente = gestionarEquiposUseCase.retirarEquipo(id, equipoId);
        return ResponseEntity.ok(mapper.aResponse(cliente));
    }
}
