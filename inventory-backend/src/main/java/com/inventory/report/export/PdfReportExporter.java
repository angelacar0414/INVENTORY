package com.inventory.report.export;

import com.inventory.report.dto.*;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * EXPORTADOR DE REPORTES A PDF
 * ----------------------------
 * Construye el PDF con la librería OpenPDF. Cada reporte tiene:
 *
 *   1. Encabezado: nombre del reporte, quién lo generó y cuándo.
 *   2. Filtros aplicados.
 *   3. Indicadores (tarjetas con los números principales).
 *   4. Dos gráficas (dibujadas por ChartRenderer).
 *   5. La tabla de detalle, con el encabezado repetido en cada página.
 *   6. Pie de página con "Página X de Y".
 *
 * La hoja es A4 horizontal para que las tablas de muchas columnas
 * se lean cómodas.
 */
@Component
public class PdfReportExporter {

    // --- Colores de la identidad visual (azul oscuro del menú lateral) ---
    private static final Color MARCA = new Color(0x0F, 0x1B, 0x3D);
    private static final Color TINTA = new Color(0x0B, 0x0B, 0x0B);
    private static final Color TINTA_SUAVE = new Color(0x52, 0x51, 0x4E);
    private static final Color LINEA = new Color(0xE1, 0xE0, 0xD9);
    private static final Color FONDO_TARJETA = new Color(0xF6, 0xF7, 0xF9);
    private static final Color FONDO_ZEBRA = new Color(0xF8, 0xF9, 0xFB);

    // Colores de texto (más oscuros que los de las gráficas para que se
    // lean bien al imprimir)
    private static final Color TEXTO_BUENO = new Color(0x06, 0x76, 0x47);
    private static final Color TEXTO_CRITICO = new Color(0xB4, 0x23, 0x18);
    private static final Color TEXTO_ALERTA = new Color(0xB5, 0x47, 0x08);
    private static final Color TEXTO_ENTRADA = new Color(0x1C, 0x5C, 0xAB);
    private static final Color TEXTO_SALIDA = new Color(0xB9, 0x3E, 0x12);

    private static final Locale CO = Locale.forLanguageTag("es-CO");
    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // =========================================================
    // REPORTE DE PRODUCTOS
    // =========================================================

    public byte[] productos(ProductReportDTO reporte,
                            String generadoPor,
                            LocalDateTime generadoEn) {

        ProductReportSummaryDTO r = reporte.resumen();

        return construir("Reporte de productos", generadoPor, generadoEn,
                reporte.filtros(), documento -> {

            // --- Indicadores ---
            documento.add(tarjetas(
                    new String[]{"PRODUCTOS", "DISPONIBLES", "AGOTADOS",
                            "STOCK BAJO", "UNIDADES EN STOCK", "VALOR DEL INVENTARIO"},
                    new String[]{
                            entero(r.totalProductos()),
                            entero(r.disponibles()),
                            entero(r.agotados()),
                            entero(r.stockBajo()),
                            entero(r.unidadesEnStock()),
                            dinero(r.valorInventario())}));

            // --- Gráficas ---
            byte[] dona = ChartRenderer.dona(
                    "Productos por estado",
                    "Según el estado de cada producto",
                    List.of(
                            new ChartRenderer.Porcion("Disponible", r.disponibles(), ChartRenderer.BUENO),
                            new ChartRenderer.Porcion("Agotado", r.agotados(), ChartRenderer.CRITICO),
                            new ChartRenderer.Porcion("Inactivo", r.inactivos(), ChartRenderer.NEUTRO)),
                    "productos");

            byte[] barras = ChartRenderer.barrasHorizontales(
                    "Productos más cerca de agotarse",
                    "Stock actual frente al stock mínimo",
                    barrasDeStockCritico(reporte.items()),
                    ChartRenderer.AZUL, "Stock actual", "Stock mínimo");

            documento.add(graficas(dona, barras));

            // --- Los 5 productos con más stock ---
            documento.add(topStock(reporte.items()));

            // --- Detalle ---
            documento.add(tituloSeccion(
                    "Detalle (" + entero(reporte.items().size()) + " productos)"));

            if (reporte.items().isEmpty()) {
                documento.add(sinRegistros());
            } else {
                documento.add(tablaProductos(reporte.items()));
                documento.add(nota("Stock bajo: producto activo con stock mayor que 0 "
                        + "y menor o igual a su stock mínimo. Valor del inventario "
                        + "calculado a precio de compra, solo con productos activos."));
            }
        });
    }

