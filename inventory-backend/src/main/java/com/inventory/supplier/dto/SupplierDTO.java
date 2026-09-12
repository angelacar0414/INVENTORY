package com.inventory.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Este es el objeto que uso para enviar y recibir la información de un
 * proveedor entre el Frontend y el Backend, en lugar de exponer
 * directamente la entidad SupplierEntity.
 *
 * Las anotaciones de validación (@NotBlank, @Email, @Size) me permiten
 * revisar automáticamente los datos que llegan en cada solicitud, antes
 * de que el Service intente guardarlos.
 *
 * @author Angela Carvajal Ortiz
 * @version 1.0
 */
public class SupplierDTO {

    // El id solo lo devuelvo en las respuestas, no lo recibo al crear un proveedor nuevo.
    private Long id;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @Size(max = 30, message = "El documento no puede superar los 30 caracteres")
    private String documento;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String telefono;

    @Email(message = "El correo electrónico no tiene un formato válido")
    @Size(max = 120, message = "El correo no puede superar los 120 caracteres")
    private String correo;

    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    private String direccion;

    // El estado activo no lo recibo desde el Frontend al crear un proveedor;
    // ese valor lo controla el Backend automáticamente.
    private Boolean activo;

    public SupplierDTO() {
    }

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
