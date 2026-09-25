package com.inventory.dashboard.dto;

import java.time.LocalDateTime;

/**
 * Con esta clase armamos cada fila de la tabla "Últimos movimientos" que
 * aparece abajo en el Dashboard. La armamos aparte del DTO de Movimientos
 * (el que va a tener el módulo de Movimientos más adelante) porque aquí solo
 * necesitamos lo justo para mostrar en pantalla, no toda la información del
 * movimiento completo.
 */
public class MovimientoResumenDTO {

    private LocalDateTime fecha;
    private String producto;
    private String tipoMovimiento;
    private Integer cantidad;
    private String usuario;
    private String cliente;

    public MovimientoResumenDTO() {
    }

    public MovimientoResumenDTO(LocalDateTime fecha, String producto, String tipoMovimiento,
                                 Integer cantidad, String usuario, String cliente) {
        this.fecha = fecha;
        this.producto = producto;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.usuario = usuario;
        this.cliente = cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
}
