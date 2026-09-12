package com.inventory.supplier.entity;

import jakarta.persistence.*;

/**
 * Represento la entidad Proveedor dentro del sistema INVENTORY.
 *
 * Aquí guardo la información de cada proveedor que suministra productos
 * al inventario. Esta clase está conectada directamente con la tabla
 * "proveedor" de la base de datos mediante Spring Data JPA, siguiendo
 * la misma lógica que ya usé en el módulo de Categorías.
 *
 * @author Angela Carvajal Ortiz
 * @version 1.0
 */
@Entity
@Table(name = "proveedor")
public class SupplierEntity {

    // Identificador único del proveedor, se genera automáticamente en la base de datos.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long id;

    // Nombre del proveedor. Es un campo obligatorio.
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    // Documento de identificación del proveedor (NIT, cédula, etc.).
    @Column(name = "documento", length = 30)
    private String documento;

    // Teléfono de contacto del proveedor.
    @Column(name = "telefono", length = 20)
    private String telefono;

    // Correo electrónico del proveedor. También lo uso para validar duplicados.
    @Column(name = "correo", length = 120)
    private String correo;

    // Dirección física del proveedor.
    @Column(name = "direccion", length = 200)
    private String direccion;

    // Controla si el proveedor está activo o fue desactivado (eliminación lógica).
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    // Constructor vacío que necesita JPA para poder crear el objeto.
    public SupplierEntity() {
    }

    // A partir de aquí dejo los getters y setters de cada campo,
    // los uso para leer y modificar la información del proveedor.

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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
