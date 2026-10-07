package com.inventory.movement.exception;

/**
 * Se lanza cuando un movimiento incumple una regla de negocio.
 *
 * Ejemplos:
 *
 * - Registrar una salida sin indicar el cliente.
 * - Registrar un movimiento con cantidad menor o igual a cero.
 * - Consultar el historial con un tipo de movimiento inexistente.
 *
 * MovementExceptionHandler la convierte en una respuesta HTTP 400.
 */
public class InvalidMovementException extends RuntimeException {

    public InvalidMovementException(String mensaje) {
        super(mensaje);
    }
}
