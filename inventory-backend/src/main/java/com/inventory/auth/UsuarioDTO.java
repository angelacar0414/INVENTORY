package com.inventory.auth;

/**
 * DTO (Data Transfer Object) para Usuario
 * Se usa para transferir datos entre el cliente y el servidor
 *
 * @author Darío Bustamante
 * @version 1.0
 */
public class UsuarioDTO {

    // ==================== ATRIBUTOS ====================

    /** Correo electrónico del usuario */
    private String email;

    /** Contraseña en texto plano (solo en solicitudes) */
    private String contraseña;

    // ==================== CONSTRUCTORES ====================

    /** Constructor vacío */
    public UsuarioDTO() {
    }

    /** Constructor con email y contraseña */
    public UsuarioDTO(String email, String contraseña) {
        this.email = email;
        this.contraseña = contraseña;
    }

    // ==================== GETTERS Y SETTERS ====================

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