package com.inventory.report.export;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * DIBUJANTE DE GRÁFICAS PARA LOS REPORTES
 * ---------------------------------------
 * Dibuja las gráficas del PDF directamente con Java2D (java.awt), que ya
 * viene incluido en Java. Así no hace falta ninguna librería extra y
 * tenemos control total del diseño.
 *
 * Cada método devuelve los bytes de una imagen PNG que el exportador
 * de PDF incrusta en la página.
 *
 * Reglas de diseño que se siguen en todas las gráficas:
 *  - Marcas delgadas (barras de máximo 24 px, líneas de 2 px).
 *  - Rejilla de líneas finas y discretas; el dato es lo que destaca.
 *  - Una leyenda siempre que hay dos o más series.
 *  - Los textos van en tinta gris/negra, nunca del color de la serie.
 *  - Colores validados para que se distingan también con daltonismo:
 *    azul = entradas / datos, naranja = salidas.
 *
 * Las gráficas se dibujan sobre un lienzo lógico de 520 x 300 y se
 * generan al doble de resolución (SCALE = 2) para que se vean nítidas
 * al imprimir.
 */
public final class ChartRenderer {

    // --- Colores (paleta validada para fondo blanco) ---
    public static final Color AZUL = new Color(0x2A78D6);
    public static final Color NARANJA = new Color(0xEB6834);
    public static final Color BUENO = new Color(0x0CA30C);
    public static final Color CRITICO = new Color(0xD03B3B);
    public static final Color NEUTRO = new Color(0x898781);

    private static final Color TINTA = new Color(0x0B0B0B);
    private static final Color TINTA_SUAVE = new Color(0x52514E);
    private static final Color REJILLA = new Color(0xE1E0D9);
    private static final Color EJE = new Color(0xC3C2B7);
    private static final Color FONDO = Color.WHITE;

    // --- Tamaño del lienzo ---
    public static final int ANCHO = 520;
    public static final int ALTO = 300;
    private static final int SCALE = 2;

    private static final int MARGEN = 16;

    private ChartRenderer() {
    }

    // =========================================================
    // DATOS DE ENTRADA
    // =========================================================

    /** Una porción de la dona. */
    public record Porcion(String etiqueta, long valor, Color color) {
    }

    /** Una barra horizontal; "marca" es opcional (por ejemplo el stock mínimo). */
    public record Barra(String etiqueta, double valor, Double marca) {
    }

    // =========================================================
    // DONA (reparto por estado)
    // =========================================================

    public static byte[] dona(String titulo, String subtitulo,
                              List<Porcion> porciones, String textoCentro) {

        Lienzo lienzo = new Lienzo();
        Graphics2D g = lienzo.g;
        encabezado(g, titulo, subtitulo);

        long total = 0;
        for (Porcion p : porciones) {
            total += p.valor();
        }

        if (total == 0) {
            sinDatos(g);
            return lienzo.png();
        }

        double cx = 125;
        double cy = 182;
        double radioExterior = 84;
        double grosor = 26;
        double radioMedio = radioExterior - grosor / 2;

        // Cantidad de porciones con valor: solo entre varias se dibujan
        // separaciones; con una sola, el anillo va completo.
        int conValor = 0;
        for (Porcion p : porciones) {
            if (p.valor() > 0) conValor++;
        }

        double separacion = conValor > 1
                ? Math.toDegrees(2.0 / radioMedio) : 0;

        g.setStroke(new BasicStroke((float) grosor, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER));

        double angulo = 90; // empezamos arriba y avanzamos en sentido horario
        for (Porcion p : porciones) {
            if (p.valor() <= 0) continue;

            double barrido = 360.0 * p.valor() / total;
            double extension = Math.max(barrido - separacion, 0.5);

            g.setColor(p.color());
            g.draw(new Arc2D.Double(
                    cx - radioMedio, cy - radioMedio,
                    radioMedio * 2, radioMedio * 2,
                    angulo - separacion / 2, -extension, Arc2D.OPEN));

            angulo -= barrido;
        }

        // Número grande en el centro del anillo
        g.setColor(TINTA);
        g.setFont(fuente(Font.BOLD, 28));
        centrar(g, String.valueOf(total), cx, cy + 4);
        g.setColor(TINTA_SUAVE);
        g.setFont(fuente(Font.PLAIN, 11));
        centrar(g, textoCentro, cx, cy + 22);

        // Leyenda a la derecha: marca de color + etiqueta + cantidad y %
        double y = 130;
        for (Porcion p : porciones) {
            g.setColor(p.color());
            g.fill(new RoundRectangle2D.Double(250, y - 10, 12, 12, 4, 4));

            g.setColor(TINTA);
            g.setFont(fuente(Font.PLAIN, 12));
            g.drawString(p.etiqueta(), 270, (float) y);

            long porcentaje = Math.round(100.0 * p.valor() / total);
            g.setColor(TINTA_SUAVE);
            g.setFont(fuente(Font.BOLD, 12));
            derecha(g, p.valor() + "  (" + porcentaje + "%)", ANCHO - MARGEN, y);

            y += 30;
        }

        return lienzo.png();
    }

