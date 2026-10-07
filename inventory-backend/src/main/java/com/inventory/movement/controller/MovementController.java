package com.inventory.movement.controller;

import com.inventory.movement.dto.MovementDTO;
import com.inventory.movement.service.MovementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST del módulo Movimientos.
 *
 * Recibe las solicitudes HTTP del frontend y las dirige hacia
 * MovementService. No contiene reglas de negocio: solo recibe la
 * solicitud, valida el DTO mediante @Valid, llama al Service y construye
 * la respuesta HTTP.
 *
 * El historial de movimientos no se puede modificar ni eliminar, por eso
 * este Controller no tiene endpoints PUT ni DELETE (y SecurityConfig
 * además los bloquea).
 *
 * Ruta base del módulo:
 *
 * /api/v1/movimientos
 */
@RestController
@RequestMapping("/api/v1/movimientos")
public class MovementController {

    /**
     * Service que contiene la lógica de negocio del módulo Movimientos.
     */
    private final MovementService movementService;

    /**
     * Constructor utilizado por Spring para inyectar MovementService.
     *
     * @param movementService servicio de movimientos
     */
    public MovementController(MovementService movementService) {
        this.movementService = movementService;
    }

    // =========================================================
    // CONSULTAR HISTORIAL
    // =========================================================

    /**
     * Consulta el historial de movimientos, del más reciente al más
     * antiguo.
     *
     * Endpoint:
     * GET /api/v1/movimientos
     * GET /api/v1/movimientos?criterio=texto
     * GET /api/v1/movimientos?tipo=ENTRADA
     * GET /api/v1/movimientos?criterio=texto&tipo=SALIDA
     *
     * @param criterio texto para buscar por nombre o código del producto
     * @param tipo ENTRADA o SALIDA
     * @return historial de movimientos
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(required = false) String criterio,
            @RequestParam(required = false) String tipo) {

        List<MovementDTO> movimientos =
                movementService.findAll(criterio, tipo);

        return ResponseEntity.ok(
                success(
                        "Movimientos consultados correctamente.",
                        movimientos
                )
        );
    }

    // =========================================================
    // CONSULTAR MOVIMIENTO POR ID
    // =========================================================

    /**
     * Consulta un movimiento específico utilizando su ID.
     *
     * Endpoint:
     * GET /api/v1/movimientos/{id}
     *
     * @param id identificador del movimiento
     * @return información del movimiento
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(
            @PathVariable Long id) {

        MovementDTO movimiento = movementService.findById(id);

        return ResponseEntity.ok(
                success(
                        "Movimiento consultado correctamente.",
                        movimiento
                )
        );
    }

    // =========================================================
    // REGISTRAR ENTRADA
    // =========================================================

    /**
     * Registra una entrada de inventario (aumenta el stock).
     *
     * Endpoint:
     * POST /api/v1/movimientos/entrada
     *
     * El usuario que registra el movimiento se toma de la sesión
     * iniciada; el frontend no lo envía.
     *
     * @param dto idProducto, cantidad y observación
     * @param authentication usuario con sesión iniciada
     * @return movimiento registrado
     */
    @PostMapping("/entrada")
    public ResponseEntity<Map<String, Object>> registrarEntrada(
            @Valid @RequestBody MovementDTO dto,
            Authentication authentication) {

        MovementDTO creado = movementService.registrarEntrada(
                dto,
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        success(
                                "Entrada registrada correctamente.",
                                creado
                        )
                );
    }

    // =========================================================
    // REGISTRAR SALIDA
    // =========================================================

    /**
     * Registra una salida de inventario (disminuye el stock).
     *
     * Endpoint:
     * POST /api/v1/movimientos/salida
     *
     * @param dto idProducto, cantidad, idCliente y observación
     * @param authentication usuario con sesión iniciada
     * @return movimiento registrado
     */
    @PostMapping("/salida")
    public ResponseEntity<Map<String, Object>> registrarSalida(
            @Valid @RequestBody MovementDTO dto,
            Authentication authentication) {

        MovementDTO creado = movementService.registrarSalida(
                dto,
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        success(
                                "Salida registrada correctamente.",
                                creado
                        )
                );
    }

    // =========================================================
    // RESPUESTA ESTÁNDAR
    // =========================================================

    /**
     * Construye la respuesta estándar utilizada por los módulos
     * del proyecto INVENTORY:
     *
     * {
     *     "success": true,
     *     "message": "...",
     *     "data": ...
     * }
     *
     * @param mensaje mensaje de respuesta
     * @param data información que se devolverá
     * @return mapa con la respuesta estándar
     */
    private Map<String, Object> success(
            String mensaje,
            Object data) {

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put("success", true);
        respuesta.put("message", mensaje);
        respuesta.put("data", data);

        return respuesta;
    }
}
