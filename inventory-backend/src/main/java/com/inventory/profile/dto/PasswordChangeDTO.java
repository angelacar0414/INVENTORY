package com.inventory.profile.dto;

/**
 * Datos para cambiar la contraseña (RF-034).
 * Los nombres no llevan "ñ" para evitar problemas de codificación en el JSON.
 */
public record PasswordChangeDTO(
        String contrasenaActual,
        String contrasenaNueva,
        String confirmacion
) {
}
