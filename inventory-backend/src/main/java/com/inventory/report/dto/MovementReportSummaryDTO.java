package com.inventory.report.dto;

/**
 * Indicadores que resumen el reporte de movimientos.
 *
 * balanceNeto = unidades que entraron menos unidades que salieron.
 * Un valor positivo significa que el inventario creció en el período.
 */
public record MovementReportSummaryDTO(
        long totalMovimientos,
        long totalEntradas,
        long totalSalidas,
        long unidadesEntradas,
        long unidadesSalidas,
        long balanceNeto
) {
}
