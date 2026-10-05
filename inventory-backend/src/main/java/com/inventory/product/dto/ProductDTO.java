package com.inventory.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) utilizado para transportar
 * la información de los productos entre el backend y el cliente.
 *
 * A diferencia de ProductEntity, este objeto no representa
 * directamente una tabla de la base de datos.
 *
 * Su función principal es controlar la información que entra
 * y sale de la API y aplicar las validaciones necesarias.
 */
public class ProductDTO {

    /**
     * Identificador único del producto.
     *
     * Es generado automáticamente por la base de datos.
     */
    private Long idProducto;

    /**
     * Código único del producto.
     *
     * Este valor será generado por el backend, por lo que
     * el usuario no necesita ingresarlo manualmente.
     */
    private String codigo;

    /**
     * Nombre del producto.
     *
     * Campo obligatorio y con una longitud máxima de 150 caracteres.
     */
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    /**
     * Descripción opcional del producto.
     */
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;

    /**
     * Precio de compra del producto.
     *
     * Debe ser obligatorio y no puede ser negativo.
     */
    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio de compra no puede ser negativo")
    private BigDecimal precioCompra;

    /**
     * Precio de venta del producto.
     *
     * Debe ser obligatorio y no puede ser negativo.
     *
     * El sistema permite que el precio de venta sea menor
     * que el precio de compra, aunque posteriormente el
     * frontend podrá mostrar una advertencia al usuario.
     */
    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio de venta no puede ser negativo")
    private BigDecimal precioVenta;

    /**
     * Stock actual del producto.
     *
     * Este campo solamente se utiliza para establecer
     * el stock inicial durante la creación.
     *
     * Las modificaciones posteriores del stock se realizarán
     * mediante el módulo de Movimientos.
     */
    @Min(value = 0, message = "El stock actual no puede ser negativo")
    private Integer stockActual;

    /**
     * Cantidad mínima de unidades que se desea mantener
     * disponible en inventario.
     */
    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    /**
     * Identificador de la categoría asociada al producto.
     */
    @NotNull(message = "La categoría es obligatoria")
    private Long idCategoria;

    /**
     * Nombre de la categoría.
     *
     * Se utiliza principalmente para mostrar información
     * al consultar productos.
     */
    private String nombreCategoria;

    /**
     * Identificador del proveedor asociado al producto.
     */
    @NotNull(message = "El proveedor es obligatorio")
    private Long idProveedor;

    /**
     * Nombre del proveedor.
     *
     * Se utiliza principalmente para mostrar información
     * al consultar productos.
     */
    private String nombreProveedor;

    /**
     * Estado actual del producto.
     *
     * Puede ser DISPONIBLE, AGOTADO o INACTIVO.
     */
    private String estado;

    /**
     * Indica si el producto se encuentra activo.
     */
    private Boolean activo;

    // ---------------------------------------------------------
    // Getters y Setters
    // ---------------------------------------------------------

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public Long getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Long idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}