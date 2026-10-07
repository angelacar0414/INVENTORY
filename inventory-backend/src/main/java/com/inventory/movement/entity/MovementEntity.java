package com.inventory.movement.entity;

import com.inventory.auth.UsuarioEntity;
import com.inventory.client.entity.ClientEntity;
import com.inventory.product.entity.ProductEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un movimiento de inventario.
 *
 * Esta clase se encuentra relacionada directamente con la tabla
 * "movimiento" de la base de datos mediante JPA/Hibernate.
 *
 * Cada movimiento es una ENTRADA o una SALIDA de unidades de un producto
 * y funciona como el historial del inventario. Por esa razón:
 *
 * - Nunca se elimina (no existe eliminación lógica ni física).
 * - Nunca se modifica después de registrado: todas las columnas están
 *   marcadas como updatable = false.
 *
 * Relaciones:
 *
 * - Muchos movimientos pertenecen a un producto.
 * - Muchos movimientos son registrados por un usuario.
 * - Muchos movimientos de SALIDA pueden estar asociados a un cliente.
 *   En las ENTRADAS el cliente es null.
 */
@Entity
@Table(name = "movimiento")
public class MovementEntity {

    /**
     * Identificador único del movimiento.
     *
     * Se genera automáticamente mediante el AUTO_INCREMENT de MySQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long idMovimiento;

    /**
     * Tipo de movimiento: ENTRADA o SALIDA.
     */
    @Enumerated(EnumType.STRING)
    @Column(
        name = "tipo_movimiento",
        nullable = false,
        updatable = false,
        length = 10
    )
    private MovementType tipoMovimiento;

    /**
     * Cantidad de unidades que entran o salen del inventario.
     *
     * Siempre es mayor que cero.
     */
    @Column(name = "cantidad", nullable = false, updatable = false)
    private Integer cantidad;

    /**
     * Stock que tenía el producto justo antes del movimiento.
     */
    @Column(name = "stock_anterior", nullable = false, updatable = false)
    private Integer stockAnterior;

    /**
     * Stock que quedó en el producto justo después del movimiento.
     */
    @Column(name = "stock_posterior", nullable = false, updatable = false)
    private Integer stockPosterior;

    /**
     * Fecha y hora en la que se registró el movimiento.
     */
    @Column(name = "fecha_movimiento", nullable = false, updatable = false)
    private LocalDateTime fechaMovimiento;

    /**
     * Observación opcional escrita por el usuario.
     */
    @Column(name = "observacion", length = 255, updatable = false)
    private String observacion;

    /**
     * Producto sobre el cual se realizó el movimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_producto",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_movimiento_producto")
    )
    private ProductEntity producto;

    /**
     * Usuario que registró el movimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_usuario",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_movimiento_usuario")
    )
    private UsuarioEntity usuario;

    /**
     * Cliente que recibió los productos.
     *
     * Es obligatorio únicamente en las SALIDAS. En las ENTRADAS
     * permanece en null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "id_cliente",
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_movimiento_cliente")
    )
    private ClientEntity cliente;

    // ---------------------------------------------------------
    // Getters y Setters
    // ---------------------------------------------------------

    public Long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public MovementType getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(MovementType tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getStockAnterior() {
        return stockAnterior;
    }

    public void setStockAnterior(Integer stockAnterior) {
        this.stockAnterior = stockAnterior;
    }

    public Integer getStockPosterior() {
        return stockPosterior;
    }

    public void setStockPosterior(Integer stockPosterior) {
        this.stockPosterior = stockPosterior;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public ProductEntity getProducto() {
        return producto;
    }

    public void setProducto(ProductEntity producto) {
        this.producto = producto;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public ClientEntity getCliente() {
        return cliente;
    }

    public void setCliente(ClientEntity cliente) {
        this.cliente = cliente;
    }
}
