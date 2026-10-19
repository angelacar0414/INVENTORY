package com.inventory.dashboard.dto;

import java.util.List;

/**
 * Este es el objeto que armamos para mandarle al Frontend todo lo que necesita
 * pintar la pantalla del Dashboard: las tarjetas de arriba (productos totales,
 * stock bajo, agotados, entradas y salidas de hoy) y la tabla de últimos
 * movimientos.
 *
 * No es una Entity porque el Dashboard no tiene tabla propia en la base de
 * datos, solo consulta y junta información que ya existe en otras tablas
 * (producto, movimiento, etc).
 */
public class DashboardDTO {

    private long totalProductos;
    private long totalCategorias;
    private long totalProveedores;
    private long totalClientes;
    private long totalMovimientos;
    private long stockBajo;
    private long agotados;
    private long entradasHoy;
    private long salidasHoy;
    private long unidadesEntradasHoy;
    private long unidadesSalidasHoy;
    private List<MovimientoResumenDTO> ultimosMovimientos;

    public DashboardDTO() {
    }

    public DashboardDTO(long totalProductos, long totalCategorias,
                         long totalProveedores, long totalClientes,
                         long totalMovimientos, long stockBajo, long agotados,
                         long entradasHoy, long salidasHoy,
                         long unidadesEntradasHoy, long unidadesSalidasHoy,
                         List<MovimientoResumenDTO> ultimosMovimientos) {
        this.totalProductos = totalProductos;
        this.totalCategorias = totalCategorias;
        this.totalProveedores = totalProveedores;
        this.totalClientes = totalClientes;
        this.totalMovimientos = totalMovimientos;
        this.stockBajo = stockBajo;
        this.agotados = agotados;
        this.entradasHoy = entradasHoy;
        this.salidasHoy = salidasHoy;
        this.unidadesEntradasHoy = unidadesEntradasHoy;
        this.unidadesSalidasHoy = unidadesSalidasHoy;
        this.ultimosMovimientos = ultimosMovimientos;
    }

    public long getTotalProductos() {
        return totalProductos;
    }

    public void setTotalProductos(long totalProductos) {
        this.totalProductos = totalProductos;
    }

    public long getTotalCategorias() {
        return totalCategorias;
    }

    public void setTotalCategorias(long totalCategorias) {
        this.totalCategorias = totalCategorias;
    }

    public long getTotalProveedores() {
        return totalProveedores;
    }

    public void setTotalProveedores(long totalProveedores) {
        this.totalProveedores = totalProveedores;
    }

    public long getTotalClientes() {
        return totalClientes;
    }

    public void setTotalClientes(long totalClientes) {
        this.totalClientes = totalClientes;
    }

    public long getTotalMovimientos() {
        return totalMovimientos;
    }

    public void setTotalMovimientos(long totalMovimientos) {
        this.totalMovimientos = totalMovimientos;
    }

    public long getStockBajo() {
        return stockBajo;
    }

    public void setStockBajo(long stockBajo) {
        this.stockBajo = stockBajo;
    }

    public long getAgotados() {
        return agotados;
    }

    public void setAgotados(long agotados) {
        this.agotados = agotados;
    }

    public long getUnidadesEntradasHoy() {
        return unidadesEntradasHoy;
    }

    public void setUnidadesEntradasHoy(long unidadesEntradasHoy) {
        this.unidadesEntradasHoy = unidadesEntradasHoy;
    }

    public long getUnidadesSalidasHoy() {
        return unidadesSalidasHoy;
    }

    public void setUnidadesSalidasHoy(long unidadesSalidasHoy) {
        this.unidadesSalidasHoy = unidadesSalidasHoy;
    }

    public long getEntradasHoy() {
        return entradasHoy;
    }

    public void setEntradasHoy(long entradasHoy) {
        this.entradasHoy = entradasHoy;
    }

    public long getSalidasHoy() {
        return salidasHoy;
    }

    public void setSalidasHoy(long salidasHoy) {
        this.salidasHoy = salidasHoy;
    }

    public List<MovimientoResumenDTO> getUltimosMovimientos() {
        return ultimosMovimientos;
    }

    public void setUltimosMovimientos(List<MovimientoResumenDTO> ultimosMovimientos) {
        this.ultimosMovimientos = ultimosMovimientos;
    }
}
