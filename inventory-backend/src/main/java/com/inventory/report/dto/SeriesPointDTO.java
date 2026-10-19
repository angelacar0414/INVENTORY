package com.inventory.report.dto;

import java.time.LocalDate;

/**
 * Un punto de la serie de tiempo (entradas y salidas por día,
 * semana o mes). Se usa para la gráfica de columnas del PDF.
 *
 * @param etiqueta texto corto para el eje, por ejemplo "07/10".
 * @param inicio   primer día del grupo.
 */
public record SeriesPointDTO(
        String etiqueta,
        LocalDate inicio,
        long entradas,
        long salidas
) {
}
