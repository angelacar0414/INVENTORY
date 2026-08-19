package com.inventory.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * CLASE DTO (Data Transfer Object)
 * ---------------------------------
 * Es el objeto que "viaja" entre el Frontend (React) y el Backend
 * (Spring Boot) en formato JSON.
 *
 * ¿Por qué no usamos directamente la Entity?
 * Porque así protegemos la estructura interna de la base de datos:
 * si mañana cambiamos algo en la tabla, el Frontend no se entera,
 * porque sigue hablando con el DTO.
 *
 * Corresponde al Documento 9 (Capas del sistema) del EKB.
 */
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;

    private Boolean activo;

    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String nombre, String descripcion, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    // ---------- Getters y Setters ----------

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
