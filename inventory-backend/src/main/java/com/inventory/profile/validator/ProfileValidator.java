package com.inventory.profile.validator;

import com.inventory.profile.dto.PasswordChangeDTO;
import com.inventory.profile.dto.ProfileUpdateDTO;
import com.inventory.profile.exception.InvalidProfileException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * VALIDADOR DEL MÓDULO PERFIL
 * ---------------------------
 * Revisa que los datos recibidos tengan sentido ANTES de tocar la base de
 * datos. Las reglas que dependen de la base de datos (correo repetido,
 * contraseña actual correcta) se validan en el Service.
 */
@Component
public class ProfileValidator {

    public static final int LARGO_MINIMO_CONTRASENA = 6;

    private static final Pattern CORREO =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public void validarActualizacion(ProfileUpdateDTO datos) {
        if (datos == null) {
            throw new InvalidProfileException("No se recibieron datos para actualizar.");
        }
        if (vacio(datos.nombre())) {
            throw new InvalidProfileException("El nombre es obligatorio.");
        }
        if (vacio(datos.apellido())) {
            throw new InvalidProfileException("El apellido es obligatorio.");
        }
        if (vacio(datos.email())) {
            throw new InvalidProfileException("El correo electrónico es obligatorio.");
        }
        if (!CORREO.matcher(datos.email().trim()).matches()) {
            throw new InvalidProfileException("El correo electrónico no tiene un formato válido.");
        }
    }

    public void validarCambioContrasena(PasswordChangeDTO datos) {
        if (datos == null || vacio(datos.contrasenaActual())) {
            throw new InvalidProfileException("Debes escribir tu contraseña actual.");
        }
        if (vacio(datos.contrasenaNueva())) {
            throw new InvalidProfileException("Debes escribir la nueva contraseña.");
        }
        if (datos.contrasenaNueva().length() < LARGO_MINIMO_CONTRASENA) {
            throw new InvalidProfileException("La nueva contraseña debe tener al menos "
                    + LARGO_MINIMO_CONTRASENA + " caracteres.");
        }
        if (!datos.contrasenaNueva().equals(datos.confirmacion())) {
            throw new InvalidProfileException("La confirmación no coincide con la nueva contraseña.");
        }
        if (datos.contrasenaNueva().equals(datos.contrasenaActual())) {
            throw new InvalidProfileException("La nueva contraseña debe ser diferente a la actual.");
        }
    }

    private boolean vacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
