package com.inventory.report.dto;

import java.math.BigDecimal;

/**
 * Indicadores que resumen el reporte de productos.
 *
 * unidadesEnStock y valorInventario solo cuentan productos activos,
 * porque un producto desactivado ya no hace parte del inventario.
 * El valor se calcula a precio de compra (lo que costó el stock).
 */
public record ProductReportSummaryDTO(
        long totalProductos,
        long disponibles,
        long agotados,
        long inactivos,
        long stockBajo,
        long unidadesEnStock,
        BigDecimal valorInventario
) {
}
