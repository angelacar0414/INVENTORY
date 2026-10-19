package com.inventory.profile.service;

import com.inventory.auth.UsuarioEntity;
import com.inventory.auth.UsuarioRepository;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.profile.dto.PasswordChangeDTO;
import com.inventory.profile.dto.ProfileDTO;
import com.inventory.profile.dto.ProfileUpdateDTO;
import com.inventory.profile.exception.InvalidProfileException;
import com.inventory.profile.validator.ProfileValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * SERVICE DEL MÓDULO PERFIL
 * -------------------------
 * Cada usuario administra SU PROPIA información. El usuario nunca llega
 * por la URL: se toma de la sesión (el "username" autenticado). Así nadie
 * puede ver ni modificar el perfil de otra persona cambiando un número.
 *
 * Reglas de negocio (RF-032, RF-033, RF-034):
 *  - Solo se pueden cambiar nombre, apellido y correo.
 *  - El correo no puede estar en uso por otro usuario.
 *  - Para cambiar la contraseña hay que confirmar la actual.
 *  - La contraseña se guarda cifrada con BCrypt (nunca en texto plano).
 */
@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileValidator validator;

    public ProfileService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          ProfileValidator validator) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    /** RF-032: consultar el perfil del usuario autenticado. */
    @Transactional(readOnly = true)
    public ProfileDTO consultar(String username) {
        return aDto(buscar(username));
    }

    /** RF-033: actualizar nombre, apellido y correo. */
    @Transactional
    public ProfileDTO actualizar(String username, ProfileUpdateDTO datos) {
        validator.validarActualizacion(datos);

        UsuarioEntity usuario = buscar(username);
        String email = datos.email().trim();

        usuarioRepository.findByEmail(email).ifPresent(otro -> {
            if (!otro.getIdUsuario().equals(usuario.getIdUsuario())) {
                throw new DuplicateResourceException("El correo electrónico ya está registrado por otro usuario.");
            }
        });

        usuario.setNombre(datos.nombre().trim());
        usuario.setApellido(datos.apellido().trim());
        usuario.setEmail(email);
        usuario.setFechaActualizacion(LocalDateTime.now());

        UsuarioEntity guardado = usuarioRepository.save(usuario);
        log.info("Perfil actualizado: usuario '{}'", username);
        return aDto(guardado);
    }

    /** RF-034: cambiar la contraseña confirmando la actual. */
    @Transactional
    public void cambiarContrasena(String username, PasswordChangeDTO datos) {
        validator.validarCambioContrasena(datos);

        UsuarioEntity usuario = buscar(username);

        if (!passwordEncoder.matches(datos.contrasenaActual(), usuario.getContraseñaEncriptada())) {
            log.warn("Cambio de contraseña rechazado: contraseña actual incorrecta para '{}'", username);
            throw new InvalidProfileException("La contraseña actual no es correcta.");
        }

        usuario.setContraseñaEncriptada(passwordEncoder.encode(datos.contrasenaNueva()));
        usuario.setFechaActualizacion(LocalDateTime.now());
        usuarioRepository.save(usuario);

        log.info("Contraseña cambiada: usuario '{}'", username);
    }

    // ---------------------------------------------------------

    private UsuarioEntity buscar(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe."));
    }

    private ProfileDTO aDto(UsuarioEntity u) {
        return new ProfileDTO(
                u.getIdUsuario(),
                u.getNombre(),
                u.getApellido(),
                u.getUsername(),
                u.getEmail(),
                u.getRol(),
                Boolean.TRUE.equals(u.getActivo()),
                u.getFechaCreacion());
    }
}
