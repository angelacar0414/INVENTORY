package com.inventory.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad Usuario para autenticación y registro.
 * Representa un usuario registrado en el sistema.
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

    /** Nombre del usuario */
    @Column(name = "nombre")
    private String nombre;

    /** Apellido del usuario */
    @Column(name = "apellido")
    private String apellido;

    /** Nombre de usuario utilizado para iniciar sesión */
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    /** Correo electrónico del usuario */
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    /** Contraseña encriptada del usuario */
    @Column(name = "contraseña_encriptada", nullable = false)
    private String contraseñaEncriptada;

    /** Rol del usuario dentro del sistema */
    @Column(name = "rol", nullable = false)
    private String rol;

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

    /** Constructor vacío requerido por JPA */
    public UsuarioEntity() {
    }

    /**
     * Constructor básico con email y contraseña.
     * Se mantiene temporalmente para no romper el código actual
     * mientras adaptamos el servicio de autenticación.
     */
    public UsuarioEntity(String email, String contraseñaEncriptada) {
        this.email = email;
        this.contraseñaEncriptada = contraseñaEncriptada;
        this.activo = true;
        this.rol = "OPERADOR";
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

    public String getContraseñaEncriptada() {
        return contraseñaEncriptada;
    }

    public void setContraseñaEncriptada(String contraseñaEncriptada) {
        this.contraseñaEncriptada = contraseñaEncriptada;
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