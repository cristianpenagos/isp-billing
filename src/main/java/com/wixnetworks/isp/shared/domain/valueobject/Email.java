package com.wixnetworks.isp.shared.domain.valueobject;

import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa una direccion de correo electronico.
 *
 * Reglas de validacion (RFC 5322 simplificado):
 * - Formato: parte_local@dominio.tld
 * - Maximo 254 caracteres en total (limite RFC)
 * - La parte local no puede estar vacia
 * - El dominio debe tener al menos un punto
 *
 * El email se normaliza a minusculas porque RFC 5321 establece que la
 * parte del dominio es case-insensitive (y la mayoria de proveedores
 * tratan toda la direccion como case-insensitive en la practica).
 */
public final class Email {

    private static final int LONGITUD_MAXIMA = 254;

    // Regex simplificada que cubre la mayoria de emails validos sin ser monstruosa
    private static final Pattern PATRON_EMAIL = Pattern.compile(
            "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"
    );

    private final String direccion;

    private Email(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Crea un nuevo Email validando el formato.
     *
     * @param direccion la direccion de correo
     * @return una instancia valida de Email, normalizada a minusculas
     * @throws DatoInvalidoException si el formato no es valido
     */
    public static Email de(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            throw new DatoInvalidoException("El email no puede estar vacio");
        }

        String normalizado = direccion.trim().toLowerCase();

        if (normalizado.length() > LONGITUD_MAXIMA) {
            throw new DatoInvalidoException(
                    "El email no puede tener mas de " + LONGITUD_MAXIMA + " caracteres");
        }

        if (!PATRON_EMAIL.matcher(normalizado).matches()) {
            throw new DatoInvalidoException(
                    "El email no tiene un formato valido, recibido: " + direccion);
        }

        return new Email(normalizado);
    }

    public String valor() {
        return direccion;
    }

    /**
     * Retorna la parte local del email (lo que esta antes del @).
     * Ejemplo: "juan@example.com" -> "juan"
     */
    public String parteLocal() {
        return direccion.substring(0, direccion.indexOf('@'));
    }

    /**
     * Retorna el dominio del email (lo que esta despues del @).
     * Ejemplo: "juan@example.com" -> "example.com"
     */
    public String dominio() {
        return direccion.substring(direccion.indexOf('@') + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(direccion, email.direccion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direccion);
    }

    @Override
    public String toString() {
        return direccion;
    }
}
