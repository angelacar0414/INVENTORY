package com.inventory.movement.entity;

/**
 * Tipos de movimiento posibles dentro del inventario.
 *
 * ENTRADA: ingresan unidades al inventario, por lo tanto el stock aumenta.
 * SALIDA: se entregan unidades a un cliente, por lo tanto el stock disminuye.
 *
 * Los valores coinciden con el ENUM('ENTRADA', 'SALIDA') de la columna
 * tipo_movimiento de la tabla movimiento.
 */
public enum MovementType {

    ENTRADA,
    SALIDA
}
