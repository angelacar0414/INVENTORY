package com.inventory.report.dto;

import java.time.LocalDateTime;

/**
 * Una fila del reporte de movimientos.
 */
public record MovementReportRowDTO(
        Long idMovimiento,
        LocalDateTime fecha,
        String tipo,
        String codigoProducto,
        String nombreProducto,
        int cantidad,
        int stockAnterior,
        int stockPosterior,
        String cliente,
        String usuario,
        String observacion
) {
}
