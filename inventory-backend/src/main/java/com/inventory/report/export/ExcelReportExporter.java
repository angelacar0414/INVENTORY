package com.inventory.report.export;

import com.inventory.report.dto.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * EXPORTADOR DE REPORTES A EXCEL
 * ------------------------------
 * Genera un archivo .xlsx con Apache POI. Cada reporte tiene dos hojas:
 *
 *   - "Resumen": quién lo generó, filtros e indicadores.
 *   - "Datos":   la tabla completa, lista para filtrar, ordenar o
 *                usar en tablas dinámicas (una fila de encabezado y
 *                una fila por registro, sin celdas combinadas).
 *
 * Los números se guardan como números y las fechas como fechas
 * (no como texto) para que Excel pueda operar con ellos.
 */
@Component
public class ExcelReportExporter {

    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =========================================================
    // REPORTE DE PRODUCTOS
    // =========================================================

    public byte[] productos(ProductReportDTO reporte,
                            String generadoPor,
                            LocalDateTime generadoEn) {

        try (XSSFWorkbook libro = new XSSFWorkbook();
             ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            Estilos estilos = new Estilos(libro);
            ProductReportSummaryDTO r = reporte.resumen();

            // ---------- Hoja "Resumen" ----------
            Sheet resumen = libro.createSheet("Resumen");
            int fila = encabezadoResumen(resumen, estilos, "Reporte de productos",
                    generadoPor, generadoEn, reporte.filtros());

            fila = indicador(resumen, estilos, fila, "Total de productos", r.totalProductos(), false);
            fila = indicador(resumen, estilos, fila, "Disponibles", r.disponibles(), false);
            fila = indicador(resumen, estilos, fila, "Agotados", r.agotados(), false);
            fila = indicador(resumen, estilos, fila, "Inactivos", r.inactivos(), false);
            fila = indicador(resumen, estilos, fila, "Con stock bajo", r.stockBajo(), false);
            fila = indicador(resumen, estilos, fila, "Unidades en stock (activos)", r.unidadesEnStock(), false);

            Row filaValor = resumen.createRow(fila++);
            texto(filaValor, 0, "Valor del inventario a precio de compra (activos)", estilos.etiqueta);
            numero(filaValor, 1, r.valorInventario().doubleValue(), estilos.dineroDestacado);

            anchos(resumen, 46, 22);

            // ---------- Hoja "Datos" ----------
            Sheet datos = libro.createSheet("Datos");
            String[] encabezados = {"Código", "Producto", "Categoría", "Proveedor",
                    "Stock actual", "Stock mínimo", "Estado", "Stock bajo",
                    "Precio compra", "Precio venta"};
            encabezadoTabla(datos, estilos, encabezados);

            int numeroFila = 1;
            for (ProductReportRowDTO p : reporte.items()) {
                Row f = datos.createRow(numeroFila++);
                texto(f, 0, p.codigo(), estilos.texto);
                texto(f, 1, p.nombre(), estilos.texto);
                texto(f, 2, p.categoria(), estilos.texto);
                texto(f, 3, p.proveedor(), estilos.texto);
                numero(f, 4, p.stockActual(), estilos.entero);
                numero(f, 5, p.stockMinimo(), estilos.entero);
                texto(f, 6, p.estado(), estilos.estado(p.estado(), p.stockBajo()));
                texto(f, 7, p.stockBajo() ? "SI" : "NO",
                        p.stockBajo() ? estilos.estadoAlerta : estilos.texto);
                numero(f, 8, valor(p.precioCompra()), estilos.dinero);
                numero(f, 9, valor(p.precioVenta()), estilos.dinero);
            }

            cerrarTabla(datos, encabezados.length, numeroFila,
                    new int[]{14, 36, 22, 28, 14, 14, 16, 12, 18, 18});

            libro.write(salida);
            return salida.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "No fue posible generar el archivo Excel del reporte.", e);
        }
    }

    // =========================================================
    // REPORTE DE MOVIMIENTOS
    // =========================================================

    public byte[] movimientos(MovementReportDTO reporte,
                              String generadoPor,
                              LocalDateTime generadoEn) {

        try (XSSFWorkbook libro = new XSSFWorkbook();
             ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            Estilos estilos = new Estilos(libro);
            MovementReportSummaryDTO r = reporte.resumen();

            // ---------- Hoja "Resumen" ----------
            Sheet resumen = libro.createSheet("Resumen");
            int fila = encabezadoResumen(resumen, estilos, "Reporte de movimientos",
                    generadoPor, generadoEn, reporte.filtros());

            fila = indicador(resumen, estilos, fila, "Total de movimientos", r.totalMovimientos(), false);
            fila = indicador(resumen, estilos, fila, "Entradas (registros)", r.totalEntradas(), false);
            fila = indicador(resumen, estilos, fila, "Salidas (registros)", r.totalSalidas(), false);
            fila = indicador(resumen, estilos, fila, "Unidades que entraron", r.unidadesEntradas(), false);
            fila = indicador(resumen, estilos, fila, "Unidades que salieron", r.unidadesSalidas(), false);
            fila = indicador(resumen, estilos, fila, "Balance neto (entradas - salidas)", r.balanceNeto(), true);

            // Serie por día / semana / mes
            fila++;
            Row tituloSerie = resumen.createRow(fila++);
            texto(tituloSerie, 0, "Unidades por " + switch (reporte.granularidad()) {
                case "SEMANA" -> "semana (lunes de cada semana)";
                case "MES" -> "mes";
                default -> "día";
            }, estilos.seccion);

            Row cabeceraSerie = resumen.createRow(fila++);
            texto(cabeceraSerie, 0, "Período", estilos.encabezado);
            texto(cabeceraSerie, 1, "Entradas", estilos.encabezado);
            texto(cabeceraSerie, 2, "Salidas", estilos.encabezado);

            for (SeriesPointDTO punto : reporte.serie()) {
                Row f = resumen.createRow(fila++);
                texto(f, 0, punto.etiqueta(), estilos.texto);
                numero(f, 1, punto.entradas(), estilos.entero);
                numero(f, 2, punto.salidas(), estilos.entero);
            }

            // Ranking de productos
            if (!reporte.topProductos().isEmpty()) {
                fila++;
                Row tituloTop = resumen.createRow(fila++);
                texto(tituloTop, 0, "Productos con más unidades movidas", estilos.seccion);

                Row cabeceraTop = resumen.createRow(fila++);
                texto(cabeceraTop, 0, "Producto", estilos.encabezado);
                texto(cabeceraTop, 1, "Unidades", estilos.encabezado);

                for (RankingItemDTO item : reporte.topProductos()) {
                    Row f = resumen.createRow(fila++);
                    texto(f, 0, item.nombre(), estilos.texto);
                    numero(f, 1, item.valor(), estilos.entero);
                }
            }

            anchos(resumen, 46, 22, 22);

            // ---------- Hoja "Datos" ----------
            Sheet datos = libro.createSheet("Datos");
            String[] encabezados = {"Fecha", "Tipo", "Código", "Producto", "Cantidad",
                    "Stock anterior", "Stock posterior", "Cliente", "Usuario", "Observación"};
            encabezadoTabla(datos, estilos, encabezados);

            int numeroFila = 1;
            for (MovementReportRowDTO m : reporte.items()) {
                Row f = datos.createRow(numeroFila++);

                Cell celdaFecha = f.createCell(0);
                if (m.fecha() != null) {
                    celdaFecha.setCellValue(m.fecha());
                }
                celdaFecha.setCellStyle(estilos.fechaHora);

                texto(f, 1, m.tipo(), "ENTRADA".equals(m.tipo())
                        ? estilos.estadoEntrada : estilos.estadoSalida);
                texto(f, 2, m.codigoProducto(), estilos.texto);
                texto(f, 3, m.nombreProducto(), estilos.texto);
                numero(f, 4, m.cantidad(), estilos.entero);
                numero(f, 5, m.stockAnterior(), estilos.entero);
                numero(f, 6, m.stockPosterior(), estilos.entero);
                texto(f, 7, m.cliente() == null ? "" : m.cliente(), estilos.texto);
                texto(f, 8, m.usuario(), estilos.texto);
                texto(f, 9, m.observacion() == null ? "" : m.observacion(), estilos.texto);
            }

            cerrarTabla(datos, encabezados.length, numeroFila,
                    new int[]{20, 14, 14, 36, 12, 16, 16, 26, 26, 36});

            libro.write(salida);
            return salida.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "No fue posible generar el archivo Excel del reporte.", e);
        }
    }

    // =========================================================
    // ARMADO COMÚN
    // =========================================================

    /** Título, autor, fecha y filtros. Devuelve la siguiente fila libre. */
    private int encabezadoResumen(Sheet hoja, Estilos estilos, String titulo,
                                  String generadoPor, LocalDateTime generadoEn,
                                  List<String> filtros) {

        Row fila0 = hoja.createRow(0);
        texto(fila0, 0, "INVENTORY  -  " + titulo, estilos.titulo);
        fila0.setHeightInPoints(26);

        Row fila1 = hoja.createRow(1);
        texto(fila1, 0, "Generado el " + generadoEn.format(FECHA_HORA)
                + "  por  " + generadoPor, estilos.subtitulo);

        Row fila2 = hoja.createRow(2);
        texto(fila2, 0, "Filtros: " + (filtros.isEmpty()
                ? "ninguno (se incluyen todos los registros)"
                : String.join("  |  ", filtros)), estilos.subtitulo);

        Row seccion = hoja.createRow(4);
        texto(seccion, 0, "Indicadores", estilos.seccion);

        return 5;
    }

    private int indicador(Sheet hoja, Estilos estilos, int fila,
                          String etiqueta, long valor, boolean conSigno) {

        Row f = hoja.createRow(fila);
        texto(f, 0, etiqueta, estilos.etiqueta);
        numero(f, 1, valor, conSigno ? estilos.enteroConSigno : estilos.enteroDestacado);
        return fila + 1;
    }

    private void encabezadoTabla(Sheet hoja, Estilos estilos, String[] encabezados) {
        Row fila = hoja.createRow(0);
        fila.setHeightInPoints(22);
        for (int i = 0; i < encabezados.length; i++) {
            texto(fila, i, encabezados[i], estilos.encabezado);
        }
    }

    /** Filtro automático, fila de encabezado fija y anchos de columna. */
    private void cerrarTabla(Sheet hoja, int columnas, int filasUsadas, int[] anchos) {

        hoja.createFreezePane(0, 1);

        // El filtro automático necesita al menos una fila de datos.
        if (filasUsadas > 1) {
            hoja.setAutoFilter(new CellRangeAddress(0, filasUsadas - 1, 0, columnas - 1));
        }

        anchos(hoja, anchos);
    }

    /** Ancho de columna en "caracteres" (POI usa 1/256 de carácter). */
    private void anchos(Sheet hoja, int... caracteres) {
        for (int i = 0; i < caracteres.length; i++) {
            hoja.setColumnWidth(i, caracteres[i] * 256);
        }
    }

    private void texto(Row fila, int columna, String valor, XSSFCellStyle estilo) {
        Cell celda = fila.createCell(columna);
        celda.setCellValue(valor == null ? "" : valor);
        celda.setCellStyle(estilo);
    }

    private void numero(Row fila, int columna, double valor, XSSFCellStyle estilo) {
        Cell celda = fila.createCell(columna);
        celda.setCellValue(valor);
        celda.setCellStyle(estilo);
    }

    private double valor(BigDecimal dinero) {
        return dinero == null ? 0 : dinero.doubleValue();
    }

    // =========================================================
    // ESTILOS
    // =========================================================

    /** Todos los estilos del libro, creados una sola vez (Excel limita su cantidad). */
    private static final class Estilos {

        final XSSFCellStyle titulo;
        final XSSFCellStyle subtitulo;
        final XSSFCellStyle seccion;
        final XSSFCellStyle encabezado;
        final XSSFCellStyle texto;
        final XSSFCellStyle etiqueta;
        final XSSFCellStyle entero;
        final XSSFCellStyle enteroDestacado;
        final XSSFCellStyle enteroConSigno;
        final XSSFCellStyle dinero;
        final XSSFCellStyle dineroDestacado;
        final XSSFCellStyle fechaHora;
        final XSSFCellStyle estadoBueno;
        final XSSFCellStyle estadoCritico;
        final XSSFCellStyle estadoNeutro;
        final XSSFCellStyle estadoAlerta;
        final XSSFCellStyle estadoEntrada;
        final XSSFCellStyle estadoSalida;

        Estilos(XSSFWorkbook libro) {

            XSSFColor marca = color(0x0F, 0x1B, 0x3D);
            XSSFColor gris = color(0x52, 0x51, 0x4E);

            titulo = base(libro, fuente(libro, 16, true, marca), null);
            subtitulo = base(libro, fuente(libro, 10, false, gris), null);
            seccion = base(libro, fuente(libro, 12, true, marca), null);

            encabezado = base(libro, fuente(libro, 10, true, color(0xFF, 0xFF, 0xFF)), marca);
            encabezado.setAlignment(HorizontalAlignment.CENTER);
            encabezado.setVerticalAlignment(VerticalAlignment.CENTER);

            texto = base(libro, fuente(libro, 10, false, null), null);
            bordeInferior(texto);

            etiqueta = base(libro, fuente(libro, 10, false, null), null);
            bordeInferior(etiqueta);

            entero = base(libro, fuente(libro, 10, false, null), null);
            entero.setDataFormat(libro.createDataFormat().getFormat("#,##0"));
            bordeInferior(entero);

            enteroDestacado = base(libro, fuente(libro, 11, true, null), null);
            enteroDestacado.setDataFormat(libro.createDataFormat().getFormat("#,##0"));
            bordeInferior(enteroDestacado);

            enteroConSigno = base(libro, fuente(libro, 11, true, null), null);
            enteroConSigno.setDataFormat(libro.createDataFormat().getFormat("+#,##0;-#,##0;0"));
            bordeInferior(enteroConSigno);

            dinero = base(libro, fuente(libro, 10, false, null), null);
            dinero.setDataFormat(libro.createDataFormat().getFormat("$#,##0.00"));
            bordeInferior(dinero);

            dineroDestacado = base(libro, fuente(libro, 11, true, null), null);
            dineroDestacado.setDataFormat(libro.createDataFormat().getFormat("$#,##0.00"));
            bordeInferior(dineroDestacado);

            fechaHora = base(libro, fuente(libro, 10, false, null), null);
            fechaHora.setDataFormat(libro.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));
            bordeInferior(fechaHora);

            estadoBueno = base(libro, fuente(libro, 10, true, color(0x06, 0x76, 0x47)), null);
            estadoCritico = base(libro, fuente(libro, 10, true, color(0xB4, 0x23, 0x18)), null);
            estadoNeutro = base(libro, fuente(libro, 10, true, gris), null);
            estadoAlerta = base(libro, fuente(libro, 10, true, color(0xB5, 0x47, 0x08)), null);
            estadoEntrada = base(libro, fuente(libro, 10, true, color(0x1C, 0x5C, 0xAB)), null);
            estadoSalida = base(libro, fuente(libro, 10, true, color(0xB9, 0x3E, 0x12)), null);
            for (XSSFCellStyle e : new XSSFCellStyle[]{estadoBueno, estadoCritico,
                    estadoNeutro, estadoAlerta, estadoEntrada, estadoSalida}) {
                bordeInferior(e);
            }
        }

        /** Estilo de la columna "Estado" de productos. */
        XSSFCellStyle estado(String estado, boolean stockBajo) {
            if (stockBajo) return estadoAlerta;
            return switch (estado) {
                case "DISPONIBLE" -> estadoBueno;
                case "AGOTADO" -> estadoCritico;
                default -> estadoNeutro;
            };
        }

        private static XSSFColor color(int r, int g, int b) {
            return new XSSFColor(new byte[]{(byte) r, (byte) g, (byte) b}, null);
        }

        private static XSSFFont fuente(XSSFWorkbook libro, int tamano,
                                       boolean negrita, XSSFColor color) {
            XSSFFont f = libro.createFont();
            f.setFontHeightInPoints((short) tamano);
            f.setBold(negrita);
            if (color != null) {
                f.setColor(color);
            }
            return f;
        }

        private static XSSFCellStyle base(XSSFWorkbook libro, XSSFFont fuente,
                                          XSSFColor relleno) {
            XSSFCellStyle estilo = libro.createCellStyle();
            estilo.setFont(fuente);
            estilo.setVerticalAlignment(VerticalAlignment.CENTER);
            if (relleno != null) {
                estilo.setFillForegroundColor(relleno);
                estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            return estilo;
        }

        private static void bordeInferior(XSSFCellStyle estilo) {
            estilo.setBorderBottom(BorderStyle.THIN);
            estilo.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        }
    }
}
