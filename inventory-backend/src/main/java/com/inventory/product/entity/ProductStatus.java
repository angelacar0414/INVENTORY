package com.inventory.product.entity;

/**
 * Estados posibles de un producto dentro del inventario.
 *
 * DISPONIBLE: el producto tiene existencias.
 * AGOTADO: el producto tiene stock igual a cero.
 * INACTIVO: el producto fue desactivado del sistema.
 */
public enum ProductStatus {

    DISPONIBLE,
    AGOTADO,
    INACTIVO
}