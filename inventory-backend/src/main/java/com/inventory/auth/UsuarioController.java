package com.inventory.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de Autenticación.
 * Expone los endpoints para registro, inicio y cierre de sesión.
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

    /** Administrador de autenticación de Spring Security */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Repositorio utilizado para guardar el contexto de seguridad
     * dentro de la sesión HTTP.
     */
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();


    // ==================== REGISTRO ====================

    /**
     * Endpoint para registrar un nuevo usuario.
     *
     * @param usuarioDTO datos del usuario
     * @return respuesta con mensaje de éxito o error
     */
    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrar(
            @RequestBody UsuarioDTO usuarioDTO) {

        Map<String, Object> respuesta = new HashMap<>();

        try {

            String mensaje = usuarioService.registrar(usuarioDTO);

            if (mensaje.equals("Usuario registrado correctamente")) {

                respuesta.put("success", true);
                respuesta.put("message", mensaje);
                respuesta.put("data", usuarioDTO.getUsername());

                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(respuesta);

            } else {

                respuesta.put("success", false);
                respuesta.put("message", mensaje);
                respuesta.put("errors", mensaje);

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(respuesta);
            }

        } catch (Exception e) {

            respuesta.put("success", false);
            respuesta.put(
                    "message",
                    "Error al registrar usuario: " + e.getMessage()
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(respuesta);
        }
    }


    // ==================== LOGIN ====================

    /**
     * Endpoint para iniciar sesión.
     *
     * Spring Security valida las credenciales y, si son correctas,
     * guarda la autenticación dentro de una sesión HTTP.
     *
     * @param usuarioDTO credenciales del usuario
     * @param request solicitud HTTP
     * @param response respuesta HTTP
     * @return respuesta con el resultado de la autenticación
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody UsuarioDTO usuarioDTO,
            HttpServletRequest request,
            HttpServletResponse response) {

        Map<String, Object> respuesta = new HashMap<>();

        try {

            // ==================== VALIDACIONES ====================

            if (usuarioDTO.getUsername() == null ||
                    usuarioDTO.getUsername().trim().isEmpty()) {

                respuesta.put("success", false);
                respuesta.put("message", "El username es obligatorio");

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(respuesta);
            }

            if (usuarioDTO.getContraseña() == null ||
                    usuarioDTO.getContraseña().trim().isEmpty()) {

                respuesta.put("success", false);
                respuesta.put("message", "La contraseña es obligatoria");

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(respuesta);
            }


            // ==================== AUTENTICACIÓN ====================

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    usuarioDTO.getUsername().trim(),
                                    usuarioDTO.getContraseña()
                            )
                    );


            // ==================== CREAR CONTEXTO DE SEGURIDAD ====================

            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);

            SecurityContextHolder.setContext(context);


            // ==================== GUARDAR SESIÓN ====================

            securityContextRepository.saveContext(
                    context,
                    request,
                    response
            );


            // ==================== OBTENER EL ROL ====================

            // Cuando Spring Security valida las credenciales, ya sabe
            // qué rol tiene el usuario (lo tomó de UsuarioDetailsService
            // al momento de autenticar). Ese rol queda guardado dentro
            // de "authorities", pero con el prefijo "ROLE_" que Spring
            // Security agrega automáticamente por dentro (por ejemplo:
            // "ROLE_ADMINISTRADOR").
            //
            // Como al Frontend no le sirve ese prefijo, lo quitamos
            // aquí antes de mandarlo, para que solo llegue
            // "ADMINISTRADOR" u "OPERADOR", tal como están guardados
            // en la base de datos.
            String rol = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .orElse("");


            // ==================== RESPUESTA ====================

            respuesta.put("success", true);
            respuesta.put("message", "Autenticación satisfactoria");
            respuesta.put("username", authentication.getName());

            // Enviamos también el rol para que el Frontend pueda
            // mostrar en pantalla si el usuario que inició sesión
            // es Administrador u Operador, sin tener que volver a
            // consultar la base de datos desde otra pantalla.
            respuesta.put("rol", rol);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(respuesta);


        } catch (DisabledException e) {

            respuesta.put("success", false);
            respuesta.put("message", "El usuario está inactivo");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(respuesta);


        } catch (BadCredentialsException e) {

            respuesta.put("success", false);
            respuesta.put("message", "Username o contraseña incorrectos");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(respuesta);


        } catch (Exception e) {

            respuesta.put("success", false);
            respuesta.put(
                    "message",
                    "Error al autenticar usuario: " + e.getMessage()
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(respuesta);
        }
    }


    // ==================== LOGOUT ====================

    /**
     * Endpoint para cerrar sesión.
     *
     * Elimina la sesión HTTP actual y limpia el contexto
     * de seguridad de Spring Security.
     *
     * @param request solicitud HTTP
     * @return respuesta de cierre de sesión
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            HttpServletRequest request) {

        Map<String, Object> respuesta = new HashMap<>();

        try {

            // Obtener la sesión actual, si existe.
            HttpSession session = request.getSession(false);

            if (session != null) {
                session.invalidate();
            }

            // Limpiar el contexto de seguridad.
            SecurityContextHolder.clearContext();

            respuesta.put("success", true);
            respuesta.put("message", "Sesión cerrada correctamente");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(respuesta);

        } catch (Exception e) {

            respuesta.put("success", false);
            respuesta.put(
                    "message",
                    "Error al cerrar sesión: " + e.getMessage()
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(respuesta);
        }
    }
}