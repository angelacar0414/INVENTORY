/**
 * TIPO: Tipo de movimiento
 * ------------------------
 * Un movimiento de inventario solamente puede ser:
 *
 * - ENTRADA: ingresan unidades y el stock aumenta.
 * - SALIDA: se entregan unidades a un cliente y el stock disminuye.
 */
export type TipoMovimiento = "ENTRADA" | "SALIDA";

/**
 * INTERFAZ: Movimiento
 * --------------------
 * Representa un movimiento del historial tal como lo devuelve
 * el Backend.
 *
 * Esta interfaz debe coincidir con el MovementDTO del Backend.
 *
 * Los movimientos son de solo lectura: una vez registrados
 * no se pueden modificar ni eliminar.
 */
export interface IMovimiento {
  // Identificador único del movimiento.
  idMovimiento: number;

  // ENTRADA o SALIDA.
  tipoMovimiento: TipoMovimiento;

  // Unidades que entraron o salieron.
  cantidad: number;

  // Stock del producto justo antes del movimiento.
  stockAnterior: number;

  // Stock del producto justo después del movimiento.
  stockPosterior: number;

  // Fecha y hora del movimiento (formato ISO enviado por el Backend).
  fechaMovimiento: string;

  // Observación opcional.
  observacion: string | null;

  // Producto sobre el que se hizo el movimiento.
  idProducto: number;
  codigoProducto: string;
  nombreProducto: string;

  // Usuario que registró el movimiento.
  idUsuario: number;
  nombreUsuario: string;

  // Cliente que recibió los productos (solo en las salidas).
  idCliente: number | null;
  nombreCliente: string | null;
}

/**
 * INTERFAZ: Formulario de Movimiento
 * ----------------------------------
 * Representa los datos que el usuario envía al registrar una
 * entrada o una salida.
 *
 * El tipo no se envía en el cuerpo: lo define el endpoint
 * (/movimientos/entrada o /movimientos/salida).
 *
 * Tampoco se envían el usuario, la fecha ni los stocks: los calcula
 * el Backend.
 */
export interface IMovimientoFormulario {
  // Producto sobre el que se hace el movimiento.
  idProducto: number;

  // Unidades del movimiento (entero mayor que cero).
  cantidad: number;

  // Observación opcional.
  observacion?: string;

  // Cliente. Obligatorio solo en las salidas.
  idCliente?: number;
}
