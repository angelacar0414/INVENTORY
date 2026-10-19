package com.inventory.report.exception;

/**
 * Se lanza cuando los filtros de un reporte no son válidos
 * (fechas invertidas, estado inexistente, proveedor que no existe...).
 * El ReportExceptionHandler la convierte en una respuesta 400.
 */
public class InvalidReportException extends RuntimeException {

    public InvalidReportException(String message) {
        super(message);
    }
}
