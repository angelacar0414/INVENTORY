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
    private long stockBajo;
    private long agotados;
    private long entradasHoy;
    private long salidasHoy;
    private List<MovimientoResumenDTO> ultimosMovimientos;

    public DashboardDTO() {
    }

    public DashboardDTO(long totalProductos, long stockBajo, long agotados,
                         long entradasHoy, long salidasHoy,
                         List<MovimientoResumenDTO> ultimosMovimientos) {
        this.totalProductos = totalProductos;
        this.stockBajo = stockBajo;
        this.agotados = agotados;
        this.entradasHoy = entradasHoy;
        this.salidasHoy = salidasHoy;
        this.ultimosMovimientos = ultimosMovimientos;
    }

    public long getTotalProductos() {
        return totalProductos;
    }

    public void setTotalProductos(long totalProductos) {
        this.totalProductos = totalProductos;
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
