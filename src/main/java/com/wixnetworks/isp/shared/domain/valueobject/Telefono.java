
package com.wixnetworks.isp.shared.domain.valueobject;

import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;

import java.util.Objects;

/**
 * Value Object que representa un numero de telefono en formato E.164 colombiano.
 *
 * Reglas de validacion:
 * - Debe ser un numero colombiano (codigo de pais +57)
 * - Despues del +57 debe tener exactamente 10 digitos
 * - El primer digito despues del codigo de pais debe ser 3 (celular)
 *
 * Internamente se almacena en formato canonico: +573147748687
 * Acepta entradas con espacios o guiones, que se normalizan automaticamente.
 *
 * Ejemplos validos al crear:
 * - "+573147748687"
 * - "+57 314 774 8687"
 * - "+57-314-774-8687"
 * - "3147748687" (se asume +57)
 * - "573147748687"
 */
public final class Telefono {

    private static final String CODIGO_PAIS_COLOMBIA = "+57";
    private static final int LONGITUD_NACIONAL = 10;

    private final String numeroE164;

    private Telefono(String numeroE164) {
        this.numeroE164 = numeroE164;
    }

    /**
     * Crea un nuevo Telefono validando el formato.
     *
     * @param numero el numero de telefono (acepta multiples formatos)
     * @return una instancia valida de Telefono en formato E.164
     * @throws DatoInvalidoException si el numero no cumple las reglas
     */
    public static Telefono de(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new DatoInvalidoException("El telefono no puede estar vacio");
        }

        // Limpieza: quitar espacios, guiones y parentesis
        String limpio = numero.trim()
                .replace(" ", "")
                .replace("-", "")
                .replace("(", "")
                .replace(")", "");

        // Normalizacion: asegurar prefijo +57
        String normalizado = normalizar(limpio);

        // Validar que el resto (despues de +57) sea exactamente 10 digitos
        String parteNacional = normalizado.substring(CODIGO_PAIS_COLOMBIA.length());

        if (!parteNacional.matches("\\d+")) {
            throw new DatoInvalidoException(
                    "El telefono solo puede contener digitos, recibido: " + numero);
        }

        if (parteNacional.length() != LONGITUD_NACIONAL) {
            throw new DatoInvalidoException(
                    "El telefono colombiano debe tener 10 digitos despues del codigo de pais, " +
                            "recibido: " + parteNacional.length() + " digitos");
        }

        // Validar que sea celular (empieza con 3)
        if (!parteNacional.startsWith("3")) {
            throw new DatoInvalidoException(
                    "El telefono debe ser un celular colombiano (debe empezar con 3), " +
                            "recibido: " + numero);
        }

        return new Telefono(normalizado);
    }

    private static String normalizar(String entrada) {
        if (entrada.startsWith("+57")) {
            return entrada;
        }
        if (entrada.startsWith("57") && entrada.length() == 12) {
            return "+" + entrada;
        }
        if (entrada.length() == 10) {
            return CODIGO_PAIS_COLOMBIA + entrada;
        }
        // Si no coincide con ningun patron, dejarlo como esta para que la validacion siguiente lo rechace
        return entrada.startsWith("+") ? entrada : CODIGO_PAIS_COLOMBIA + entrada;
    }

    /**
     * Retorna el numero en formato E.164 estandar.
     * Ejemplo: "+573147748687"
     */
    public String valor() {
        return numeroE164;
    }

    /**
     * Retorna el numero en el formato que WhatsApp Business API requiere.
     * Sin "+" ni espacios, solo digitos.
     * Ejemplo: "+573147748687" -> "573147748687"
     */
    public String paraWhatsApp() {
        return numeroE164.substring(1);
    }

    /**
     * Retorna una version enmascarada para mostrar publicamente.
     * Ejemplo: "+573147748687" -> "+57 314 *** 8687"
     */
    public String enmascarar() {
        String nacional = numeroE164.substring(CODIGO_PAIS_COLOMBIA.length());
        return CODIGO_PAIS_COLOMBIA + " " + nacional.substring(0, 3) + " *** " + nacional.substring(6);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Telefono telefono)) return false;
        return Objects.equals(numeroE164, telefono.numeroE164);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroE164);
    }

    @Override
    public String toString() {
        return numeroE164;
    }
}