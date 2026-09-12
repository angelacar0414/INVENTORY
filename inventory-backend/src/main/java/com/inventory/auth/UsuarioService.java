package com.inventory.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio de Usuario
 * Contiene la lógica de negocio para registro e inicio de sesión
 *
 * @author Darío Bustamante
 * @version 1.0
 */
@Service
public class UsuarioService {

    // ==================== INYECCIONES ====================

    /** Repository de Usuario para acceder a la base de datos */
    @Autowired
    private UsuarioRepository usuarioRepository;

    /** Codificador de contraseñas BCrypt */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ==================== MÉTODOS ====================

    /**
     * Registra un nuevo usuario en el sistema
     * Valida que el email no esté duplicado y encripta la contraseña
     *
     * @param usuarioDTO datos del nuevo usuario (email y contraseña)
     * @return mensaje de éxito o error
     */
    public String registrar(UsuarioDTO usuarioDTO) {

        // Validar que el email no esté vacío
        if (usuarioDTO.getEmail() == null || usuarioDTO.getEmail().trim().isEmpty()) {
            return "El email es obligatorio";
        }

        // Validar que la contraseña no esté vacía
        if (usuarioDTO.getContraseña() == null || usuarioDTO.getContraseña().trim().isEmpty()) {
            return "La contraseña es obligatoria";
        }

        // Verificar que el email no esté duplicado
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            return "El email ya está registrado";
        }

        // Crear nuevo usuario
        UsuarioEntity nuevoUsuario = new UsuarioEntity();
        nuevoUsuario.setEmail(usuarioDTO.getEmail());

        // Encriptar la contraseña con BCrypt
        String contraseñaEncriptada = passwordEncoder.encode(usuarioDTO.getContraseña());
        nuevoUsuario.setContraseñaEncriptada(contraseñaEncriptada);

        // Establecer fechas
        nuevoUsuario.setFechaCreacion(LocalDateTime.now());
        nuevoUsuario.setFechaActualizacion(LocalDateTime.now());
        nuevoUsuario.setActivo(true);

        // Guardar en la base de datos
        usuarioRepository.save(nuevoUsuario);

        return "Usuario registrado correctamente";
    }

    /**
     * Autentica un usuario validando email y contraseña
     *
     * @param usuarioDTO credenciales del usuario (email y contraseña)
     * @return mensaje de autenticación exitosa o error
     */
    public String autenticar(UsuarioDTO usuarioDTO) {

        // Validar que el email no esté vacío
        if (usuarioDTO.getEmail() == null || usuarioDTO.getEmail().trim().isEmpty()) {
            return "El email es obligatorio";
        }

        // Validar que la contraseña no esté vacía
        if (usuarioDTO.getContraseña() == null || usuarioDTO.getContraseña().trim().isEmpty()) {
            return "La contraseña es obligatoria";
        }

        // Buscar el usuario por email
        Optional<UsuarioEntity> usuarioOptional = usuarioRepository.findByEmail(usuarioDTO.getEmail());

        // Verificar que el usuario existe
        if (usuarioOptional.isEmpty()) {
            return "El usuario no existe";
        }

        UsuarioEntity usuario = usuarioOptional.get();

        // Verificar que el usuario está activo
        if (!usuario.getActivo()) {
            return "El usuario está inactivo";
        }

        // Validar la contraseña comparando la ingresada con la encriptada
        if (passwordEncoder.matches(usuarioDTO.getContraseña(), usuario.getContraseñaEncriptada())) {
            return "Autenticación satisfactoria";
        } else {
            return "Contraseña incorrecta";
        }
    }
}