package com.wixnetworks.isp.clientes.infrastructure.web;

import com.wixnetworks.isp.shared.domain.exception.ConflictoEstadoException;
import com.wixnetworks.isp.shared.domain.exception.DatoInvalidoException;
import com.wixnetworks.isp.shared.domain.exception.RecursoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de excepciones.
 *
 * Traduce las excepciones del dominio a HTTP status apropiados:
 * - DatoInvalidoException → 400 Bad Request
 * - RecursoNoEncontradoException → 404 Not Found
 * - ConflictoEstadoException → 409 Conflict
 * - MethodArgumentNotValidException → 400 (validaciones Bean Validation)
 *
 * Esto evita tener try/catch repetitivos en cada metodo del controller.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNoEncontrado(RecursoNoEncontradoException ex) {
        return construirError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DatoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleDatoInvalido(DatoInvalidoException ex) {
        return construirError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ConflictoEstadoException.class)
    public ResponseEntity<ErrorResponse> handleConflicto(ConflictoEstadoException ex) {
        return construirError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validacion fallida",
                LocalDateTime.now(),
                errores
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ResponseEntity<ErrorResponse> construirError(HttpStatus status, String mensaje) {
        ErrorResponse response = new ErrorResponse(
                status.value(),
                mensaje,
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Estructura estandar de respuesta de error.
     */
    public record ErrorResponse(
            int status,
            String mensaje,
            LocalDateTime timestamp,
            Map<String, String> erroresCampos
    ) {}
}
