package com.inventory.jdbcdemo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DEMOSTRACIÓN DE CONEXIÓN JDBC "PURA"
 * --------------------------------------
 * Esta clase NO forma parte de la arquitectura oficial de INVENTORY
 * (que usa Spring Data JPA, ver EKB ADR-004 y ADR-008).
 *
 * Se incluye únicamente para dejar evidencia explícita, literal y
 * comentada de cómo se conecta Java a MySQL usando JDBC "a mano"
 * (DriverManager, Connection, PreparedStatement, ResultSet), tal
 * como lo pide el componente formativo de esta actividad.
 *
 * Es un ejemplo independiente: se puede ejecutar solo, sin levantar
 * todo Spring Boot, para ver la conexión y el CRUD básico de la
 * tabla "categoria" funcionando con JDBC puro.
 *
 * Para ejecutarlo: clic derecho sobre el archivo en IntelliJ ->
 * "Run 'CategoryJdbcDemo.main()'".
 */
public class CategoryJdbcDemo {

    // Datos de conexión (los mismos que en application.properties)
    private static final String URL = "jdbc:mysql://localhost:3306/inventory_db";
    private static final String USUARIO = "root";
    private static final String CLAVE = "TU_CLAVE_MYSQL"; // <-- cámbiala

    public static void main(String[] args) {
        try {
            // 1) INSERTAR una categoría nueva (equivale a RF-9)
            insertarCategoria("Herramientas", "Herramientas manuales y eléctricas");

            // 2) CONSULTAR todas las categorías (equivale a RF-12)
            List<String> categorias = consultarCategorias();
            System.out.println("Categorías registradas:");
            categorias.forEach(System.out::println);

            // 3) ACTUALIZAR una categoría (equivale a RF-10)
            actualizarDescripcion("Herramientas", "Herramientas para el taller");

            // 4) ELIMINAR LÓGICAMENTE una categoría (equivale a RF-11)
            desactivarCategoria("Herramientas");

        } catch (SQLException e) {
            System.out.println("Error de conexión o de SQL: " + e.getMessage());
        }
    }

    // Abre una conexión JDBC nueva. "try-with-resources" la cierra sola.
    private static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    // ---------- INSERT ----------
    private static void insertarCategoria(String nombre, String descripcion) throws SQLException {
        String sql = "INSERT INTO categoria (nombre, descripcion, activo) VALUES (?, ?, true)";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.executeUpdate();

            System.out.println("Categoría insertada correctamente.");
        }
    }

    // ---------- SELECT ----------
    private static List<String> consultarCategorias() throws SQLException {
        String sql = "SELECT id_categoria, nombre, descripcion, activo " +
                     "FROM categoria WHERE activo = true";

        List<String> resultado = new ArrayList<>();

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String linea = rs.getLong("id_categoria") + " - " +
                               rs.getString("nombre") + " - " +
                               rs.getString("descripcion");
                resultado.add(linea);
            }
        }
        return resultado;
    }

    // ---------- UPDATE ----------
    private static void actualizarDescripcion(String nombre, String nuevaDescripcion) throws SQLException {
        String sql = "UPDATE categoria SET descripcion = ? WHERE nombre = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevaDescripcion);
            ps.setString(2, nombre);
            int filas = ps.executeUpdate();

            System.out.println(filas + " categoría(s) actualizada(s).");
        }
    }

    // ---------- "DELETE" (en realidad, eliminación lógica) ----------
    private static void desactivarCategoria(String nombre) throws SQLException {
        String sql = "UPDATE categoria SET activo = false WHERE nombre = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            int filas = ps.executeUpdate();

            System.out.println(filas + " categoría(s) desactivada(s).");
        }
    }
}
