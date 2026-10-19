package com.inventory.dashboard.service;

import com.inventory.dashboard.dto.DashboardDTO;
import com.inventory.dashboard.dto.MovimientoResumenDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Este Service es distinto a los que ya armamos en Categorías o Proveedores,
 * porque el Dashboard no tiene Entity ni Repository propio: solo consulta y
 * agrupa información que ya está guardada en otras tablas (producto y
 * movimiento). Por esta razón usamos JdbcTemplate en vez de un
 * JpaRepository: así consultamos directo las tablas de la base de datos sin
 * necesitar que ya exista el módulo Java de Productos o de Movimientos.
 *
 * La idea es que cuando armemos esos dos módulos completos (con su Entity,
 * Repository, etc.) este Service siga funcionando igual, porque las tablas
 * "producto" y "movimiento" no van a cambiar de nombre ni de columnas.
 *
 * Dejamos cada consulta dentro de su propio try/catch: así, si todavía no
 * hemos creado la tabla producto o movimiento en MySQL, el Dashboard no se
 * cae con un error 500, sino que simplemente muestra el contador en cero
 * mientras tanto.
 */
@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final JdbcTemplate jdbcTemplate;

    public DashboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DashboardDTO obtenerResumen() {
        long totalProductos = contarProductosActivos();
        long totalCategorias = contarActivos("categoria");
        long totalProveedores = contarActivos("proveedor");
        long totalClientes = contarActivos("cliente");
        long totalMovimientos = contarTodosLosMovimientos();
        long stockBajo = contarProductosStockBajo();
        long agotados = contarProductosAgotados();
        // Cada arreglo trae {cantidad de movimientos, unidades movidas}.
        long[] entradas = resumenDeHoy("ENTRADA");
        long[] salidas = resumenDeHoy("SALIDA");
        List<MovimientoResumenDTO> ultimosMovimientos = obtenerUltimosMovimientos(5);

        return new DashboardDTO(totalProductos, totalCategorias,
                totalProveedores, totalClientes, totalMovimientos, stockBajo, agotados,
                entradas[0], salidas[0], entradas[1], salidas[1], ultimosMovimientos);
    }

    private long contarProductosActivos() {
        try {
            String sql = "SELECT COUNT(*) FROM producto WHERE activo = true";
            Long total = jdbcTemplate.queryForObject(sql, Long.class);
            return total == null ? 0 : total;
        } catch (Exception e) {
            // Todavía no existe la tabla producto (el módulo de Productos
            // no está armado), así que por ahora dejamos el contador en 0.
            return 0;
        }
    }

    /**
     * Cuenta los registros activos de una tabla (RF-027: totales de
     * categorías, proveedores y clientes).
     *
     * El nombre de la tabla no viene del usuario: solo lo llama este mismo
     * Service con textos fijos, por eso es seguro concatenarlo en la consulta.
     */
    private long contarActivos(String tabla) {
        try {
            String sql = "SELECT COUNT(*) FROM " + tabla + " WHERE activo = true";
            Long total = jdbcTemplate.queryForObject(sql, Long.class);
            return total == null ? 0 : total;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Total histórico de movimientos registrados (entradas y salidas). */
    private long contarTodosLosMovimientos() {
        try {
            Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM movimiento", Long.class);
            return total == null ? 0 : total;
        } catch (Exception e) {
            return 0;
        }
    }

    private long contarProductosStockBajo() {
        try {
            String sql = "SELECT COUNT(*) FROM producto " +
                    "WHERE activo = true AND stock_actual > 0 AND stock_actual <= stock_minimo";
            Long total = jdbcTemplate.queryForObject(sql, Long.class);
            return total == null ? 0 : total;
        } catch (Exception e) {
            return 0;
        }
    }

    private long contarProductosAgotados() {
        try {
            String sql = "SELECT COUNT(*) FROM producto WHERE activo = true AND stock_actual = 0";
            Long total = jdbcTemplate.queryForObject(sql, Long.class);
            return total == null ? 0 : total;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Resume la actividad de HOY de un tipo de movimiento (ENTRADA o SALIDA).
     *
     * Devuelve dos números:
     *   [0] cuántos movimientos se registraron hoy.
     *   [1] cuántas unidades se movieron en total (la suma de las cantidades).
     *
     * Antes usábamos DATE(fecha_movimiento) = CURDATE(). El problema es que
     * CURDATE() lo calcula MySQL con SU zona horaria, y las fechas de los
     * movimientos las guarda Java con la suya (la URL de conexión tiene
     * serverTimezone=UTC). En la noche, cuando en Colombia todavía es "hoy"
     * pero en UTC ya es "mañana", el contador se quedaba en cero.
     *
     * Ahora el rango de "hoy" lo calcula Java con LocalDate.now(), que es el
     * mismo reloj con el que se guardó el movimiento, y se lo enviamos a
     * MySQL como Timestamp (igual que hace el módulo de Reportes).
     */
    private long[] resumenDeHoy(String tipoMovimiento) {
        try {
            LocalDate hoy = LocalDate.now();
            Timestamp desde = Timestamp.valueOf(hoy.atStartOfDay());
            Timestamp hasta = Timestamp.valueOf(hoy.plusDays(1).atStartOfDay());

            String sql = "SELECT COUNT(*), COALESCE(SUM(cantidad), 0) FROM movimiento " +
                    "WHERE tipo_movimiento = ? AND fecha_movimiento >= ? AND fecha_movimiento < ?";

            return jdbcTemplate.query(sql, (ResultSetExtractor<long[]>) rs -> {
                if (rs.next()) {
                    return new long[]{rs.getLong(1), rs.getLong(2)};
                }
                return new long[]{0, 0};
            }, tipoMovimiento, desde, hasta);
        } catch (Exception e) {
            // No tumbamos el Dashboard, pero dejamos el motivo en la consola
            // del Backend para poder encontrar el problema.
            log.error("No se pudo calcular la actividad de hoy ({})", tipoMovimiento, e);
            return new long[]{0, 0};
        }
    }

    private List<MovimientoResumenDTO> obtenerUltimosMovimientos(int cantidad) {
        List<MovimientoResumenDTO> lista = new ArrayList<>();
        try {
            String sql = "SELECT m.fecha_movimiento, p.nombre AS producto, m.tipo_movimiento, " +
                    "m.cantidad, CONCAT(u.nombre, ' ', u.apellido) AS usuario, c.nombre AS cliente " +
                    "FROM movimiento m " +
                    "JOIN producto p ON m.id_producto = p.id_producto " +
                    "JOIN usuario u ON m.id_usuario = u.id_usuario " +
                    "LEFT JOIN cliente c ON m.id_cliente = c.id_cliente " +
                    "ORDER BY m.fecha_movimiento DESC " +
                    "LIMIT ?";

            lista = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Timestamp fecha = rs.getTimestamp("fecha_movimiento");
                return new MovimientoResumenDTO(
                        fecha != null ? fecha.toLocalDateTime() : null,
                        rs.getString("producto"),
                        rs.getString("tipo_movimiento"),
                        rs.getInt("cantidad"),
                        rs.getString("usuario"),
                        rs.getString("cliente")
                );
            }, cantidad);
        } catch (Exception e) {
            // Dejamos la lista vacía en lugar de tumbar el Dashboard, pero
            // registramos el error para no perderlo en silencio.
            log.error("No se pudieron consultar los últimos movimientos", e);
        }
        return lista;
    }
}
