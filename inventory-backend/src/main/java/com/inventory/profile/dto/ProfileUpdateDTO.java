package com.inventory.profile.dto;

/**
 * Datos personales que el usuario puede cambiar en su perfil (RF-033).
 * El username y el rol NO se pueden modificar desde aquí.
 */
public record ProfileUpdateDTO(
        String nombre,
        String apellido,
        String email
) {
}
