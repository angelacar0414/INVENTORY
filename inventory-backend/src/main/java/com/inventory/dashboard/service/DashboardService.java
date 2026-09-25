package com.inventory.dashboard.service;

import com.inventory.dashboard.dto.DashboardDTO;
import com.inventory.dashboard.dto.MovimientoResumenDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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

    private final JdbcTemplate jdbcTemplate;

    public DashboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DashboardDTO obtenerResumen() {
        long totalProductos = contarProductosActivos();
        long stockBajo = contarProductosStockBajo();
        long agotados = contarProductosAgotados();
        long entradasHoy = contarMovimientosDeHoy("ENTRADA");
        long salidasHoy = contarMovimientosDeHoy("SALIDA");
        List<MovimientoResumenDTO> ultimosMovimientos = obtenerUltimosMovimientos(5);

        return new DashboardDTO(totalProductos, stockBajo, agotados,
                entradasHoy, salidasHoy, ultimosMovimientos);
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

    private long contarMovimientosDeHoy(String tipoMovimiento) {
        try {
            String sql = "SELECT COUNT(*) FROM movimiento " +
                    "WHERE tipo_movimiento = ? AND DATE(fecha_movimiento) = CURDATE()";
            Long total = jdbcTemplate.queryForObject(sql, Long.class, tipoMovimiento);
            return total == null ? 0 : total;
        } catch (Exception e) {
            // Todavía no existe la tabla movimiento (el módulo de
            // Movimientos no está armado), así que dejamos el contador en 0.
            return 0;
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
            // Todavía no existen las tablas producto/movimiento, entonces
            // dejamos la lista vacía en lugar de tumbar el Dashboard.
        }
        return lista;
    }
}
