package com.inventory.report.dto;

import java.util.List;

/**
 * Respuesta completa del reporte de productos.
 *
 * @param resumen indicadores generales.
 * @param items   filas del reporte (ya filtradas).
 * @param filtros descripción legible de los filtros aplicados,
 *                por ejemplo "Estado: AGOTADO".
 */
public record ProductReportDTO(
        ProductReportSummaryDTO resumen,
        List<ProductReportRowDTO> items,
        List<String> filtros
) {
}
