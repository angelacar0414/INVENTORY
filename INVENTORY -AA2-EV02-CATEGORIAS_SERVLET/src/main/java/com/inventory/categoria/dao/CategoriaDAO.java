package com.inventory.categoria.dao;

import com.inventory.categoria.modelo.Categoria;
import com.inventory.categoria.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * CLASE DAO (Data Access Object)
 * ---------------------------------
 * Es la única clase que ejecuta sentencias SQL contra la tabla
 * "categoria". El Servlet nunca escribe SQL directamente: le pide
 * a esta clase que inserte, consulte, actualice o desactive.
 */
public class CategoriaDAO {

    // Trae solo las categorías ACTIVAS (listado normal)
    public List<Categoria> listarActivas() throws SQLException {
        String sql = "SELECT id_categoria, nombre, descripcion, activo " +
                "FROM categoria WHERE activo = true ORDER BY id_categoria";
        return ejecutarListado(sql);
    }

    // Trae TODAS las categorías, activas e inactivas (para no "perderlas")
    public List<Categoria> listarTodas() throws SQLException {
        String sql = "SELECT id_categoria, nombre, descripcion, activo " +
                "FROM categoria ORDER BY id_categoria";
        return ejecutarListado(sql);
    }

    // Método reutilizado por listarActivas() y listarTodas() para no repetir código
    private List<Categoria> ejecutarListado(String sql) throws SQLException {
        List<Categoria> lista = new ArrayList<>();

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearFila(rs));
            }
        }
        return lista;
    }

    // Busca una categoría por su id (para el formulario de edición)
    public Categoria buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_categoria, nombre, descripcion, activo " +
                "FROM categoria WHERE id_categoria = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearFila(rs);
                }
            }
        }
        return null;
    }

    // Valida si ya existe una categoría con ese nombre (al CREAR)
    public boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categoria WHERE LOWER(nombre) = LOWER(?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Igual que existeNombre, pero IGNORANDO la propia categoría (al EDITAR)
    public boolean existeNombreEnOtraCategoria(String nombre, int idActual) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categoria WHERE LOWER(nombre) = LOWER(?) AND id_categoria <> ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, idActual);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Inserta una categoría nueva
    public void insertar(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categoria (nombre, descripcion, activo) VALUES (?, ?, true)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.executeUpdate();
        }
    }

    // Actualiza nombre y descripción de una categoría existente
    public void actualizar(Categoria categoria) throws SQLException {
        String sql = "UPDATE categoria SET nombre = ?, descripcion = ? WHERE id_categoria = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setInt(3, categoria.getId());
            ps.executeUpdate();
        }
    }

    // Desactiva la categoría (eliminación lógica)
    public void desactivar(int id) throws SQLException {
        String sql = "UPDATE categoria SET activo = false WHERE id_categoria = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Reactiva una categoría que estaba desactivada
    public void reactivar(int id) throws SQLException {
        String sql = "UPDATE categoria SET activo = true WHERE id_categoria = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Categoria mapearFila(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setId(rs.getInt("id_categoria"));
        c.setNombre(rs.getString("nombre"));
        c.setDescripcion(rs.getString("descripcion"));
        c.setActivo(rs.getBoolean("activo"));
        return c;
    }
}