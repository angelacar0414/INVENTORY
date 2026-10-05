package com.inventory.product.entity;

import com.inventory.category.entity.CategoryEntity;
import com.inventory.supplier.entity.SupplierEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Entidad que representa un producto dentro del sistema INVENTORY.
 *
 * Esta clase se encuentra relacionada directamente con la tabla
 * "producto" de la base de datos mediante JPA/Hibernate.
 *
 * Un producto pertenece a una categoría y a un proveedor.
 * El stock actual será utilizado posteriormente por el módulo
 * de movimientos para controlar las entradas y salidas.
 */
@Entity
@Table(
    name = "producto",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_producto_codigo",
            columnNames = "codigo"
        )
    }
)
public class ProductEntity {

    /**
     * Identificador único del producto.
     *
     * Se genera automáticamente mediante la estrategia IDENTITY,
     * utilizando el campo AUTO_INCREMENT de MySQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    /**
     * Código único generado para identificar el producto.
     *
     * El código es administrado por el backend y no debe ser
     * modificado manualmente durante la edición del producto.
     */
    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo;

    /**
     * Nombre del producto.
     */
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    /**
     * Descripción opcional del producto.
     */
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    /**
     * Precio al que se adquiere el producto del proveedor.
     */
    @Column(
        name = "precio_compra",
        nullable = false,
        precision = 10,
        scale = 2
    )
    private BigDecimal precioCompra;

    /**
     * Precio de venta establecido para el producto.
     */
    @Column(
        name = "precio_venta",
        nullable = false,
        precision = 10,
        scale = 2
    )
    private BigDecimal precioVenta;

    /**
     * Cantidad disponible actualmente en inventario.
     *
     * Este valor podrá ser modificado posteriormente mediante
     * el módulo de movimientos de inventario.
     */
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual = 0;

    /**
     * Cantidad mínima de unidades que se desea mantener
     * disponible en inventario.
     */
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 0;

    /**
     * Estado actual del producto.
     *
     * El estado puede ser:
     * DISPONIBLE, AGOTADO o INACTIVO.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private ProductStatus estado;

    /**
     * Indica si el producto se encuentra activo dentro del sistema.
     *
     * FALSE representa un producto desactivado lógicamente.
     */
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /**
     * Categoría a la que pertenece el producto.
     *
     * La relación es muchos a uno porque una categoría puede
     * tener varios productos, mientras cada producto pertenece
     * a una categoría.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_categoria",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_producto_categoria")
    )
    private CategoryEntity categoria;

    /**
     * Proveedor asociado al producto.
     *
     * La relación es muchos a uno porque un proveedor puede
     * suministrar varios productos.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_proveedor",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_producto_proveedor")
    )
    private SupplierEntity proveedor;

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

    public ProductStatus getEstado() {
        return estado;
    }

    public void setEstado(ProductStatus estado) {
        this.estado = estado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public CategoryEntity getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoryEntity categoria) {
        this.categoria = categoria;
    }

    public SupplierEntity getProveedor() {
        return proveedor;
    }

    public void setProveedor(SupplierEntity proveedor) {
        this.proveedor = proveedor;
    }
}