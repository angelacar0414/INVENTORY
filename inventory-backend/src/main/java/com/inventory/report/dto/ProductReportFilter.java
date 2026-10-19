package com.inventory.report.dto;

/**
 * FILTROS DEL REPORTE DE PRODUCTOS
 * --------------------------------
 * Agrupa los filtros que el usuario puede elegir en pantalla.
 * Un filtro en null (o false en el caso de stockBajo) significa
 * "no filtrar por este criterio".
 *
 * Se declara como "record" (Java 17): es una clase de solo datos,
 * inmutable, que genera sola el constructor y los métodos de lectura.
 *
 * @param estado      DISPONIBLE, AGOTADO o INACTIVO (null = todos).
 * @param idProveedor proveedor del producto (null = todos).
 * @param idCategoria categoría del producto (null = todas).
 * @param stockBajo   true = solo productos activos con stock bajo
 *                    (mismo criterio del Dashboard: stock mayor que 0
 *                    y menor o igual al stock mínimo).
 */
public record ProductReportFilter(
        String estado,
        Long idProveedor,
        Long idCategoria,
        boolean stockBajo
) {
}
