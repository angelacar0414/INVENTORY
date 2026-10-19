package com.inventory.report.dto;

import java.time.LocalDateTime;

/**
 * Stock de un producto justo después de un movimiento.
 * Con varios puntos se dibuja la evolución del stock.
 */
public record StockPointDTO(
        LocalDateTime fecha,
        int stock
) {
}
