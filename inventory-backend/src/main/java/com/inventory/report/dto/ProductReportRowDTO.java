package com.inventory.report.dto;

import java.math.BigDecimal;

/**
 * Una fila del reporte de productos.
 */
public record ProductReportRowDTO(
        Long idProducto,
        String codigo,
        String nombre,
        String categoria,
        String proveedor,
        int stockActual,
        int stockMinimo,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        String estado,
        boolean stockBajo
) {
}
