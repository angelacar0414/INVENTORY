package com.inventory.categoria.modelo;

/**
 * CLASE MODELO
 * -------------
 * Representa una categoría dentro del programa. Es una clase sencilla,
 * solo con atributos, getters y setters (por eso se le llama "POJO":
 * Plain Old Java Object). Aquí no hay conexión a base de datos ni
 * lógica, solo la "forma" que tiene un objeto Categoría en memoria.
 */
public class Categoria {

    private int id;
    private String nombre;
    private String descripcion;
    private boolean activo;

    public Categoria() {
    }

    public Categoria(int id, String nombre, String descripcion, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    // Constructor útil para cuando se va a crear una categoría nueva
    // (todavía no tiene id, porque MySQL se lo asigna solo)
    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
