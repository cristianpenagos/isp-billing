package com.wixnetworks.isp.clientes.domain.model;

import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;
import com.wixnetworks.isp.shared.domain.exception.RecursoNoEncontradoException;
import com.wixnetworks.isp.shared.domain.valueobject.Cedula;
import com.wixnetworks.isp.shared.domain.valueobject.Direccion;
import com.wixnetworks.isp.shared.domain.valueobject.Email;
import com.wixnetworks.isp.shared.domain.valueobject.Telefono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Aggregate root del modulo Clientes.
 *
 * Un Cliente agrupa:
 * - Datos de identidad (cedula, nombre, telefono, email)
 * - Direccion de instalacion
 * - Estado (ACTIVO/SUSPENDIDO/RETIRADO) con reglas de transicion
 * - Lista de equipos CPE instalados
 *
 * Reglas (invariantes del aggregate):
 * - El codigo interno es unico y sigue el patron WX-NNNNN (6 digitos minimo tras WX-)
 * - La cedula es unica en el sistema (lo valida el repositorio, no esta clase)
 * - Un cliente nace ACTIVO
 * - Un cliente RETIRADO no puede recibir equipos nuevos
 * - Para cambios de estado se usan los metodos de dominio (suspender/reactivar/retirar)
 */

