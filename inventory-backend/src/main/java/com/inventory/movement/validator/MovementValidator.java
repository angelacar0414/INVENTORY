package com.inventory.movement.validator;

import com.inventory.movement.exception.InsufficientStockException;
import com.inventory.movement.exception.InvalidMovementException;
import org.springframework.stereotype.Component;

/**
 * Validator del módulo Movimientos.
 *
 * Concentra las reglas de negocio propias de los movimientos que van más
 * allá de las validaciones básicas del DTO (@NotNull, @Min, @Size).
 *
 * Este módulo sí necesita Validator porque algunas reglas dependen del
 * tipo de movimiento y del stock del producto, algo que las anotaciones
 * del DTO no pueden comprobar.
 *
 * La clase NO consulta la base de datos: recibe los valores ya
 * consultados por MovementService. Cada método lanza una excepción si la
 * regla se incumple y no hace nada si todo está correcto.
 */
@Component
public class MovementValidator {

    /**
     * Verifica que la cantidad exista y sea mayor que cero.
     *
     * El DTO ya valida esto con @Min(1), pero la validación del backend
     * es la autoridad final, así que se repite aquí.
     *
     * @param cantidad unidades del movimiento
     */
    public void validarCantidad(Integer cantidad) {

        if (cantidad == null || cantidad <= 0) {
            throw new InvalidMovementException(
                    "La cantidad debe ser mayor que cero."
            );
        }
    }

    /**
     * Verifica que se haya indicado un cliente.
     *
     * Regla del negocio: el cliente es obligatorio únicamente en las
     * SALIDAS.
     *
     * @param idCliente identificador del cliente enviado por el frontend
     */
    public void validarClienteObligatorio(Long idCliente) {

        if (idCliente == null) {
            throw new InvalidMovementException(
                    "El cliente es obligatorio para registrar una salida."
            );
        }
    }

    /**
     * Verifica que haya unidades suficientes para realizar una salida.
     *
     * Regla del negocio: el stock nunca puede quedar negativo.
     *
     * @param stockActual unidades disponibles del producto
     * @param cantidad unidades que se quieren sacar
     */
    public void validarStockSuficiente(int stockActual, int cantidad) {

        if (cantidad > stockActual) {
            throw new InsufficientStockException(
                    "Stock insuficiente. Disponible: " + stockActual
                            + ", solicitado: " + cantidad + "."
            );
        }
    }

    /**
     * Verifica que una entrada no desborde el máximo que soporta
     * el campo de stock (INT).
     *
     * Se calcula con long para que la suma no se desborde antes
     * de poder compararla.
     *
     * @param stockActual unidades disponibles del producto
     * @param cantidad unidades que se quieren ingresar
     */
    public void validarCapacidad(int stockActual, int cantidad) {

        long nuevoStock = (long) stockActual + cantidad;

        if (nuevoStock > Integer.MAX_VALUE) {
            throw new InvalidMovementException(
                    "La cantidad ingresada supera el stock máximo permitido."
            );
        }
    }
}