    /** Los 8 productos activos con menor cobertura (stock / stock mínimo). */
    private List<ChartRenderer.Barra> barrasDeStockCritico(List<ProductReportRowDTO> items) {

        return items.stream()
                .filter(p -> !"INACTIVO".equals(p.estado()) && p.stockMinimo() > 0)
                .sorted(Comparator.comparingDouble(
                        p -> (double) p.stockActual() / p.stockMinimo()))
                .limit(8)
                .map(p -> new ChartRenderer.Barra(
                        p.nombre(), p.stockActual(), (double) p.stockMinimo()))
                .toList();
    }

    private PdfPTable topStock(List<ProductReportRowDTO> items) {

        List<ProductReportRowDTO> top = items.stream()
                .filter(p -> !"INACTIVO".equals(p.estado()))
                .sorted(Comparator.comparingInt(ProductReportRowDTO::stockActual).reversed())
                .limit(5)
                .toList();

        PdfPTable tabla = new PdfPTable(new float[]{1.2f, 5f, 2.2f, 1.6f});
        tabla.setWidthPercentage(60);
        tabla.setHorizontalAlignment(Element.ALIGN_LEFT);
        tabla.setSpacingBefore(10);
        tabla.setKeepTogether(true);

        PdfPCell titulo = new PdfPCell(new Phrase(
                "Los 5 productos con más unidades en stock", fuente(10, Font.BOLD, TINTA)));
        titulo.setColspan(4);
        titulo.setBorder(Rectangle.NO_BORDER);
        titulo.setPaddingBottom(5);
        tabla.addCell(titulo);

        for (String encabezado : new String[]{"#", "Producto", "Categoría", "Stock"}) {
            tabla.addCell(celdaEncabezado(encabezado, Element.ALIGN_LEFT));
        }

        if (top.isEmpty()) {
            PdfPCell vacio = celda("Sin datos para mostrar", Element.ALIGN_LEFT, false, null, false);
            vacio.setColspan(4);
            tabla.addCell(vacio);
            return tabla;
        }

        int posicion = 1;
        for (ProductReportRowDTO p : top) {
            boolean zebra = posicion % 2 == 0;
            tabla.addCell(celda(String.valueOf(posicion), Element.ALIGN_LEFT, zebra, null, false));
            tabla.addCell(celda(p.nombre(), Element.ALIGN_LEFT, zebra, null, false));
            tabla.addCell(celda(p.categoria(), Element.ALIGN_LEFT, zebra, null, false));
            tabla.addCell(celda(entero(p.stockActual()), Element.ALIGN_RIGHT, zebra, null, true));
            posicion++;
        }

        return tabla;
    }

    private PdfPTable tablaProductos(List<ProductReportRowDTO> items) {

        PdfPTable tabla = new PdfPTable(new float[]{1.3f, 3.4f, 2f, 2.2f, 1f, 1f, 1.5f, 1.6f, 1.6f});
        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1);

        String[] encabezados = {"Código", "Producto", "Categoría", "Proveedor",
                "Stock", "Mínimo", "Estado", "P. compra", "P. venta"};
        int[] alineacion = {Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_LEFT,
                Element.ALIGN_LEFT, Element.ALIGN_RIGHT, Element.ALIGN_RIGHT,
                Element.ALIGN_LEFT, Element.ALIGN_RIGHT, Element.ALIGN_RIGHT};

        for (int i = 0; i < encabezados.length; i++) {
            tabla.addCell(celdaEncabezado(encabezados[i], alineacion[i]));
        }

