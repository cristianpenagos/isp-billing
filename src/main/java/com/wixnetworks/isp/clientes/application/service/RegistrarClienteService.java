package com.wixnetworks.isp.clientes.application.service;

import com.wixnetworks.isp.clientes.domain.model.Cliente;
import com.wixnetworks.isp.clientes.domain.port.in.RegistrarClienteUseCase;
import com.wixnetworks.isp.clientes.domain.port.out.ClienteRepository;
import com.wixnetworks.isp.shared.domain.exception.ConflictoEstadoException;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;
import com.wixnetworks.isp.shared.domain.valueobject.Direccion;
import com.wixnetworks.isp.shared.domain.valueobject.Email;
import com.wixnetworks.isp.shared.domain.valueobject.Telefono;

/**
 * Implementacion del caso de uso RegistrarClienteUseCase.
 *
 * Responsabilidades:
 * - Convertir strings crudos del command a value objects (que validan)
 * - Verificar reglas que requieren consultar el repositorio (unicidad)
 * - Delegar la creacion del cliente al factory del aggregate
 * - Persistir el cliente
 */
public class RegistrarClienteService implements RegistrarClienteUseCase {

    private final ClienteRepository repository;

    public RegistrarClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente ejecutar(RegistrarClienteCommand comando) {
        // 1. Convertir primitivas a value objects (estos validan por si mismos)
        Cedula cedula = Cedula.de(comando.cedula());
        Telefono telefono = Telefono.de(comando.telefonoWhatsApp());
        Email email = Email.de(comando.email());
        Direccion direccion = construirDireccion(comando);

        // 2. Verificar unicidad (reglas que requieren consultar el repositorio)
        verificarCedulaUnica(cedula);
        verificarCodigoInternoUnico(comando.codigoInterno());

        // 3. Crear el aggregate (el factory valida sus propias reglas)
        Cliente nuevo = Cliente.registrar(
                comando.codigoInterno(),
                cedula,
                comando.nombreCompleto(),
                telefono,
                email,
                direccion
        );

        // 4. Persistir
        return repository.guardar(nuevo);
    }

    private Direccion construirDireccion(RegistrarClienteCommand comando) {
        if (comando.barrio() == null || comando.barrio().isBlank()) {
            return Direccion.sinBarrio(comando.descripcionDireccion(), comando.ciudad());
        }
        return Direccion.de(comando.descripcionDireccion(), comando.barrio(), comando.ciudad());
    }

    private void verificarCedulaUnica(Cedula cedula) {
        if (repository.existeConCedula(cedula)) {
            throw new ConflictoEstadoException(
                    "Ya existe un cliente con la cedula " + cedula.enmascarar());
        }
    }

    private void verificarCodigoInternoUnico(String codigoInterno) {
        if (repository.existeConCodigoInterno(codigoInterno)) {
            throw new ConflictoEstadoException(
                    "Ya existe un cliente con el codigo interno " + codigoInterno);
        }
    }
}