    // =========================================================
    // BARRAS HORIZONTALES (ranking / stock vs mínimo)
    // =========================================================

    public static byte[] barrasHorizontales(String titulo, String subtitulo,
                                            List<Barra> barras, Color color,
                                            String nombreBarra, String nombreMarca) {

        Lienzo lienzo = new Lienzo();
        Graphics2D g = lienzo.g;
        encabezado(g, titulo, subtitulo);

        if (barras.isEmpty()) {
            sinDatos(g);
            return lienzo.png();
        }

        g.setFont(fuente(Font.PLAIN, 11));
        FontMetrics fm = g.getFontMetrics();

        // Ancho de la columna de etiquetas: lo que ocupe la más larga,
        // con un tope para que las barras tengan espacio.
        int anchoEtiquetas = 0;
        for (Barra b : barras) {
            anchoEtiquetas = Math.max(anchoEtiquetas, fm.stringWidth(b.etiqueta()));
        }
        anchoEtiquetas = Math.min(anchoEtiquetas, 150);

        double x0 = MARGEN + anchoEtiquetas + 10;
        double x1 = ANCHO - MARGEN - 44; // espacio para el valor al final de la barra
        double yArriba = 72;
        double yAbajo = ALTO - (nombreMarca != null ? 44 : 36);

        double maximo = 0;
        for (Barra b : barras) {
            maximo = Math.max(maximo, b.valor());
            if (b.marca() != null) maximo = Math.max(maximo, b.marca());
        }
        double paso = pasoAmable(maximo);
        double tope = escalaAmable(maximo);

        // Rejilla vertical + números del eje inferior
        g.setFont(fuente(Font.PLAIN, 10));
        for (double v = 0; v <= tope + paso / 1000; v += paso) {
            double x = x0 + (x1 - x0) * v / tope;
            g.setColor(v == 0 ? EJE : REJILLA);
            g.setStroke(new BasicStroke(1f));
            g.draw(new java.awt.geom.Line2D.Double(x, yArriba, x, yAbajo));
            g.setColor(TINTA_SUAVE);
            centrar(g, numero(v), x, yAbajo + 14);
        }

        double pasoFila = (yAbajo - yArriba) / barras.size();
        double grosor = Math.min(16, pasoFila * 0.55);

        for (int i = 0; i < barras.size(); i++) {
            Barra b = barras.get(i);
            double yc = yArriba + pasoFila * (i + 0.5);

            // etiqueta (recortada si no cabe)
            g.setColor(TINTA);
            g.setFont(fuente(Font.PLAIN, 11));
            g.drawString(ajustar(g, b.etiqueta(), anchoEtiquetas),
                    MARGEN, (float) (yc + 4));

            // barra con el extremo del dato redondeado y la base recta
            double largo = (x1 - x0) * b.valor() / tope;
            if (largo > 0.5) {
                g.setColor(color);
                g.fill(barraRedondeadaDerecha(x0, yc - grosor / 2, largo, grosor));
            }

            // valor junto a la punta; si la marca de referencia queda más
            // allá de la barra, el número se corre para no taparse con ella
            double finalVisual = x0 + largo;
            if (b.marca() != null) {
                finalVisual = Math.max(finalVisual,
                        x0 + (x1 - x0) * b.marca() / tope + 2);
            }
            g.setColor(TINTA);
            g.setFont(fuente(Font.BOLD, 11));
            g.drawString(numero(b.valor()), (float) (finalVisual + 6), (float) (yc + 4));

            // marca de referencia (stock mínimo): línea vertical oscura
            if (b.marca() != null) {
                double xm = x0 + (x1 - x0) * b.marca() / tope;
                g.setColor(TINTA);
                g.setStroke(new BasicStroke(2f));
                g.draw(new java.awt.geom.Line2D.Double(
                        xm, yc - grosor / 2 - 4, xm, yc + grosor / 2 + 4));
            }
        }

        // Leyenda inferior
        double yl = ALTO - 12;
        double xl = MARGEN;
        g.setFont(fuente(Font.PLAIN, 11));
        g.setColor(color);
        g.fill(new RoundRectangle2D.Double(xl, yl - 10, 12, 12, 4, 4));
        g.setColor(TINTA_SUAVE);
        g.drawString(nombreBarra, (float) (xl + 18), (float) yl);
        if (nombreMarca != null) {
            double xm = xl + 18 + g.getFontMetrics().stringWidth(nombreBarra) + 24;
            g.setColor(TINTA);
            g.setStroke(new BasicStroke(2f));
            g.draw(new java.awt.geom.Line2D.Double(xm + 5, yl - 12, xm + 5, yl + 3));
            g.setColor(TINTA_SUAVE);
            g.drawString(nombreMarca, (float) (xm + 16), (float) yl);
        }

        return lienzo.png();
    }

