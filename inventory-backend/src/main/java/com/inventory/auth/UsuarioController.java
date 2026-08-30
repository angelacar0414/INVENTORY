package com.inventory.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de Autenticación
 * Expone los endpoints para registro e inicio de sesión
 *
 * @author Darío Bustamante
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
public class UsuarioController {

    // ==================== INYECCIONES ====================

    /** Servicio de Usuario para la lógica de negocio */
    @Autowired
    private UsuarioService usuarioService;

    // ==================== ENDPOINTS ====================

    /**
     * Endpoint para registrar un nuevo usuario
     *
     * @param usuarioDTO datos del usuario (email y contraseña)
     * @return respuesta con mensaje de éxito o error
     */
    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody UsuarioDTO usuarioDTO) {

        // Crear mapa de respuesta
        Map<String, Object> respuesta = new HashMap<>();

        try {
            // Llamar al servicio para registrar
            String mensaje = usuarioService.registrar(usuarioDTO);

            // Verificar si fue exitoso
            if (mensaje.equals("Usuario registrado correctamente")) {
                respuesta.put("success", true);
                respuesta.put("message", mensaje);
                respuesta.put("data", usuarioDTO.getEmail());
                return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            } else {
                // Si hay error de validación
                respuesta.put("success", false);
                respuesta.put("message", mensaje);
                respuesta.put("errors", mensaje);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }

        } catch (Exception e) {
            // Capturar cualquier error no esperado
            respuesta.put("success", false);
            respuesta.put("message", "Error al registrar usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    /**
     * Endpoint para iniciar sesión (autenticar usuario)
     *
     * @param usuarioDTO credenciales del usuario (email y contraseña)
     * @return respuesta con mensaje de autenticación exitosa o error
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UsuarioDTO usuarioDTO) {

        // Crear mapa de respuesta
        Map<String, Object> respuesta = new HashMap<>();

        try {
            // Llamar al servicio para autenticar
            String mensaje = usuarioService.autenticar(usuarioDTO);

            // Verificar si fue exitoso
            if (mensaje.equals("Autenticación satisfactoria")) {
                respuesta.put("success", true);
                respuesta.put("message", mensaje);
                respuesta.put("email", usuarioDTO.getEmail());
                return ResponseEntity.status(HttpStatus.OK).body(respuesta);
            } else if (mensaje.equals("El usuario no existe")) {
                // Usuario no encontrado
                respuesta.put("success", false);
                respuesta.put("message", mensaje);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
            } else {
                // Error de validación (contraseña incorrecta, usuario inactivo, etc)
                respuesta.put("success", false);
                respuesta.put("message", mensaje);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
            }

        } catch (Exception e) {
            // Capturar cualquier error no esperado
            respuesta.put("success", false);
            respuesta.put("message", "Error al autenticar usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }
}