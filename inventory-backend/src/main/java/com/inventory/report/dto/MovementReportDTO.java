package com.inventory.report.dto;

import java.util.List;

/**
 * Respuesta completa del reporte de movimientos.
 *
 * @param granularidad  DIA, SEMANA o MES: cómo se agrupó la serie
 *                      según la duración del período consultado.
 * @param serie         entradas y salidas (en unidades) por grupo.
 * @param topProductos  los 5 productos con más unidades movidas.
 * @param evolucionStock stock tras cada movimiento; solo se llena
 *                      cuando el reporte es de un único producto.
 */
public record MovementReportDTO(
        MovementReportSummaryDTO resumen,
        List<MovementReportRowDTO> items,
        List<String> filtros,
        String granularidad,
        List<SeriesPointDTO> serie,
        List<RankingItemDTO> topProductos,
        List<StockPointDTO> evolucionStock
) {
}
