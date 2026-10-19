package com.inventory.profile.exception;

import com.inventory.profile.controller.ProfileController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MANEJO DE ERRORES DEL MÓDULO PERFIL
 * -----------------------------------
 * Mismo patrón que Movimientos y Reportes: solo atiende al
 * ProfileController y tiene la máxima prioridad para que el
 * GlobalExceptionHandler no lo convierta en un error 500.
 *
 * Formato (SSD 6.4): { success: false, message: "...", errors: ["..."] }
 */
@RestControllerAdvice(assignableTypes = ProfileController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProfileExceptionHandler {

    @ExceptionHandler(InvalidProfileException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidProfile(
            InvalidProfileException ex) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", ex.getMessage());
        body.put("errors", List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
