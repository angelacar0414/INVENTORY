package com.inventory.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventory.user.dto.UserCreateDTO;
import com.inventory.user.dto.UserDTO;
import com.inventory.user.service.UserService;

/**
 * Controlador REST encargado de gestionar las operaciones
 * administrativas del módulo de usuarios.
 *
 * @author Dario Bustamante
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/usuarios")
public class UserController {

    // Servicio que contiene la lógica del módulo de usuarios.
    private final UserService userService;


    // ==================== CONSTRUCTOR ====================

    /**
     * Constructor del controlador.
     *
     * Spring inyecta automáticamente el servicio.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }


    // ==================== CREAR USUARIO ====================

    /**
     * Registra un nuevo usuario desde el módulo de usuarios.
     *
     * Endpoint:
     * POST /api/v1/usuarios
     */
    @PostMapping
    public ResponseEntity<UserDTO> crearUsuario(
            @RequestBody UserCreateDTO dto) {

        return ResponseEntity.ok(
                userService.crearUsuario(dto)
        );
    }


    // ==================== LISTAR USUARIOS ====================

    /**
     * Obtiene todos los usuarios registrados.
     *
     * Endpoint:
     * GET /api/v1/usuarios
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> listarUsuarios() {

        return ResponseEntity.ok(
                userService.listarUsuarios()
        );
    }


    // ==================== BUSCAR POR ID ====================

    /**
     * Obtiene un usuario específico mediante su ID.
     *
     * Endpoint:
     * GET /api/v1/usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userService.buscarPorId(id)
        );
    }


    // ==================== ACTUALIZAR USUARIO ====================

    /**
     * Actualiza los datos básicos de un usuario.
     *
     * Endpoint:
     * PUT /api/v1/usuarios/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody UserDTO dto) {

        return ResponseEntity.ok(
                userService.actualizarUsuario(id, dto)
        );
    }


    // ==================== INACTIVAR USUARIO ====================

    /**
     * Inactiva un usuario sin eliminarlo físicamente
     * de la base de datos.
     *
     * Endpoint:
     * DELETE /api/v1/usuarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> inactivarUsuario(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userService.inactivarUsuario(id)
        );
    }


    // ==================== REACTIVAR USUARIO ====================

    /**
     * Reactiva un usuario que estaba inactivo.
     *
     * Endpoint:
     * PUT /api/v1/usuarios/{id}/reactivar
     */
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<UserDTO> reactivarUsuario(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userService.reactivarUsuario(id)
        );
    }
}