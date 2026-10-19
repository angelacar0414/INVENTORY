import type { TipoMovimiento } from "../../movements/types/IMovimiento";

/**
 * TIPOS DEL MÓDULO REPORTES
 * -------------------------
 * Estas interfaces deben coincidir con los DTO del Backend
 * (paquete com.inventory.report.dto).
 */

/* ============================================================
   FILTROS (lo que el usuario elige en pantalla)
   ============================================================
   Todos los filtros se guardan como texto porque vienen de
   campos <select> o <input>. Un texto vacío significa
   "no filtrar por este criterio".
   ============================================================ */

export type EstadoProducto = "DISPONIBLE" | "AGOTADO" | "INACTIVO";

export interface IFiltrosProductos {
  estado: EstadoProducto | "";
  idProveedor: string;
  idCategoria: string;
  stockBajo: boolean;
}

export interface IFiltrosMovimientos {
  // Fechas en formato AAAA-MM-DD (el que entrega <input type="date">).
  desde: string;
  hasta: string;
  tipo: TipoMovimiento | "";
  idProducto: string;
  idCliente: string;
}

/* ============================================================
   REPORTE DE PRODUCTOS
   ============================================================ */

export interface IFilaReporteProducto {
  idProducto: number;
  codigo: string;
  nombre: string;
  categoria: string;
  proveedor: string;
  stockActual: number;
  stockMinimo: number;
  precioCompra: number;
  precioVenta: number;
  estado: EstadoProducto;
  // Producto activo con stock mayor que 0 y menor o igual al mínimo.
  stockBajo: boolean;
}

export interface IResumenProductos {
  totalProductos: number;
  disponibles: number;
  agotados: number;
  inactivos: number;
  stockBajo: number;
  unidadesEnStock: number;
  valorInventario: number;
}

export interface IReporteProductos {
  resumen: IResumenProductos;
  items: IFilaReporteProducto[];
  // Descripción legible de los filtros aplicados.
  filtros: string[];
}

/* ============================================================
   REPORTE DE MOVIMIENTOS
   ============================================================ */

export interface IFilaReporteMovimiento {
  idMovimiento: number;
  fecha: string;
  tipo: TipoMovimiento;
  codigoProducto: string;
  nombreProducto: string;
  cantidad: number;
  stockAnterior: number;
  stockPosterior: number;
  cliente: string | null;
  usuario: string;
  observacion: string | null;
}

export interface IResumenMovimientos {
  totalMovimientos: number;
  totalEntradas: number;
  totalSalidas: number;
  unidadesEntradas: number;
  unidadesSalidas: number;
  balanceNeto: number;
}

export interface IReporteMovimientos {
  resumen: IResumenMovimientos;
  items: IFilaReporteMovimiento[];
  filtros: string[];
}