    // =========================================================
    // COLUMNAS AGRUPADAS (entradas vs salidas en el tiempo)
    // =========================================================

    public static byte[] columnasAgrupadas(String titulo, String subtitulo,
                                           List<String> etiquetas,
                                           double[] serieA, double[] serieB,
                                           String nombreA, String nombreB) {

        Lienzo lienzo = new Lienzo();
        Graphics2D g = lienzo.g;
        encabezado(g, titulo, subtitulo);

        // Leyenda (siempre presente: son dos series)
        double xl = MARGEN;
        double yl = 62;
        g.setFont(fuente(Font.PLAIN, 11));
        g.setColor(AZUL);
        g.fill(new RoundRectangle2D.Double(xl, yl - 10, 12, 12, 4, 4));
        g.setColor(TINTA_SUAVE);
        g.drawString(nombreA, (float) (xl + 18), (float) yl);
        double xb = xl + 18 + g.getFontMetrics().stringWidth(nombreA) + 22;
        g.setColor(NARANJA);
        g.fill(new RoundRectangle2D.Double(xb, yl - 10, 12, 12, 4, 4));
        g.setColor(TINTA_SUAVE);
        g.drawString(nombreB, (float) (xb + 18), (float) yl);

        if (etiquetas.isEmpty()) {
            sinDatos(g);
            return lienzo.png();
        }

        double x0 = MARGEN + 34;
        double x1 = ANCHO - MARGEN;
        double yArriba = 84;
        double yAbajo = ALTO - 32;

        double maximo = 0;
        for (int i = 0; i < etiquetas.size(); i++) {
            maximo = Math.max(maximo, Math.max(serieA[i], serieB[i]));
        }
        double paso = pasoAmable(maximo);
        double tope = escalaAmable(maximo);

        // Rejilla horizontal + números del eje izquierdo
        g.setFont(fuente(Font.PLAIN, 10));
        for (double v = 0; v <= tope + paso / 1000; v += paso) {
            double y = yAbajo - (yAbajo - yArriba) * v / tope;
            g.setColor(v == 0 ? EJE : REJILLA);
            g.setStroke(new BasicStroke(1f));
            g.draw(new java.awt.geom.Line2D.Double(x0, y, x1, y));
            g.setColor(TINTA_SUAVE);
            derecha(g, numero(v), x0 - 6, y + 4);
        }

        int n = etiquetas.size();
        double anchoGrupo = (x1 - x0) / n;
        double anchoBarra = Math.min(14, Math.max(2, (anchoGrupo - 4) / 2 - 1));
        double hueco = 2; // separación entre las dos barras de un grupo

        for (int i = 0; i < n; i++) {
            double centro = x0 + anchoGrupo * (i + 0.5);
            dibujarColumna(g, AZUL, centro - hueco / 2 - anchoBarra,
                    yAbajo, anchoBarra, (yAbajo - yArriba) * serieA[i] / tope);
            dibujarColumna(g, NARANJA, centro + hueco / 2,
                    yAbajo, anchoBarra, (yAbajo - yArriba) * serieB[i] / tope);
        }

        // Etiquetas del eje X: solo algunas, para que no se amontonen
        g.setFont(fuente(Font.PLAIN, 10));
        g.setColor(TINTA_SUAVE);
        int cadaCuanto = (int) Math.ceil(40.0 / anchoGrupo);
        for (int i = 0; i < n; i += Math.max(cadaCuanto, 1)) {
            centrar(g, etiquetas.get(i), x0 + anchoGrupo * (i + 0.5), yAbajo + 15);
        }

        return lienzo.png();
    }

