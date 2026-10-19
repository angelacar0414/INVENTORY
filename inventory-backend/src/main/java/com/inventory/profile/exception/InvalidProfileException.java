package com.inventory.profile.exception;

/**
 * Se lanza cuando los datos enviados en el módulo Perfil no cumplen las
 * reglas (campos vacíos, contraseña actual incorrecta, etc.).
 * El ProfileExceptionHandler la convierte en una respuesta 400.
 */
public class InvalidProfileException extends RuntimeException {

    public InvalidProfileException(String mensaje) {
        super(mensaje);
    }
}
