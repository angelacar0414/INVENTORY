package com.inventory.movement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) utilizado para transportar la información
 * de los movimientos de inventario entre el backend y el cliente.
 *
 * Siguiendo la decisión del proyecto (un único DTO por entidad), esta
 * misma clase se usa para recibir y para devolver información:
 *
 * CAMPOS QUE ENVÍA EL FRONTEND al registrar una entrada o una salida:
 *
 * - idProducto
 * - cantidad
 * - observacion (opcional)
 * - idCliente (obligatorio solo en las salidas)
 *
 * CAMPOS QUE CALCULA EL BACKEND y que solo se devuelven en las respuestas
 * (si el frontend los envía, se ignoran):
 *
 * - idMovimiento, tipoMovimiento, stockAnterior, stockPosterior,
 *   fechaMovimiento y los nombres de producto, usuario y cliente.
 *
 * El tipo de movimiento NO se recibe en el cuerpo de la solicitud: lo
 * define el endpoint utilizado (/entrada o /salida).
 */
public class MovementDTO {

    // ---------------------------------------------------------
    // DATOS QUE ENVÍA EL FRONTEND
    // ---------------------------------------------------------

    /**
     * Identificador del producto sobre el cual se hace el movimiento.
     */
    @NotNull(message = "El producto es obligatorio")
    private Long idProducto;

    /**
     * Cantidad de unidades del movimiento. Debe ser mayor que cero.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor que cero")
    private Integer cantidad;

    /**
     * Observación opcional del movimiento.
     */
    @Size(max = 255, message = "La observación no puede superar los 255 caracteres")
    private String observacion;

    /**
     * Identificador del cliente que recibe los productos.
     *
     * Obligatorio en las SALIDAS. En las ENTRADAS se ignora.
     */
    private Long idCliente;

    // ---------------------------------------------------------
    // DATOS QUE CALCULA EL BACKEND (solo respuesta)
    // ---------------------------------------------------------

    /**
     * Identificador único del movimiento.
     */
    private Long idMovimiento;

    /**
     * Tipo de movimiento: ENTRADA o SALIDA.
     */
    private String tipoMovimiento;

    /**
     * Stock del producto antes del movimiento.
     */
    private Integer stockAnterior;

    /**
     * Stock del producto después del movimiento.
     */
    private Integer stockPosterior;

    /**
     * Fecha y hora en que se registró el movimiento.
     */
    private LocalDateTime fechaMovimiento;

    /**
     * Código del producto (para mostrarlo en las tablas del frontend).
     */
    private String codigoProducto;

    /**
     * Nombre del producto (para mostrarlo en las tablas del frontend).
     */
    private String nombreProducto;

    /**
     * Identificador del usuario que registró el movimiento.
     */
    private Long idUsuario;

    /**
     * Nombre completo del usuario que registró el movimiento.
     */
    private String nombreUsuario;

    /**
     * Nombre del cliente asociado (solo en salidas).
     */
    private String nombreCliente;

    // ---------------------------------------------------------
    // Getters y Setters
    // ---------------------------------------------------------

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
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

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
}
