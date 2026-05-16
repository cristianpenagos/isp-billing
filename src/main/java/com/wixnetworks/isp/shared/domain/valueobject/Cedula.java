package com.wixnetworks.isp.shared.domain.valueobject;

import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;

import java.util.Objects;

/**
 * Value Object que representa una cedula de ciudadania colombiana.
 *
 * Reglas de validacion:
 * - Solo puede contener digitos (0-9)
 * - Longitud entre 6 y 10 caracteres (formato colombiano)
 * - No puede ser nula ni estar vacia
 *
 * Inmutable: una vez creada, su valor no puede cambiar.
 * Igualdad por valor: dos cedulas con el mismo numero son iguales.
 */

public final class Cedula {

    private static final int longitudMinima = 6;
    private static final int longitudMaxima = 10;

    private final String numero;

    private Cedula(String numero) {
        this.numero = numero;
    }

    /**
     * Crea una nueva Cedula validando el formato.
     *
     * @param numero el numero de cedula como string
     * @return una instancia valida de Cedula
     * @throws DatoInvalidoException si el numero es nulo, vacio, contiene
     *         caracteres no numericos o tiene longitud invalida
     */


    public static Cedula de(String numero) {
        if (numero == null || numero.isEmpty()){
            throw new DatoInvalidoException("La cedula no puede estar vacia");
        }
        String numeroLimpio = numero.trim();

        if (!numeroLimpio.matches("\\d+")){
            throw new DatoInvalidoException("La cedula solo puede contener digitos, Recibido: "+numero);
        }

        if (numeroLimpio.length()<longitudMinima || numeroLimpio.length()>longitudMaxima) {
            throw new DatoInvalidoException("La cedula debe tener entre "+longitudMinima+" y "+longitudMaxima" digitos, recibido: "+numeroLimpio.length() +" digitos");
        }
        return new Cedula(numeroLimpio);
    }
    public String valor(){
        return numero;
    }

    /**
     * Retorna una version enmascarada de la cedula para mostrar publicamente.
     * Muestra solo los ultimos 3 digitos. Ej: "1234567890" -> "*******890"
     */
    public String enmascarar() {
        int digitosVisibles = 3;
        if (numero.length() <= digitosVisibles) {
            return "*".repeat(numero.length());
        }
        int digitosOcultos = numero.length() - digitosVisibles;
        return "*".repeat(digitosOcultos) + numero.substring(digitosOcultos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cedula cedula)) return false;
        return Objects.equals(numero, cedula.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return numero;
    }
}
