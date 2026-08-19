package com.inventory.exception;

/**
 * Se lanza cuando se intenta registrar algo que ya existe
 * (por ejemplo, una categoría con un nombre repetido -> RF-9).
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String mensaje) {
        super(mensaje);
    }
}
