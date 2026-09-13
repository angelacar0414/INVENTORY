package com.inventory.user.dto;

/**
 * DTO utilizado para transportar la información de los usuarios
 * en el módulo de gestión de usuarios.
 *
 * No incluye la contraseña para evitar exponer información sensible.
 *
 * @author Darío Bustamante
 * @version 1.0
 */
public class UserDTO {

    // ==================== ATRIBUTOS ====================

    /** Identificador único del usuario */
    private Long idUsuario;

    /** Nombre del usuario */
    private String nombre;

    /** Apellido del usuario */
    private String apellido;

    /** Nombre de usuario utilizado para iniciar sesión */
    private String username;

    /** Correo electrónico del usuario */
    private String email;

    /** Rol asignado al usuario */
    private String rol;

    /** Indica si el usuario está activo */
    private Boolean activo;


    // ==================== CONSTRUCTORES ====================

    /** Constructor vacío requerido para crear el DTO */
    public UserDTO() {
    }

    /**
     * Constructor para representar la información completa
     * de un usuario.
     */
    public UserDTO(
            Long idUsuario,
            String nombre,
            String apellido,
            String username,
            String email,
            String rol,
            Boolean activo) {

        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.email = email;
        this.rol = rol;
        this.activo = activo;
    }


    // ==================== GETTERS Y SETTERS ====================

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}