    private static void dibujarColumna(Graphics2D g, Color color,
                                       double x, double yBase,
                                       double ancho, double alto) {
        if (alto < 0.5) return;
        g.setColor(color);
        if (alto < 8) {
            g.fill(new java.awt.geom.Rectangle2D.Double(x, yBase - alto, ancho, alto));
            return;
        }
        // Extremo superior redondeado (4 px) y base recta
        double r = Math.min(4, ancho / 2);
        Path2D.Double p = new Path2D.Double();
        p.moveTo(x, yBase);
        p.lineTo(x, yBase - alto + r);
        p.quadTo(x, yBase - alto, x + r, yBase - alto);
        p.lineTo(x + ancho - r, yBase - alto);
        p.quadTo(x + ancho, yBase - alto, x + ancho, yBase - alto + r);
        p.lineTo(x + ancho, yBase);
        p.closePath();
        g.fill(p);
    }

    // =========================================================
    // LÍNEA (evolución del stock de un producto)
    // =========================================================

    public static byte[] linea(String titulo, String subtitulo,
                               List<String> etiquetas, double[] valores,
                               String unidad) {

        Lienzo lienzo = new Lienzo();
        Graphics2D g = lienzo.g;
        encabezado(g, titulo, subtitulo);

        if (valores.length < 2) {
            sinDatos(g);
            return lienzo.png();
        }

        double x0 = MARGEN + 34;
        double x1 = ANCHO - MARGEN - 40; // espacio para el valor final
        double yArriba = 72;
        double yAbajo = ALTO - 36;

        double maximo = 0;
        for (double v : valores) maximo = Math.max(maximo, v);
        double paso = pasoAmable(maximo);
        double tope = escalaAmable(maximo);

        g.setFont(fuente(Font.PLAIN, 10));
        for (double v = 0; v <= tope + paso / 1000; v += paso) {
            double y = yAbajo - (yAbajo - yArriba) * v / tope;
            g.setColor(v == 0 ? EJE : REJILLA);
            g.setStroke(new BasicStroke(1f));
            g.draw(new java.awt.geom.Line2D.Double(x0, y, x1, y));
            g.setColor(TINTA_SUAVE);
            derecha(g, numero(v), x0 - 6, y + 4);
        }

        int n = valores.length;
        double[] xs = new double[n];
        double[] ys = new double[n];
        for (int i = 0; i < n; i++) {
            xs[i] = x0 + (x1 - x0) * i / (n - 1);
            ys[i] = yAbajo - (yAbajo - yArriba) * valores[i] / tope;
        }

        // Área suave bajo la línea (10 % de opacidad)
        Path2D.Double area = new Path2D.Double();
        area.moveTo(xs[0], yAbajo);
        for (int i = 0; i < n; i++) area.lineTo(xs[i], ys[i]);
        area.lineTo(xs[n - 1], yAbajo);
        area.closePath();
        g.setColor(new Color(AZUL.getRed(), AZUL.getGreen(), AZUL.getBlue(), 26));
        g.fill(area);

        // Línea de 2 px
        Path2D.Double trazo = new Path2D.Double();
        trazo.moveTo(xs[0], ys[0]);
        for (int i = 1; i < n; i++) trazo.lineTo(xs[i], ys[i]);
        g.setColor(AZUL);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(trazo);

        // Punto final con anillo blanco, y su valor
        double px = xs[n - 1];
        double py = ys[n - 1];
        g.setColor(FONDO);
        g.fill(new java.awt.geom.Ellipse2D.Double(px - 6.5, py - 6.5, 13, 13));
        g.setColor(AZUL);
        g.fill(new java.awt.geom.Ellipse2D.Double(px - 4.5, py - 4.5, 9, 9));
        g.setColor(TINTA);
        g.setFont(fuente(Font.BOLD, 12));
        g.drawString(numero(valores[n - 1]), (float) (px + 10), (float) (py + 4));

        // Fechas de inicio y fin en el eje X
        g.setFont(fuente(Font.PLAIN, 10));
        g.setColor(TINTA_SUAVE);
        g.drawString(etiquetas.get(0), (float) x0, (float) (yAbajo + 15));
        derecha(g, etiquetas.get(n - 1), x1, yAbajo + 15);
        centrar(g, unidad, (x0 + x1) / 2, yAbajo + 15);

        return lienzo.png();
    }