        boolean zebra = false;
        for (ProductReportRowDTO p : items) {

            tabla.addCell(celda(p.codigo(), Element.ALIGN_LEFT, zebra, null, false));
            tabla.addCell(celda(p.nombre(), Element.ALIGN_LEFT, zebra, null, false));
            tabla.addCell(celda(p.categoria(), Element.ALIGN_LEFT, zebra, null, false));
            tabla.addCell(celda(p.proveedor(), Element.ALIGN_LEFT, zebra, null, false));
            tabla.addCell(celda(entero(p.stockActual()), Element.ALIGN_RIGHT, zebra, null, p.stockBajo()));
            tabla.addCell(celda(entero(p.stockMinimo()), Element.ALIGN_RIGHT, zebra, null, false));

            // El estado se escribe con palabras (no solo con color) para
            // que también se entienda en una impresión en blanco y negro.
            String textoEstado;
            Color colorEstado;
            if (p.stockBajo()) {
                textoEstado = "STOCK BAJO";
                colorEstado = TEXTO_ALERTA;
            } else if ("AGOTADO".equals(p.estado())) {
                textoEstado = "AGOTADO";
                colorEstado = TEXTO_CRITICO;
            } else if ("INACTIVO".equals(p.estado())) {
                textoEstado = "INACTIVO";
                colorEstado = TINTA_SUAVE;
            } else {
                textoEstado = "DISPONIBLE";
                colorEstado = TEXTO_BUENO;
            }
            tabla.addCell(celda(textoEstado, Element.ALIGN_LEFT, zebra, colorEstado, true));

            tabla.addCell(celda(dinero(p.precioCompra()), Element.ALIGN_RIGHT, zebra, null, false));
            tabla.addCell(celda(dinero(p.precioVenta()), Element.ALIGN_RIGHT, zebra, null, false));

            zebra = !zebra;
        }

