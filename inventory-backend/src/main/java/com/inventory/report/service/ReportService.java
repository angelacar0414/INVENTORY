package com.inventory.report.service;

import com.inventory.auth.UsuarioEntity;
import com.inventory.auth.UsuarioRepository;
import com.inventory.report.dto.*;
import com.inventory.report.exception.InvalidReportException;
import com.inventory.report.export.ExcelReportExporter;
import com.inventory.report.export.PdfReportExporter;
import com.inventory.report.repository.ReportRepository;
import com.inventory.report.validator.ReportValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * SERVICE DE REPORTES
 * -------------------
 * Aquí vive la lógica de los reportes:
 *
 * 1. Validar los filtros.
 * 2. Pedir los datos al Repository.
 * 3. Calcular los indicadores (resumen, serie por período, ranking).
 * 4. Entregar esos datos a la pantalla (JSON) o a los exportadores
 *    (PDF y Excel).
 *
 * Es de solo lectura, por eso las consultas usan readOnly = true.
 */
@Service
public class ReportService {

    /** Hasta 31 días se agrupa por día; hasta 180, por semana; más, por mes. */
    private static final int MAX_DIAS_AGRUPADOS_POR_DIA = 31;
    private static final int MAX_DIAS_AGRUPADOS_POR_SEMANA = 180;
    private static final int TAMANO_TOP = 5;

    private static final String[] MESES = {
            "ene", "feb", "mar", "abr", "may", "jun",
            "jul", "ago", "sep", "oct", "nov", "dic"
    };

    private final ReportRepository reportRepository;
    private final ReportValidator reportValidator;
    private final UsuarioRepository usuarioRepository;
    private final PdfReportExporter pdfExporter;
    private final ExcelReportExporter excelExporter;

    public ReportService(ReportRepository reportRepository,
                         ReportValidator reportValidator,
                         UsuarioRepository usuarioRepository,
                         PdfReportExporter pdfExporter,
                         ExcelReportExporter excelExporter) {
        this.reportRepository = reportRepository;
        this.reportValidator = reportValidator;
        this.usuarioRepository = usuarioRepository;
        this.pdfExporter = pdfExporter;
        this.excelExporter = excelExporter;
    }

    // =========================================================
    // REPORTE DE PRODUCTOS
    // =========================================================

    @Transactional(readOnly = true)
    public ProductReportDTO reporteProductos(ProductReportFilter filtro) {

        reportValidator.validarProductos(filtro);

        List<String> filtros = new ArrayList<>();

        if (filtro.estado() != null) {
            filtros.add("Estado: " + filtro.estado());
        }

        if (filtro.idProveedor() != null) {
            String nombre = reportRepository.nombreProveedor(filtro.idProveedor())
                    .orElseThrow(() -> new InvalidReportException(
                            "El proveedor seleccionado no existe."));
            filtros.add("Proveedor: " + nombre);
        }

        if (filtro.idCategoria() != null) {
            String nombre = reportRepository.nombreCategoria(filtro.idCategoria())
                    .orElseThrow(() -> new InvalidReportException(
                            "La categoría seleccionada no existe."));
            filtros.add("Categoría: " + nombre);
        }

        if (filtro.stockBajo()) {
            filtros.add("Solo stock bajo");
        }

        List<ProductReportRowDTO> items = reportRepository.consultarProductos(filtro);

        return new ProductReportDTO(resumirProductos(items), items, filtros);
    }

    /** Calcula los indicadores a partir de las filas ya filtradas. */
    ProductReportSummaryDTO resumirProductos(List<ProductReportRowDTO> items) {

        long disponibles = 0;
        long agotados = 0;
        long inactivos = 0;
        long stockBajo = 0;
        long unidades = 0;
        BigDecimal valor = BigDecimal.ZERO;

        for (ProductReportRowDTO fila : items) {

            switch (fila.estado()) {
                case "DISPONIBLE" -> disponibles++;
                case "AGOTADO" -> agotados++;
                case "INACTIVO" -> inactivos++;
                default -> { }
            }

            if (fila.stockBajo()) {
                stockBajo++;
            }

            // Solo los productos activos cuentan como inventario.
            if (!"INACTIVO".equals(fila.estado())) {
                unidades += fila.stockActual();
                valor = valor.add(fila.precioCompra()
                        .multiply(BigDecimal.valueOf(fila.stockActual())));
            }
        }

        return new ProductReportSummaryDTO(
                items.size(), disponibles, agotados, inactivos,
                stockBajo, unidades, valor);
    }

