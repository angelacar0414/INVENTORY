package com.inventory.dashboard.controller;

import com.inventory.dashboard.dto.DashboardDTO;
import com.inventory.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Este Controller solo tiene un endpoint porque el Dashboard es de solo
 * lectura: no se registra nada acá, solo se consulta un resumen.
 *
 * Seguimos el mismo formato de respuesta que ya usamos en Categorías y
 * Proveedores (success, message, data), para que en el Frontend podamos
 * leer la respuesta del mismo modo sin importar el módulo.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerResumen() {
        DashboardDTO resumen = dashboardService.obtenerResumen();
        return ResponseEntity.ok(success("Resumen del dashboard obtenido correctamente", resumen));
    }

    private Map<String, Object> success(String mensaje, Object data) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("success", true);
        respuesta.put("message", mensaje);
        respuesta.put("data", data);
        return respuesta;
    }
}
