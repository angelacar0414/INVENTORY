package com.inventory.categoria.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * CLASE DE CONEXIÓN
 * -------------------
 * Esta es la clase que abre la conexión JDBC "pura" hacia MySQL,
 * usando DriverManager (sin frameworks). Cualquier clase DAO que
 * necesite hablar con la base de datos llama al método conectar()
 * de aquí, en vez de repetir estos mismos datos en cada archivo.
 *
 * Si cambia la contraseña o el nombre de la base de datos, solo se
 * edita en un único lugar: este archivo.
 */
public class ConexionBD {

    // Datos de conexión (los mismos de siempre: MySQL local, base inventory_db)
    private static final String URL = "jdbc:mysql://localhost:3306/inventory_db?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root"; // contraseña real de MySQL

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de MySQL.", e);
        }

        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}



