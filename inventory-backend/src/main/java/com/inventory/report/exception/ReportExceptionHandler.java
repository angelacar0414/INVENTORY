package com.inventory.report.exception;

import com.inventory.report.controller.ReportController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MANEJO DE ERRORES DEL MÓDULO REPORTES
 * -------------------------------------
 * Solo atiende al ReportController (assignableTypes) y tiene la máxima
 * prioridad, igual que el handler de Movimientos, para que el
 * GlobalExceptionHandler (que captura Exception) no lo convierta en 500.
 *
 * Formato de error del proyecto (SSD 6.4): { success: false, message: "...", errors: ["..."] }
 */
@RestControllerAdvice(assignableTypes = ReportController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ReportExceptionHandler {

    @ExceptionHandler(InvalidReportException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidReport(
            InvalidReportException ex) {

        return respuesta(ex.getMessage());
    }

    /**
     * Ocurre cuando un parámetro llega con un formato que no se puede
     * convertir (por ejemplo desde=2026-13-45 o idProveedor=abc).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        return respuesta("El valor del filtro '" + ex.getName()
                + "' no tiene un formato válido.");
    }

    private ResponseEntity<Map<String, Object>> respuesta(String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", mensaje);
        body.put("errors", java.util.List.of(mensaje));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
