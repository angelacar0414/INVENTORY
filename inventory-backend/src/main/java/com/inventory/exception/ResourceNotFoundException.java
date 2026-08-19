package com.inventory.exception;

/**
 * Se lanza cuando buscamos algo por ID (por ejemplo, una categoría)
 * y no existe en la base de datos.
 *
 * Es una excepción "compartida" (paquete exception) porque la van a
 * usar TODOS los módulos: categorías, proveedores, clientes, productos, etc.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}