    // =========================================================
    // REPORTE DE MOVIMIENTOS
    // =========================================================

    @Transactional(readOnly = true)
    public MovementReportDTO reporteMovimientos(MovementReportFilter filtro) {

        reportValidator.validarMovimientos(filtro);

        List<String> filtros = new ArrayList<>();

        if (filtro.desde() != null) {
            filtros.add("Desde: " + formatoFecha(filtro.desde()));
        }

        if (filtro.hasta() != null) {
            filtros.add("Hasta: " + formatoFecha(filtro.hasta()));
        }

        if (filtro.tipo() != null) {
            filtros.add("Tipo: " + filtro.tipo());
        }

        if (filtro.idProducto() != null) {
            String nombre = reportRepository.descripcionProducto(filtro.idProducto())
                    .orElseThrow(() -> new InvalidReportException(
                            "El producto seleccionado no existe."));
            filtros.add("Producto: " + nombre);
        }

        if (filtro.idCliente() != null) {
            String nombre = reportRepository.nombreCliente(filtro.idCliente())
                    .orElseThrow(() -> new InvalidReportException(
                            "El cliente seleccionado no existe."));
            filtros.add("Cliente: " + nombre);
        }

        List<MovementReportRowDTO> items = reportRepository.consultarMovimientos(filtro);

        String granularidad = elegirGranularidad(filtro, items);

        return new MovementReportDTO(
                resumirMovimientos(items),
                items,
                filtros,
                granularidad,
                construirSerie(filtro, items, granularidad),
                construirTop(items),
                construirEvolucion(filtro, items)
        );
    }

    MovementReportSummaryDTO resumirMovimientos(List<MovementReportRowDTO> items) {

        long entradas = 0;
        long salidas = 0;
        long unidadesEntradas = 0;
        long unidadesSalidas = 0;

        for (MovementReportRowDTO fila : items) {
            if ("ENTRADA".equals(fila.tipo())) {
                entradas++;
                unidadesEntradas += fila.cantidad();
            } else {
                salidas++;
                unidadesSalidas += fila.cantidad();
            }
        }

        return new MovementReportSummaryDTO(
                items.size(), entradas, salidas,
                unidadesEntradas, unidadesSalidas,
                unidadesEntradas - unidadesSalidas);
    }

    // ---------------------------------------------------------
    // Serie de tiempo: entradas y salidas por día / semana / mes
    // ---------------------------------------------------------

    /** Primer y último día que cubre el reporte (null si no hay datos). */
    private LocalDate[] rango(MovementReportFilter filtro,
                              List<MovementReportRowDTO> items) {

        if (items.isEmpty()) {
            return null;
        }

        LocalDate minimo = null;
        LocalDate maximo = null;

        for (MovementReportRowDTO fila : items) {
            LocalDate dia = fila.fecha().toLocalDate();
            if (minimo == null || dia.isBefore(minimo)) minimo = dia;
            if (maximo == null || dia.isAfter(maximo)) maximo = dia;
        }

        LocalDate inicio = filtro.desde() != null ? filtro.desde() : minimo;
        LocalDate fin = filtro.hasta() != null ? filtro.hasta() : maximo;

        return new LocalDate[]{inicio, fin};
    }

    String elegirGranularidad(MovementReportFilter filtro,
                              List<MovementReportRowDTO> items) {

        LocalDate[] limites = rango(filtro, items);

        if (limites == null) {
            return "DIA";
        }

        long dias = ChronoUnit.DAYS.between(limites[0], limites[1]) + 1;

        if (dias <= MAX_DIAS_AGRUPADOS_POR_DIA) return "DIA";
        if (dias <= MAX_DIAS_AGRUPADOS_POR_SEMANA) return "SEMANA";
        return "MES";
    }

