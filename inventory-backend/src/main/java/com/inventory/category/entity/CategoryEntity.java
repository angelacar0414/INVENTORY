package com.inventory.category.entity;

import jakarta.persistence.*;

/**
 * CLASE ENTITY
 * -------------
 * Una "Entity" es una clase Java que representa UNA TABLA de la base de
 * datos. Cada objeto de esta clase = una fila de la tabla "categoria".
 *
 * Spring Data JPA usa esta clase para saber qué columnas tiene la tabla
 * y para poder guardar/leer datos SIN que nosotros escribamos el SQL
 * a mano (por debajo, sigue usando JDBC, que es el driver que conecta
 * Java con MySQL).
 *
 * Entidad del módulo Categorías.
 */
@Entity                       // Le dice a Spring: "esto es una tabla"
@Table(name = "categoria")    // Nombre exacto de la tabla en MySQL
public class CategoryEntity {

    @Id // Marca el campo como llave primaria (PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "id_categoria")
    private Long id;

    // nullable = false  -> equivale a NOT NULL
    // unique = true     -> equivale a UNIQUE ( nombre no duplicado)
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    // Campo para la ELIMINACIÓN LÓGICA (nunca se borra de verdad,
    // solo se pone en false), como mecanismos de seguridad.
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // ---------- Constructores ----------

    public CategoryEntity() {
        // Constructor vacío obligatorio para que JPA pueda crear objetos
    }

    public CategoryEntity(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = true;
    }

    // ---------- Getters y Setters ----------
    // JPA los necesita para poder leer y escribir los valores.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
