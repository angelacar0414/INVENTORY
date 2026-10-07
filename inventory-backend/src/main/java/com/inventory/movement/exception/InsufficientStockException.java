package com.inventory.movement.exception;

/**
 * Se lanza cuando se intenta registrar una salida por una cantidad
 * mayor al stock disponible del producto.
 *
 * Es un caso particular de InvalidMovementException, por lo que también
 * se responde con HTTP 400. Cuando esto ocurre no se modifica el stock ni
 * se registra ningún movimiento.
 */
public class InsufficientStockException extends InvalidMovementException {

    public InsufficientStockException(String mensaje) {
        super(mensaje);
    }
}
