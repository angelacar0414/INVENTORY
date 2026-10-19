package com.inventory.report.dto;

import java.time.LocalDate;

/**
 * FILTROS DEL REPORTE DE MOVIMIENTOS
 * ----------------------------------
 * Todos son opcionales. Las fechas son inclusivas: un movimiento
 * hecho el día "hasta" a las 11:59 p. m. sí entra en el reporte.
 *
 * @param desde      primer día del período (null = sin límite).
 * @param hasta      último día del período (null = sin límite).
 * @param tipo       ENTRADA o SALIDA (null = ambos).
 * @param idProducto producto específico (null = todos).
 * @param idCliente  cliente específico; solo existe en las salidas.
 */
public record MovementReportFilter(
        LocalDate desde,
        LocalDate hasta,
        String tipo,
        Long idProducto,
        Long idCliente
) {
}
