package com.inventory.report.controller;

import com.inventory.report.dto.MovementReportFilter;
import com.inventory.report.dto.ProductReportFilter;
import com.inventory.report.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CONTROLLER DE REPORTES
 * ----------------------
 * Base: /api/v1/reportes
 *
 * Endpoints (todos de consulta, método GET):
 *
 *   /productos          reporte en pantalla (JSON)
 *   /productos/pdf      el mismo reporte en PDF con gráficas
 *   /productos/excel    el mismo reporte en Excel
 *   /movimientos        reporte en pantalla (JSON)
 *   /movimientos/pdf    PDF con gráficas
 *   /movimientos/excel  Excel
 *
 * Los seis reciben los mismos filtros (como parámetros en la URL), así
 * lo que se ve en pantalla es exactamente lo que se descarga.
 *
 * Acceso: solo el rol ADMINISTRADOR (se define en SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/reportes")
public class ReportController {

    private static final String TIPO_PDF = "application/pdf";
    private static final String TIPO_EXCEL =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final DateTimeFormatter MARCA_DE_TIEMPO =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmm");

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // =========================================================
    // PRODUCTOS
    // =========================================================

    @GetMapping("/productos")
    public ResponseEntity<Map<String, Object>> reporteProductos(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long idProveedor,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(defaultValue = "false") boolean stockBajo) {

        ProductReportFilter filtro =
                filtroProductos(estado, idProveedor, idCategoria, stockBajo);

        return ResponseEntity.ok(success(
                "Reporte de productos generado correctamente",
                reportService.reporteProductos(filtro)));
    }

    @GetMapping("/productos/pdf")
    public ResponseEntity<byte[]> productosPdf(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long idProveedor,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(defaultValue = "false") boolean stockBajo,
            Authentication authentication) {

        ProductReportFilter filtro =
                filtroProductos(estado, idProveedor, idCategoria, stockBajo);

        byte[] pdf = reportService.exportarProductosPdf(filtro, authentication.getName());

        return archivo(pdf, TIPO_PDF, "reporte-productos", "pdf");
    }

    @GetMapping("/productos/excel")
    public ResponseEntity<byte[]> productosExcel(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long idProveedor,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(defaultValue = "false") boolean stockBajo,
            Authentication authentication) {

        ProductReportFilter filtro =
                filtroProductos(estado, idProveedor, idCategoria, stockBajo);

        byte[] excel = reportService.exportarProductosExcel(filtro, authentication.getName());

        return archivo(excel, TIPO_EXCEL, "reporte-productos", "xlsx");
    }

    // =========================================================
    // MOVIMIENTOS
    // =========================================================

    @GetMapping("/movimientos")
    public ResponseEntity<Map<String, Object>> reporteMovimientos(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long idProducto,
            @RequestParam(required = false) Long idCliente) {

        MovementReportFilter filtro =
                filtroMovimientos(desde, hasta, tipo, idProducto, idCliente);

        return ResponseEntity.ok(success(
                "Reporte de movimientos generado correctamente",
                reportService.reporteMovimientos(filtro)));
    }

    @GetMapping("/movimientos/pdf")
    public ResponseEntity<byte[]> movimientosPdf(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long idProducto,
            @RequestParam(required = false) Long idCliente,
            Authentication authentication) {

        MovementReportFilter filtro =
                filtroMovimientos(desde, hasta, tipo, idProducto, idCliente);

        byte[] pdf = reportService.exportarMovimientosPdf(filtro, authentication.getName());

        return archivo(pdf, TIPO_PDF, "reporte-movimientos", "pdf");
    }

    @GetMapping("/movimientos/excel")
    public ResponseEntity<byte[]> movimientosExcel(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long idProducto,
            @RequestParam(required = false) Long idCliente,
            Authentication authentication) {

        MovementReportFilter filtro =
                filtroMovimientos(desde, hasta, tipo, idProducto, idCliente);

        byte[] excel = reportService.exportarMovimientosExcel(filtro, authentication.getName());

        return archivo(excel, TIPO_EXCEL, "reporte-movimientos", "xlsx");
    }

    // =========================================================
    // APOYO
    // =========================================================

    /** Limpia los textos que llegan por la URL: espacios y mayúsculas. */
    private ProductReportFilter filtroProductos(String estado, Long idProveedor,
                                                Long idCategoria, boolean stockBajo) {
        return new ProductReportFilter(
                normalizar(estado), idProveedor, idCategoria, stockBajo);
    }

    private MovementReportFilter filtroMovimientos(LocalDate desde, LocalDate hasta,
                                                   String tipo, Long idProducto,
                                                   Long idCliente) {
        return new MovementReportFilter(
                desde, hasta, normalizar(tipo), idProducto, idCliente);
    }

    /** Un texto vacío significa "sin filtro"; el resto se compara en mayúsculas. */
    private String normalizar(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim().toUpperCase();
    }

    /** Respuesta de descarga: el navegador la guarda como archivo. */
    private ResponseEntity<byte[]> archivo(byte[] contenido, String tipoMime,
                                           String nombreBase, String extension) {

        String nombre = nombreBase + "-"
                + LocalDateTime.now().format(MARCA_DE_TIEMPO) + "." + extension;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + nombre + "\"")
                .contentType(MediaType.parseMediaType(tipoMime))
                .body(contenido);
    }

    private Map<String, Object> success(String message, Object data) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", message);
        body.put("data", data);
        return body;
    }
}
