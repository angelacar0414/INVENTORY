package com.inventory.user.dto;

/**
 * DTO utilizado para recibir la información necesaria
 * para crear un nuevo usuario desde el módulo de usuarios.
 *
 * La contraseña solamente se recibe para poder procesarla
 * y almacenarla de forma segura mediante BCrypt.
 *
 * @author Dario Bustamante
 * @version 1.0
 */
public class UserCreateDTO {

    // ==================== ATRIBUTOS ====================

    /** Nombre del usuario */
    private String nombre;

    /** Apellido del usuario */
    private String apellido;

    /** Nombre de usuario utilizado para iniciar sesión */
    private String username;

    /** Correo electrónico del usuario */
    private String email;

    /** Contraseña proporcionada al crear el usuario */
    private String contraseña;

    /** Rol que tendrá el usuario */
    private String rol;


    // ==================== CONSTRUCTOR ====================

    /**
     * Constructor vacío requerido para recibir
     * información mediante JSON.
     */
    public UserCreateDTO() {
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}