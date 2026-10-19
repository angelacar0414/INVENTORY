package com.inventory.profile.dto;

import java.time.LocalDateTime;

/**
 * Datos del perfil que se envían al Frontend.
 * Nunca incluye la contraseña (ni siquiera cifrada).
 */
public record ProfileDTO(
        Long idUsuario,
        String nombre,
        String apellido,
        String username,
        String email,
        String rol,
        boolean activo,
        LocalDateTime fechaCreacion
) {
}
