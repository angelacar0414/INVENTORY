package com.inventory.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad Usuario para autenticación y registro
 * Representa un usuario registrado en el sistema
 *
 * @author Darío Bustamante
 * @version 1.0
 */
@Entity
@Table(name = "usuario")
public class UsuarioEntity {

    // ==================== ATRIBUTOS ====================

    /** Identificador único del usuario */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    /** Correo electrónico del usuario (único) */
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    /** Contraseña encriptada del usuario */
    @Column(name = "contraseña_encriptada", nullable = false)
    private String contraseñaEncriptada;

    /** Indica si el usuario está activo o inactivo */
    @Column(name = "activo")
    private Boolean activo = true;

    /** Fecha de creación del registro */
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    /** Fecha de última actualización */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // ==================== CONSTRUCTORES ====================

    /** Constructor vacío (JPA) */
    public UsuarioEntity() {
    }

    /** Constructor con email y contraseña */
    public UsuarioEntity(String email, String contraseñaEncriptada) {
        this.email = email;
        this.contraseñaEncriptada = contraseñaEncriptada;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ==================== GETTERS Y SETTERS ====================

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseñaEncriptada() {
        return contraseñaEncriptada;
    }

    public void setContraseñaEncriptada(String contraseñaEncriptada) {
        this.contraseñaEncriptada = contraseñaEncriptada;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}