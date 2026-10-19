package com.inventory.report.repository;

import com.inventory.report.dto.MovementReportFilter;
import com.inventory.report.dto.MovementReportRowDTO;
import com.inventory.report.dto.ProductReportFilter;
import com.inventory.report.dto.ProductReportRowDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY DE REPORTES
 * ----------------------
 * Los reportes son consultas de solo lectura que cruzan varias tablas
 * (producto, categoría, proveedor, movimiento, usuario, cliente) y
 * devuelven datos ya "aplanados". Por eso, igual que el
 * DashboardService, se usa JdbcTemplate con SQL en lugar de entidades
 * JPA: no hay nada que guardar ni modificar.
 *
 * Los filtros se agregan al SQL solo cuando el usuario los eligió, y
 * sus valores siempre viajan como parámetros "?" (nunca pegados al
 * texto), lo que evita la inyección SQL.
 */
@Repository
public class ReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================================================
    // PRODUCTOS
    // =========================================================

    public List<ProductReportRowDTO> consultarProductos(ProductReportFilter filtro) {

        StringBuilder sql = new StringBuilder(
                "SELECT p.id_producto, p.codigo, p.nombre, " +
                "c.nombre AS categoria, pr.nombre AS proveedor, " +
                "p.stock_actual, p.stock_minimo, " +
                "p.precio_compra, p.precio_venta, p.estado, p.activo " +
                "FROM producto p " +
                "JOIN categoria c ON p.id_categoria = c.id_categoria " +
                "JOIN proveedor pr ON p.id_proveedor = pr.id_proveedor " +
                "WHERE 1 = 1");

        List<Object> parametros = new ArrayList<>();

        if (filtro.estado() != null) {
            sql.append(" AND p.estado = ?");
            parametros.add(filtro.estado());
        }

        if (filtro.idProveedor() != null) {
            sql.append(" AND p.id_proveedor = ?");
            parametros.add(filtro.idProveedor());
        }

        if (filtro.idCategoria() != null) {
            sql.append(" AND p.id_categoria = ?");
            parametros.add(filtro.idCategoria());
        }

        if (filtro.stockBajo()) {
            sql.append(" AND p.activo = true")
               .append(" AND p.stock_actual > 0")
               .append(" AND p.stock_actual <= p.stock_minimo");
        }

        sql.append(" ORDER BY p.nombre ASC, p.id_producto ASC");

        return jdbcTemplate.query(sql.toString(), (rs, fila) -> {

            int stockActual = rs.getInt("stock_actual");
            int stockMinimo = rs.getInt("stock_minimo");
            boolean activo = rs.getBoolean("activo");

            return new ProductReportRowDTO(
                    rs.getLong("id_producto"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getString("proveedor"),
                    stockActual,
                    stockMinimo,
                    rs.getBigDecimal("precio_compra"),
                    rs.getBigDecimal("precio_venta"),
                    rs.getString("estado"),
                    activo && stockActual > 0 && stockActual <= stockMinimo
            );
        }, parametros.toArray());
    }

    // =========================================================
    // MOVIMIENTOS
    // =========================================================

    public List<MovementReportRowDTO> consultarMovimientos(MovementReportFilter filtro) {

        StringBuilder sql = new StringBuilder(
                "SELECT m.id_movimiento, m.fecha_movimiento, m.tipo_movimiento, " +
                "p.codigo, p.nombre AS producto, m.cantidad, " +
                "m.stock_anterior, m.stock_posterior, " +
                "c.nombre AS cliente, " +
                "u.nombre AS usuario_nombre, u.apellido AS usuario_apellido, " +
                "m.observacion " +
                "FROM movimiento m " +
                "JOIN producto p ON m.id_producto = p.id_producto " +
                "JOIN usuario u ON m.id_usuario = u.id_usuario " +
                "LEFT JOIN cliente c ON m.id_cliente = c.id_cliente " +
                "WHERE 1 = 1");

        List<Object> parametros = new ArrayList<>();

        if (filtro.desde() != null) {
            sql.append(" AND m.fecha_movimiento >= ?");
            parametros.add(Timestamp.valueOf(filtro.desde().atStartOfDay()));
        }

        // "hasta" es inclusivo: comparamos contra el inicio del día siguiente.
        if (filtro.hasta() != null) {
            sql.append(" AND m.fecha_movimiento < ?");
            parametros.add(Timestamp.valueOf(
                    filtro.hasta().plusDays(1).atStartOfDay()));
        }

        if (filtro.tipo() != null) {
            sql.append(" AND m.tipo_movimiento = ?");
            parametros.add(filtro.tipo());
        }

        if (filtro.idProducto() != null) {
            sql.append(" AND m.id_producto = ?");
            parametros.add(filtro.idProducto());
        }

        if (filtro.idCliente() != null) {
            sql.append(" AND m.id_cliente = ?");
            parametros.add(filtro.idCliente());
        }

        sql.append(" ORDER BY m.fecha_movimiento DESC, m.id_movimiento DESC");

        return jdbcTemplate.query(sql.toString(), (rs, fila) -> {

            Timestamp fecha = rs.getTimestamp("fecha_movimiento");
            String nombre = rs.getString("usuario_nombre");
            String apellido = rs.getString("usuario_apellido");

            return new MovementReportRowDTO(
                    rs.getLong("id_movimiento"),
                    fecha != null ? fecha.toLocalDateTime() : null,
                    rs.getString("tipo_movimiento"),
                    rs.getString("codigo"),
                    rs.getString("producto"),
                    rs.getInt("cantidad"),
                    rs.getInt("stock_anterior"),
                    rs.getInt("stock_posterior"),
                    rs.getString("cliente"),
                    ((nombre == null ? "" : nombre) + " "
                            + (apellido == null ? "" : apellido)).trim(),
                    rs.getString("observacion")
            );
        }, parametros.toArray());
    }

    // =========================================================
    // BÚSQUEDAS PUNTUALES (para validar filtros y nombrarlos)
    // =========================================================

    public Optional<String> nombreProveedor(Long id) {
        return primero("SELECT nombre FROM proveedor WHERE id_proveedor = ?", id);
    }

    public Optional<String> nombreCategoria(Long id) {
        return primero("SELECT nombre FROM categoria WHERE id_categoria = ?", id);
    }

    public Optional<String> nombreCliente(Long id) {
        return primero("SELECT nombre FROM cliente WHERE id_cliente = ?", id);
    }

    /** Devuelve "CODIGO - Nombre" del producto, o vacío si no existe. */
    public Optional<String> descripcionProducto(Long id) {
        return primero(
                "SELECT CONCAT(codigo, ' - ', nombre) FROM producto " +
                "WHERE id_producto = ?", id);
    }

    private Optional<String> primero(String sql, Long id) {
        List<String> resultado = jdbcTemplate.query(
                sql, (rs, fila) -> rs.getString(1), id);
        return resultado.isEmpty()
                ? Optional.empty()
                : Optional.ofNullable(resultado.get(0));
    }
}
