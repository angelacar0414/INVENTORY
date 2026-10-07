package com.inventory.movement.exception;

import com.inventory.movement.controller.MovementController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador de excepciones propio del módulo Movimientos.
 *
 * Complementa al GlobalExceptionHandler del proyecto: aquí solo se
 * atienden las excepciones específicas de movimientos. El resto de
 * errores (producto no encontrado, validaciones del DTO, errores
 * inesperados) los sigue resolviendo el manejador global.
 *
 * - assignableTypes limita este manejador únicamente a MovementController.
 * - @Order con la máxima prioridad hace que se evalúe ANTES que el
 *   manejador global; de lo contrario, el handler genérico
 *   (Exception.class) del GlobalExceptionHandler respondería con un
 *   error 500.
 *
 * Mantiene el mismo formato de error del resto de la API:
 *
 * {
 *     "success": false,
 *     "message": "..."
 * }
 */
@RestControllerAdvice(assignableTypes = MovementController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MovementExceptionHandler {

    /**
     * Reglas de negocio de movimientos incumplidas (incluye el stock
     * insuficiente, que es una subclase). Responde HTTP 400.
     */
    @ExceptionHandler(InvalidMovementException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidMovement(
            InvalidMovementException ex) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
