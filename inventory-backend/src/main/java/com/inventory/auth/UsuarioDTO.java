package com.inventory.auth;

/**
 * DTO (Data Transfer Object) para transportar los datos
 * necesarios para el registro y autenticación de usuarios.
 *
 * @author Darío Bustamante
 * @version 1.0
 */
public class UsuarioDTO {

    // ==================== ATRIBUTOS ====================

    /** Nombre del usuario */
    private String nombre;

    /** Apellido del usuario */
    private String apellido;

    /** Nombre de usuario utilizado para iniciar sesión */
    private String username;

    /** Correo electrónico del usuario */
    private String email;

    /** Contraseña enviada por el usuario */
    private String contraseña;


    // ==================== CONSTRUCTORES ====================

    /** Constructor vacío */
    public UsuarioDTO() {
    }

    /**
     * Constructor utilizado principalmente para autenticación.
     *
     * @param username nombre de usuario
     * @param contraseña contraseña del usuario
     */
    public UsuarioDTO(String username, String contraseña) {
        this.username = username;
        this.contraseña = contraseña;
    }


    // ==================== GETTERS Y SETTERS ====================

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
}