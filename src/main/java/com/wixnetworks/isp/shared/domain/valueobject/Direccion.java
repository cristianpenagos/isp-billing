package com.wixnetworks.isp.shared.domain.valueobject;

import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;

import java.util.Objects;
import java.util.Optional;

/**
 * Value Object que representa una direccion fisica del cliente.
 *
 * Reglas de validacion:
 * - descripcionLibre es obligatoria (max 200 caracteres)
 * - ciudad es obligatoria (max 100 caracteres)
 * - barrio es opcional (max 100 caracteres si esta presente)
 *
 * descripcionLibre incluye datos como nombre de conjunto/edificio, numero de
 * apartamento, etc. Es texto libre porque las direcciones en Colombia tienen
 * formatos muy variables (urbano, rural, conjuntos cerrados, etc.).
 *
 * Ejemplos validos:
 * - "Vegas del Rio, Bloque 1, Apto 512", barrio "El Poblado", ciudad "Medellin"
 * - "Vereda La Cabaña, casa color azul, despues del puente", sin barrio, ciudad "Rionegro"
 */
public final class Direccion {

    private static final int MAX_DESCRIPCION = 200;
    private static final int MAX_BARRIO = 100;
    private static final int MAX_CIUDAD = 100;

    private final String descripcionLibre;
    private final String barrio;
    private final String ciudad;

    private Direccion(String descripcionLibre, String barrio, String ciudad) {
        this.descripcionLibre = descripcionLibre;
        this.barrio = barrio;
        this.ciudad = ciudad;
    }

    /**
     * Crea una direccion con barrio.
     *
     * @param descripcionLibre detalles de la direccion (obligatorio, max 200 chars)
     * @param barrio nombre del barrio o sector (opcional, puede ser null o vacio)
     * @param ciudad nombre de la ciudad (obligatorio, max 100 chars)
     * @return instancia valida de Direccion
     * @throws DatoInvalidoException si los datos no cumplen las reglas
     */
    public static Direccion de(String descripcionLibre, String barrio, String ciudad) {
        validarDescripcion(descripcionLibre);
        validarCiudad(ciudad);
        String barrioValidado = validarBarrioOpcional(barrio);

        return new Direccion(
                descripcionLibre.trim(),
                barrioValidado,
                ciudad.trim()
        );
    }

    /**
     * Crea una direccion sin barrio (caso comun en zonas rurales).
     */
    public static Direccion sinBarrio(String descripcionLibre, String ciudad) {
        return de(descripcionLibre, null, ciudad);
    }

    private static void validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new DatoInvalidoException("La descripcion de la direccion no puede estar vacia");
        }
        if (descripcion.trim().length() > MAX_DESCRIPCION) {
            throw new DatoInvalidoException(
                    "La descripcion no puede tener mas de " + MAX_DESCRIPCION + " caracteres");
        }
    }

    private static void validarCiudad(String ciudad) {
        if (ciudad == null || ciudad.isBlank()) {
            throw new DatoInvalidoException("La ciudad no puede estar vacia");
        }
        if (ciudad.trim().length() > MAX_CIUDAD) {
            throw new DatoInvalidoException(
                    "La ciudad no puede tener mas de " + MAX_CIUDAD + " caracteres");
        }
    }

    private static String validarBarrioOpcional(String barrio) {
        if (barrio == null || barrio.isBlank()) {
            return null;
        }
        if (barrio.trim().length() > MAX_BARRIO) {
            throw new DatoInvalidoException(
                    "El barrio no puede tener mas de " + MAX_BARRIO + " caracteres");
        }
        return barrio.trim();
    }

    public String descripcionLibre() {
        return descripcionLibre;
    }

    /**
     * Retorna el barrio como Optional porque es un campo opcional.
     * Usa .orElse("") o .ifPresent() en el codigo cliente.
     */
    public Optional<String> barrio() {
        return Optional.ofNullable(barrio);
    }

    public String ciudad() {
        return ciudad;
    }

    /**
     * Retorna la direccion completa como texto unificado para mostrar.
     * Ejemplo: "Vegas del Rio Apto 512, El Poblado, Medellin"
     */
    public String textoCompleto() {
        StringBuilder sb = new StringBuilder(descripcionLibre);
        if (barrio != null) {
            sb.append(", ").append(barrio);
        }
        sb.append(", ").append(ciudad);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Direccion that)) return false;
        return Objects.equals(descripcionLibre, that.descripcionLibre)
                && Objects.equals(barrio, that.barrio)
                && Objects.equals(ciudad, that.ciudad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descripcionLibre, barrio, ciudad);
    }

    @Override
    public String toString() {
        return textoCompleto();
    }
}