package com.inventory.report.validator;

import com.inventory.product.entity.ProductStatus;
import com.inventory.report.dto.MovementReportFilter;
import com.inventory.report.dto.ProductReportFilter;
import com.inventory.report.exception.InvalidReportException;
import org.springframework.stereotype.Component;

/**
 * VALIDADOR DE FILTROS DE REPORTES
 * --------------------------------
 * Reglas que no dependen de la base de datos. Las que sí dependen
 * (que el proveedor o el producto existan) las valida el Service.
 */
@Component
public class ReportValidator {

    public void validarProductos(ProductReportFilter filtro) {

        if (filtro.estado() != null) {
            try {
                ProductStatus.valueOf(filtro.estado());
            } catch (IllegalArgumentException e) {
                throw new InvalidReportException(
                        "El estado debe ser DISPONIBLE, AGOTADO o INACTIVO.");
            }
        }
    }

    public void validarMovimientos(MovementReportFilter filtro) {

        if (filtro.tipo() != null
                && !filtro.tipo().equals("ENTRADA")
                && !filtro.tipo().equals("SALIDA")) {
            throw new InvalidReportException(
                    "El tipo de movimiento debe ser ENTRADA o SALIDA.");
        }

        if (filtro.desde() != null
                && filtro.hasta() != null
                && filtro.desde().isAfter(filtro.hasta())) {
            throw new InvalidReportException(
                    "La fecha inicial no puede ser posterior a la fecha final.");
        }

        // Solo las salidas tienen cliente. Pedir entradas de un cliente
        // nunca devolvería nada, así que avisamos en lugar de mostrar
        // un reporte vacío que confunda.
        if (filtro.idCliente() != null && "ENTRADA".equals(filtro.tipo())) {
            throw new InvalidReportException(
                    "El filtro de cliente solo aplica a las salidas.");
        }
    }
}