        return tabla;
    }

    // =========================================================
    // REPORTE DE MOVIMIENTOS
    // =========================================================

    public byte[] movimientos(MovementReportDTO reporte,
                              String generadoPor,
                              LocalDateTime generadoEn) {

        MovementReportSummaryDTO r = reporte.resumen();

        return construir("Reporte de movimientos", generadoPor, generadoEn,
                reporte.filtros(), documento -> {

            // --- Indicadores ---
            String balance = (r.balanceNeto() > 0 ? "+" : "") + entero(r.balanceNeto());

            documento.add(tarjetas(
                    new String[]{"MOVIMIENTOS", "ENTRADAS (REGISTROS)", "SALIDAS (REGISTROS)",
                            "UNIDADES QUE ENTRARON", "UNIDADES QUE SALIERON", "BALANCE NETO"},
                    new String[]{
                            entero(r.totalMovimientos()),
                            entero(r.totalEntradas()),
                            entero(r.totalSalidas()),
                            entero(r.unidadesEntradas()),
                            entero(r.unidadesSalidas()),
                            balance + " uds"}));

            // --- Gráfica 1: entradas vs salidas en el tiempo ---
            List<String> etiquetas = new ArrayList<>();
            double[] entradas = new double[reporte.serie().size()];
            double[] salidas = new double[reporte.serie().size()];
            for (int i = 0; i < reporte.serie().size(); i++) {
                SeriesPointDTO punto = reporte.serie().get(i);
                etiquetas.add(punto.etiqueta());
                entradas[i] = punto.entradas();
                salidas[i] = punto.salidas();
            }

            String unidadTiempo = switch (reporte.granularidad()) {
                case "SEMANA" -> "semana";
                case "MES" -> "mes";
                default -> "día";
            };
            String aclaracion = "SEMANA".equals(reporte.granularidad())
                    ? "Unidades por semana (se indica el lunes de cada una)"
                    : "Unidades movidas por " + unidadTiempo;

            byte[] columnas = ChartRenderer.columnasAgrupadas(
                    "Entradas y salidas por " + unidadTiempo, aclaracion,
                    etiquetas, entradas, salidas, "Entradas", "Salidas");

            // --- Gráfica 2: evolución del stock (un solo producto)
            //     o ranking de productos más movidos ---
            byte[] segunda;
            if (!reporte.evolucionStock().isEmpty()) {
                List<StockPointDTO> puntos = reporte.evolucionStock();
                double[] valores = new double[puntos.size()];
                List<String> fechas = new ArrayList<>();
                for (int i = 0; i < puntos.size(); i++) {
                    valores[i] = puntos.get(i).stock();
                    fechas.add(i == 0 || i == puntos.size() - 1
                            ? puntos.get(i).fecha().format(FECHA) : "");
                }
                segunda = ChartRenderer.linea(
                        "Evolución del stock",
                        "Stock del producto tras cada movimiento",
                        fechas, valores, "movimientos en orden cronológico");
            } else {
                List<ChartRenderer.Barra> top = reporte.topProductos().stream()
                        .map(t -> new ChartRenderer.Barra(t.nombre(), t.valor(), null))
                        .toList();
                segunda = ChartRenderer.barrasHorizontales(
                        "Productos con más unidades movidas",
                        "Entradas y salidas sumadas (los 5 principales)",
                        top, ChartRenderer.AZUL, "Unidades movidas", null);
            }

            documento.add(graficas(columnas, segunda));

            // --- Detalle ---
            documento.add(tituloSeccion(
                    "Detalle (" + entero(reporte.items().size()) + " movimientos)"));

            if (reporte.items().isEmpty()) {
                documento.add(sinRegistros());
            } else {
                documento.add(tablaMovimientos(reporte.items()));
            }
        });
    }

    private PdfPTable tablaMovimientos(List<MovementReportRowDTO> items) {

        PdfPTable tabla = new PdfPTable(new float[]{2.1f, 1.5f, 1.3f, 3.2f, 1.3f, 1.2f, 1.2f, 2.2f, 2.2f, 2.6f});
        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1);

        String[] encabezados = {"Fecha", "Tipo", "Código", "Producto", "Cantidad",
                "Stock ant.", "Stock post.", "Cliente", "Usuario", "Observación"};
        int[] alineacion = {Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_LEFT,
                Element.ALIGN_LEFT, Element.ALIGN_RIGHT, Element.ALIGN_RIGHT,
                Element.ALIGN_RIGHT, Element.ALIGN_LEFT, Element.ALIGN_LEFT,
                Element.ALIGN_LEFT};

        for (int i = 0; i < encabezados.length; i++) {
            tabla.addCell(celdaEncabezado(encabezados[i], alineacion[i]));
        }

        boolean zebra = false;
        for (MovementReportRowDTO m : items) {

            boolean esEntrada = "ENTRADA".equals(m.tipo());

            tabla.addCell(celda(m.fecha() == null ? "" : m.fecha().format(FECHA_HORA),
                    Element.ALIGN_LEFT, zebra, null, false));
            tabla.addCell(celda(m.tipo(), Element.ALIGN_LEFT, zebra,
                    esEntrada ? TEXTO_ENTRADA : TEXTO_SALIDA, true));
            tabla.addCell(celda(m.codigoProducto(), Element.ALIGN_LEFT, zebra, null, false));
            tabla.addCell(celda(m.nombreProducto(), Element.ALIGN_LEFT, zebra, null, false));
            tabla.addCell(celda((esEntrada ? "+" : "-") + entero(m.cantidad()),
                    Element.ALIGN_RIGHT, zebra,
                    esEntrada ? TEXTO_ENTRADA : TEXTO_SALIDA, true));
            tabla.addCell(celda(entero(m.stockAnterior()), Element.ALIGN_RIGHT, zebra, null, false));
            tabla.addCell(celda(entero(m.stockPosterior()), Element.ALIGN_RIGHT, zebra, null, true));
            tabla.addCell(celda(vacioComoGuion(m.cliente()), Element.ALIGN_LEFT, zebra, null, false));
            tabla.addCell(celda(vacioComoGuion(m.usuario()), Element.ALIGN_LEFT, zebra, null, false));
            tabla.addCell(celda(vacioComoGuion(m.observacion()), Element.ALIGN_LEFT, zebra, null, false));

            zebra = !zebra;
        }

        return tabla;
    }

    // =========================================================
    // ARMADO COMÚN DEL DOCUMENTO
    // =========================================================

    /** Contenido específico de cada reporte; puede lanzar DocumentException. */
    @FunctionalInterface
    private interface Contenido {
        void agregar(Document documento) throws DocumentException, IOException;
    }

    private byte[] construir(String titulo, String generadoPor,
                             LocalDateTime generadoEn, List<String> filtros,
                             Contenido contenido) {

        Document documento = new Document(PageSize.A4.rotate(), 28, 28, 30, 40);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try {
            PdfWriter escritor = PdfWriter.getInstance(documento, salida);
            escritor.setPageEvent(new PiePagina("INVENTORY  |  " + titulo));

            documento.open();

            documento.add(encabezado(titulo, generadoPor, generadoEn));
            documento.add(filtrosAplicados(filtros));
            contenido.agregar(documento);

            documento.close();
            return salida.toByteArray();

        } catch (DocumentException | IOException e) {
            throw new IllegalStateException(
                    "No fue posible generar el PDF del reporte.", e);
        }
    }

    private PdfPTable encabezado(String titulo, String generadoPor, LocalDateTime generadoEn) {

        PdfPTable tabla = new PdfPTable(new float[]{3f, 2f});
        tabla.setWidthPercentage(100);

        PdfPCell izquierda = new PdfPCell();
        izquierda.setBorder(Rectangle.BOTTOM);
        izquierda.setBorderColor(MARCA);
        izquierda.setBorderWidthBottom(1.5f);
        izquierda.setPaddingBottom(8);
        izquierda.addElement(new Paragraph("INVENTORY", fuente(9, Font.BOLD, ChartRenderer.AZUL)));
        izquierda.addElement(new Paragraph(titulo, fuente(22, Font.BOLD, TINTA)));

        PdfPCell derecha = new PdfPCell();
        derecha.setBorder(Rectangle.BOTTOM);
        derecha.setBorderColor(MARCA);
        derecha.setBorderWidthBottom(1.5f);
        derecha.setPaddingBottom(8);

        Paragraph fecha = new Paragraph("Generado el " + generadoEn.format(FECHA_HORA),
                fuente(9, Font.NORMAL, TINTA_SUAVE));
        fecha.setAlignment(Element.ALIGN_RIGHT);
        Paragraph autor = new Paragraph("Por: " + generadoPor,
                fuente(9, Font.NORMAL, TINTA_SUAVE));
        autor.setAlignment(Element.ALIGN_RIGHT);
        derecha.addElement(fecha);
        derecha.addElement(autor);

        tabla.addCell(izquierda);
        tabla.addCell(derecha);
        return tabla;
    }

    private Paragraph filtrosAplicados(List<String> filtros) {

        Paragraph parrafo = new Paragraph();
        parrafo.setSpacingBefore(8);
        parrafo.add(new Phrase("Filtros aplicados: ", fuente(9, Font.BOLD, TINTA)));
        parrafo.add(new Phrase(
                filtros.isEmpty()
                        ? "ninguno (se incluyen todos los registros)"
                        : String.join("   |   ", filtros),
                fuente(9, Font.NORMAL, TINTA_SUAVE)));
        return parrafo;
    }

    /** Fila de tarjetas con los indicadores principales. */
    private PdfPTable tarjetas(String[] titulos, String[] valores) {

        PdfPTable tabla = new PdfPTable(titulos.length);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(12);

        for (int i = 0; i < titulos.length; i++) {
            PdfPCell celda = new PdfPCell();
            celda.setBackgroundColor(FONDO_TARJETA);
            celda.setBorderColor(LINEA);
            celda.setBorderWidth(0.75f);
            celda.setPadding(8);
            celda.addElement(new Paragraph(titulos[i], fuente(7, Font.BOLD, TINTA_SUAVE)));
            celda.addElement(new Paragraph(valores[i], fuente(15, Font.BOLD, TINTA)));
            tabla.addCell(celda);
        }

        return tabla;
    }

    /** Dos gráficas lado a lado. */
    private PdfPTable graficas(byte[] izquierda, byte[] derecha)
            throws DocumentException, IOException {

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(12);
        tabla.setKeepTogether(true);

        for (byte[] png : new byte[][]{izquierda, derecha}) {
            PdfPCell celda = new PdfPCell(Image.getInstance(png), true);
            celda.setBorderColor(LINEA);
            celda.setBorderWidth(0.75f);
            celda.setPadding(4);
            tabla.addCell(celda);
        }

        return tabla;
    }

    private Paragraph tituloSeccion(String texto) {
        Paragraph parrafo = new Paragraph(texto, fuente(12, Font.BOLD, TINTA));
        parrafo.setSpacingBefore(16);
        parrafo.setSpacingAfter(6);
        return parrafo;
    }

    private Paragraph sinRegistros() {
        return new Paragraph("No hay registros con los filtros seleccionados.",
                fuente(10, Font.NORMAL, TINTA_SUAVE));
    }

    private Paragraph nota(String texto) {
        Paragraph parrafo = new Paragraph(texto, fuente(8, Font.ITALIC, TINTA_SUAVE));
        parrafo.setSpacingBefore(6);
        return parrafo;
    }

    // =========================================================
    // CELDAS Y FORMATO
    // =========================================================

    private PdfPCell celdaEncabezado(String texto, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente(8, Font.BOLD, Color.WHITE)));
        celda.setBackgroundColor(MARCA);
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setHorizontalAlignment(alineacion);
        celda.setPadding(5);
        return celda;
    }

    private PdfPCell celda(String texto, int alineacion, boolean zebra,
                           Color color, boolean negrita) {
        PdfPCell celda = new PdfPCell(new Phrase(
                texto == null ? "" : texto,
                fuente(8, negrita ? Font.BOLD : Font.NORMAL, color != null ? color : TINTA)));
        celda.setHorizontalAlignment(alineacion);
        celda.setBorderColor(LINEA);
        celda.setBorder(Rectangle.BOTTOM);
        celda.setBorderWidth(0.5f);
        celda.setPadding(4);
        if (zebra) {
            celda.setBackgroundColor(FONDO_ZEBRA);
        }
        return celda;
    }

    private static Font fuente(float tamano, int estilo, Color color) {
        return FontFactory.getFont(FontFactory.HELVETICA, tamano, estilo, color);
    }

    private static String entero(long valor) {
        return NumberFormat.getIntegerInstance(CO).format(valor);
    }

    private static String dinero(BigDecimal valor) {
        NumberFormat formato = NumberFormat.getNumberInstance(CO);
        formato.setMinimumFractionDigits(0);
        formato.setMaximumFractionDigits(2);
        return "$ " + formato.format(valor == null ? BigDecimal.ZERO : valor);
    }

    private static String vacioComoGuion(String texto) {
        return texto == null || texto.isBlank() ? "-" : texto;
    }

    // =========================================================
    // PIE DE PÁGINA: "Página X de Y"
    // =========================================================

    /**
     * Dibuja en cada página el nombre del reporte y el número de página.
     * El total de páginas solo se conoce al final, por eso se reserva
     * un espacio (PdfTemplate) que se rellena al cerrar el documento.
     */
    private static final class PiePagina extends PdfPageEventHelper {

        private final String texto;
        private PdfTemplate totalPaginas;
        private BaseFont fuenteBase;

        PiePagina(String texto) {
            this.texto = texto;
        }

        @Override
        public void onOpenDocument(PdfWriter escritor, Document documento) {
            try {
                fuenteBase = BaseFont.createFont(
                        BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            } catch (DocumentException | IOException e) {
                throw new IllegalStateException(e);
            }
            totalPaginas = escritor.getDirectContent().createTemplate(30, 12);
        }

        @Override
        public void onEndPage(PdfWriter escritor, Document documento) {

            PdfContentByte lienzo = escritor.getDirectContent();
            float izquierda = documento.left();
            float derecha = documento.right();
            float y = documento.bottom() - 18;

            // línea fina sobre el pie
            lienzo.setRGBColorStroke(0xE1, 0xE0, 0xD9);
            lienzo.setLineWidth(0.5f);
            lienzo.moveTo(izquierda, y + 12);
            lienzo.lineTo(derecha, y + 12);
            lienzo.stroke();

            lienzo.setRGBColorFill(0x52, 0x51, 0x4E);

            // nombre del reporte a la izquierda
            lienzo.beginText();
            lienzo.setFontAndSize(fuenteBase, 8);
            lienzo.setTextMatrix(izquierda, y);
            lienzo.showText(texto);
            lienzo.endText();

            // "Página X de " a la derecha, seguido del total (plantilla)
            String pagina = "Página " + escritor.getPageNumber() + " de ";
            float ancho = fuenteBase.getWidthPoint(pagina, 8);
            float inicio = derecha - ancho - 14;

            lienzo.beginText();
            lienzo.setFontAndSize(fuenteBase, 8);
            lienzo.setTextMatrix(inicio, y);
            lienzo.showText(pagina);
            lienzo.endText();

            lienzo.addTemplate(totalPaginas, inicio + ancho, y);
        }

        @Override
        public void onCloseDocument(PdfWriter escritor, Document documento) {
            totalPaginas.beginText();
            totalPaginas.setFontAndSize(fuenteBase, 8);
            totalPaginas.setRGBColorFill(0x52, 0x51, 0x4E);
            totalPaginas.setTextMatrix(0, 0);
            totalPaginas.showText(String.valueOf(escritor.getPageNumber() - 1));
            totalPaginas.endText();
        }
    }
}