    // =========================================================
    // PIEZAS COMUNES
    // =========================================================

    /** Lienzo con la imagen y el pincel ya configurados. */
    private static final class Lienzo {
        final BufferedImage imagen =
                new BufferedImage(ANCHO * SCALE, ALTO * SCALE, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = imagen.createGraphics();

        Lienzo() {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE);
            g.scale(SCALE, SCALE);
            g.setColor(FONDO);
            g.fillRect(0, 0, ANCHO, ALTO);
        }

        byte[] png() {
            g.dispose();
            try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
                ImageIO.write(imagen, "png", salida);
                return salida.toByteArray();
            } catch (IOException e) {
                throw new IllegalStateException(
                        "No fue posible generar la imagen de la gráfica.", e);
            }
        }
    }

    private static Font fuente(int estilo, int tamano) {
        return new Font(Font.SANS_SERIF, estilo, tamano);
    }

    private static void encabezado(Graphics2D g, String titulo, String subtitulo) {
        g.setColor(TINTA);
        g.setFont(fuente(Font.BOLD, 14));
        g.drawString(titulo, MARGEN, 26);
        if (subtitulo != null && !subtitulo.isBlank()) {
            g.setColor(TINTA_SUAVE);
            g.setFont(fuente(Font.PLAIN, 11));
            g.drawString(subtitulo, MARGEN, 43);
        }
    }

    private static void sinDatos(Graphics2D g) {
        g.setColor(TINTA_SUAVE);
        g.setFont(fuente(Font.PLAIN, 12));
        centrar(g, "Sin datos para mostrar", ANCHO / 2.0, ALTO / 2.0 + 10);
    }

    private static void centrar(Graphics2D g, String texto, double x, double y) {
        int ancho = g.getFontMetrics().stringWidth(texto);
        g.drawString(texto, (float) (x - ancho / 2.0), (float) y);
    }

    private static void derecha(Graphics2D g, String texto, double xFinal, double y) {
        int ancho = g.getFontMetrics().stringWidth(texto);
        g.drawString(texto, (float) (xFinal - ancho), (float) y);
    }

    /** Recorta el texto con "…" si no cabe en el ancho disponible. */
    private static String ajustar(Graphics2D g, String texto, int anchoMaximo) {
        FontMetrics fm = g.getFontMetrics();
        if (fm.stringWidth(texto) <= anchoMaximo) {
            return texto;
        }
        String recortado = texto;
        while (recortado.length() > 1
                && fm.stringWidth(recortado + "…") > anchoMaximo) {
            recortado = recortado.substring(0, recortado.length() - 1);
        }
        return recortado.trim() + "…";
    }

    /** Barra con la punta derecha redondeada (4 px) y la base izquierda recta. */
    private static Path2D barraRedondeadaDerecha(double x, double y,
                                                 double largo, double alto) {
        double r = Math.min(4, Math.min(largo, alto) / 2);
        Path2D.Double p = new Path2D.Double();
        p.moveTo(x, y);
        p.lineTo(x + largo - r, y);
        p.quadTo(x + largo, y, x + largo, y + r);
        p.lineTo(x + largo, y + alto - r);
        p.quadTo(x + largo, y + alto, x + largo - r, y + alto);
        p.lineTo(x, y + alto);
        p.closePath();
        return p;
    }

    // --- Escalas "amables": 10, 20, 50, 100, 200... en lugar de 37, 74 ---

    /** Menor valor "redondo" (1, 2, 5 x 10^n) que no queda por debajo del máximo. */
    static double escalaAmable(double maximo) {
        if (maximo <= 0) return 4;
        double paso = pasoAmable(maximo);
        return Math.ceil(maximo / paso) * paso;
    }

    /**
     * Paso redondo (1, 2, 5 o 10 x 10^n) para dividir el eje en unas
     * 4 o 5 partes. Se elige el más cercano a maximo / 4.
     */
    static double pasoAmable(double maximo) {
        if (maximo <= 0) return 1;
        double bruto = maximo / 4.0;
        double potencia = Math.pow(10, Math.floor(Math.log10(bruto)));
        double fraccion = bruto / potencia;
        double paso;
        if (fraccion <= 1.5) paso = 1;
        else if (fraccion <= 3) paso = 2;
        else if (fraccion <= 7) paso = 5;
        else paso = 10;
        return Math.max(paso * potencia, 1);
    }

    private static String numero(double valor) {
        return String.format(java.util.Locale.forLanguageTag("es-CO"), "%,.0f", valor);
    }
}
