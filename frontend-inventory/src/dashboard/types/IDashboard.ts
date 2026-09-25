// Con estas interfaces describimos la forma exacta de lo que nos devuelve
// el endpoint GET /api/v1/dashboard. Las separamos igual que en Backend:
// una interfaz para cada movimiento resumido y otra para el resumen
// completo que arma las tarjetas de arriba.

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
  stockBajo: number;
  agotados: number;
  entradasHoy: number;
  salidasHoy: number;
  ultimosMovimientos: IMovimientoResumen[];
}
