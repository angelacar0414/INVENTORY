/**
 * TIPOS DEL MÓDULO DASHBOARD
 * --------------------------
 * Deben coincidir con los DTO del Backend (paquete com.inventory.dashboard.dto).
 */

export interface IMovimientoResumen {
  fecha: string;
  producto: string;
  tipoMovimiento: string;
  cantidad: number;
  usuario: string;
  cliente: string | null;
}

export interface IDashboardResumen {
  totalProductos: number;
  totalCategorias: number;
  totalProveedores: number;
  totalClientes: number;
  totalMovimientos: number;
  stockBajo: number;
  agotados: number;
  entradasHoy: number;
  salidasHoy: number;
  unidadesEntradasHoy: number;
  unidadesSalidasHoy: number;
  ultimosMovimientos: IMovimientoResumen[];
}