    /** Primer día del grupo al que pertenece una fecha. */
    private LocalDate inicioDelGrupo(LocalDate fecha, String granularidad) {
        return switch (granularidad) {
            case "SEMANA" -> fecha.with(
                    TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case "MES" -> fecha.withDayOfMonth(1);
            default -> fecha;
        };
    }

    private LocalDate siguienteGrupo(LocalDate inicio, String granularidad) {
        return switch (granularidad) {
            case "SEMANA" -> inicio.plusWeeks(1);
            case "MES" -> inicio.plusMonths(1);
            default -> inicio.plusDays(1);
        };
    }

    private String etiquetaDelGrupo(LocalDate inicio, String granularidad) {
        if ("MES".equals(granularidad)) {
            return MESES[inicio.getMonthValue() - 1] + " "
                    + String.format("%02d", inicio.getYear() % 100);
        }
        return String.format("%02d/%02d",
                inicio.getDayOfMonth(), inicio.getMonthValue());
    }

    List<SeriesPointDTO> construirSerie(MovementReportFilter filtro,
                                        List<MovementReportRowDTO> items,
                                        String granularidad) {

        LocalDate[] limites = rango(filtro, items);

        if (limites == null) {
            return List.of();
        }

        // Sumamos las unidades de cada grupo (TreeMap = ordenado por fecha).
        Map<LocalDate, long[]> acumulado = new TreeMap<>();

        for (MovementReportRowDTO fila : items) {
            LocalDate grupo = inicioDelGrupo(
                    fila.fecha().toLocalDate(), granularidad);
            long[] valores = acumulado.computeIfAbsent(grupo, k -> new long[2]);
            if ("ENTRADA".equals(fila.tipo())) {
                valores[0] += fila.cantidad();
            } else {
                valores[1] += fila.cantidad();
            }
        }

        // Recorremos todos los grupos del período, incluso los que no
        // tuvieron movimientos, para que la gráfica no "salte" días.
        List<SeriesPointDTO> serie = new ArrayList<>();
        LocalDate actual = inicioDelGrupo(limites[0], granularidad);
        LocalDate ultimo = inicioDelGrupo(limites[1], granularidad);

        while (!actual.isAfter(ultimo)) {
            long[] valores = acumulado.getOrDefault(actual, new long[2]);
            serie.add(new SeriesPointDTO(
                    etiquetaDelGrupo(actual, granularidad),
                    actual, valores[0], valores[1]));
            actual = siguienteGrupo(actual, granularidad);
        }

        return serie;
    }

    // ---------------------------------------------------------
    // Ranking y evolución
    // ---------------------------------------------------------

    List<RankingItemDTO> construirTop(List<MovementReportRowDTO> items) {

        Map<String, Long> unidadesPorProducto = new LinkedHashMap<>();

        for (MovementReportRowDTO fila : items) {
            unidadesPorProducto.merge(
                    fila.nombreProducto(), (long) fila.cantidad(), Long::sum);
        }

        return unidadesPorProducto.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(TAMANO_TOP)
                .map(e -> new RankingItemDTO(e.getKey(), e.getValue()))
                .toList();
    }

    List<StockPointDTO> construirEvolucion(MovementReportFilter filtro,
                                           List<MovementReportRowDTO> items) {

        // Solo tiene sentido cuando el reporte es de un único producto.
        if (filtro.idProducto() == null || items.size() < 2) {
            return List.of();
        }

        List<StockPointDTO> puntos = new ArrayList<>();

        // Los items vienen del más reciente al más antiguo; los
        // recorremos al revés para dibujar el tiempo de izquierda a derecha.
        for (int i = items.size() - 1; i >= 0; i--) {
            MovementReportRowDTO fila = items.get(i);
            puntos.add(new StockPointDTO(fila.fecha(), fila.stockPosterior()));
        }

        return puntos;
    }

    // =========================================================
    // EXPORTACIÓN
    // =========================================================

    public byte[] exportarProductosPdf(ProductReportFilter filtro, String username) {
        ProductReportDTO reporte = reporteProductos(filtro);
        return pdfExporter.productos(reporte, nombreUsuario(username), LocalDateTime.now());
    }

    public byte[] exportarProductosExcel(ProductReportFilter filtro, String username) {
        ProductReportDTO reporte = reporteProductos(filtro);
        return excelExporter.productos(reporte, nombreUsuario(username), LocalDateTime.now());
    }

    public byte[] exportarMovimientosPdf(MovementReportFilter filtro, String username) {
        MovementReportDTO reporte = reporteMovimientos(filtro);
        return pdfExporter.movimientos(reporte, nombreUsuario(username), LocalDateTime.now());
    }

    public byte[] exportarMovimientosExcel(MovementReportFilter filtro, String username) {
        MovementReportDTO reporte = reporteMovimientos(filtro);
        return excelExporter.movimientos(reporte, nombreUsuario(username), LocalDateTime.now());
    }

    /** Nombre completo de quien genera el reporte (o su username si no se encuentra). */
    String nombreUsuario(String username) {
        return usuarioRepository.findByUsername(username)
                .map((UsuarioEntity u) -> (u.getNombre() + " " + u.getApellido()).trim())
                .orElse(username);
    }

    private String formatoFecha(LocalDate fecha) {
        return String.format("%02d/%02d/%d",
                fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear());
    }
}
