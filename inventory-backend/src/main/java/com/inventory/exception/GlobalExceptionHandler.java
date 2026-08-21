package com.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * EXCEPTION HANDLER GLOBAL
 * --------------------------
 * En vez de poner try/catch en cada Controller, aquí atrapamos
 * TODOS los errores del sistema y devolvemos siempre el mismo
 * formato de respuesta, sin exponer
 * información técnica sensible (stack traces, nombres de clases, etc).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Cuando no se encuentra algo (ej: categoría con id que no existe)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Cuando se intenta duplicar algo único (ej: nombre de categoría repetido)
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateResourceException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Cuando fallan las validaciones de @NotBlank, @Size, etc.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", "Error de validación en los datos enviados.");
        body.put("errors", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Cualquier otro error no controlado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado en el servidor.");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}
