package com.inventory.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio de Usuario.
 * Contiene la lógica de negocio para el registro
 * y la autenticación de usuarios.
 *
 * @author Darío Bustamante
 * @version 1.0
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Codificador utilizado para proteger las contraseñas.
     */
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();


    // ==================== REGISTRO ====================

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * El usuario registrado mediante este método recibe
     * automáticamente el rol OPERADOR.
     *
     * @param usuarioDTO datos del usuario a registrar
     * @return mensaje indicando el resultado de la operación
     */
    public String registrar(UsuarioDTO usuarioDTO) {

        // Validar nombre
        if (usuarioDTO.getNombre() == null ||
                usuarioDTO.getNombre().trim().isEmpty()) {

            return "El nombre es obligatorio";
        }

        // Validar apellido
        if (usuarioDTO.getApellido() == null ||
                usuarioDTO.getApellido().trim().isEmpty()) {

            return "El apellido es obligatorio";
        }

        // Validar username
        if (usuarioDTO.getUsername() == null ||
                usuarioDTO.getUsername().trim().isEmpty()) {

            return "El username es obligatorio";
        }

        // Validar email
        if (usuarioDTO.getEmail() == null ||
                usuarioDTO.getEmail().trim().isEmpty()) {

            return "El email es obligatorio";
        }

        // Validar contraseña
        if (usuarioDTO.getContraseña() == null ||
                usuarioDTO.getContraseña().trim().isEmpty()) {

            return "La contraseña es obligatoria";
        }

        // Verificar username existente
        if (usuarioRepository.existsByUsername(
                usuarioDTO.getUsername().trim())) {

            return "El username ya está registrado";
        }

        // Verificar email existente
        if (usuarioRepository.existsByEmail(
                usuarioDTO.getEmail().trim())) {

            return "El email ya está registrado";
        }


        // ==================== CREAR USUARIO ====================

        UsuarioEntity nuevoUsuario = new UsuarioEntity();

        nuevoUsuario.setNombre(usuarioDTO.getNombre().trim());
        nuevoUsuario.setApellido(usuarioDTO.getApellido().trim());
        nuevoUsuario.setUsername(usuarioDTO.getUsername().trim());
        nuevoUsuario.setEmail(usuarioDTO.getEmail().trim());

        // Encriptar contraseña antes de guardarla
        String contraseñaEncriptada =
                passwordEncoder.encode(usuarioDTO.getContraseña());

        nuevoUsuario.setContraseñaEncriptada(contraseñaEncriptada);

        // El registro público asigna automáticamente OPERADOR
        nuevoUsuario.setRol("OPERADOR");

        // Usuario activo por defecto
        nuevoUsuario.setActivo(true);

        // Fechas de registro y actualización
        nuevoUsuario.setFechaCreacion(LocalDateTime.now());
        nuevoUsuario.setFechaActualizacion(LocalDateTime.now());

        // Guardar en la base de datos
        usuarioRepository.save(nuevoUsuario);

        return "Usuario registrado correctamente";
    }


    // ==================== AUTENTICACIÓN ====================

    /**
     * Autentica un usuario utilizando username y contraseña.
     *
     * Esta versión todavía realiza la comprobación manual
     * de credenciales. Posteriormente Spring Security será
     * integrado para gestionar la autenticación y la sesión HTTP.
     *
     * @param usuarioDTO datos utilizados para iniciar sesión
     * @return mensaje indicando el resultado de la autenticación
     */
    public String autenticar(UsuarioDTO usuarioDTO) {

        // Validar username
        if (usuarioDTO.getUsername() == null ||
                usuarioDTO.getUsername().trim().isEmpty()) {

            return "El username es obligatorio";
        }

        // Validar contraseña
        if (usuarioDTO.getContraseña() == null ||
                usuarioDTO.getContraseña().trim().isEmpty()) {

            return "La contraseña es obligatoria";
        }


        // Buscar usuario por username
        Optional<UsuarioEntity> usuarioOptional =
                usuarioRepository.findByUsername(
                        usuarioDTO.getUsername().trim());


        // Usuario inexistente
        if (usuarioOptional.isEmpty()) {
            return "El usuario no existe";
        }


        UsuarioEntity usuario = usuarioOptional.get();


        // Verificar si el usuario está activo
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            return "El usuario está inactivo";
        }


        // Comparar contraseña ingresada con contraseña encriptada
        if (passwordEncoder.matches(
                usuarioDTO.getContraseña(),
                usuario.getContraseñaEncriptada())) {

            return "Autenticación satisfactoria";
        }

        return "Contraseña incorrecta";
    }
}