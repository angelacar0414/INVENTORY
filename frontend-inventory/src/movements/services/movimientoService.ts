import api from "../../services/api";
import type {
  IMovimiento,
  IMovimientoFormulario,
  TipoMovimiento,
} from "../types/IMovimiento";

/**
 * SERVICE DE MOVIMIENTOS
 * ----------------------
 * Esta capa realiza las peticiones HTTP al Backend relacionadas
 * con el módulo Movimientos.
 *
 * Las páginas del módulo NO llaman directamente a axios:
 *
 * Página → Servicio → API Backend
 *
 * La instancia "api" ya contiene la URL base del Backend y
 * withCredentials: true, que permite enviar la cookie de sesión
 * utilizada por Spring Security.
 *
 * Nota: el historial no se puede modificar ni eliminar, por eso
 * este servicio no tiene operaciones de actualizar ni desactivar.
 */

/**
 * Estructura general de respuesta utilizada por el Backend:
 *
 * {
 *   success: true,
 *   message: "...",
 *   data: ...
 * }
 */
interface RespuestaApi<T> {
  success: boolean;
  message: string;
  data: T;
}

export const movimientoService = {

  /**
   * CONSULTAR HISTORIAL
   * -------------------
   * Obtiene los movimientos, del más reciente al más antiguo.
   *
   * Ambos filtros son opcionales y se pueden combinar:
   *
   * - criterio: texto que debe coincidir con el nombre o el
   *   código del producto.
   * - tipo: ENTRADA o SALIDA.
   *
   * Endpoint:
   * GET /api/v1/movimientos?criterio=...&tipo=...
   */
  async listar(
    criterio?: string,
    tipo?: TipoMovimiento | ""
  ): Promise<IMovimiento[]> {

    // Solo enviamos los filtros que realmente tienen valor.
    const params: Record<string, string> = {};

    if (criterio && criterio.trim()) {
      params.criterio = criterio.trim();
    }

    if (tipo) {
      params.tipo = tipo;
    }

    const respuesta = await api.get<RespuestaApi<IMovimiento[]>>(
      "/movimientos",
      { params }
    );

    return respuesta.data.data;
  },

  /**
   * BUSCAR MOVIMIENTO POR ID
   * ------------------------
   * Endpoint:
   * GET /api/v1/movimientos/{id}
   */
  async buscarPorId(id: number): Promise<IMovimiento> {
    const respuesta = await api.get<RespuestaApi<IMovimiento>>(
      `/movimientos/${id}`
    );

    return respuesta.data.data;
  },

  /**
   * REGISTRAR ENTRADA
   * -----------------
   * Registra el ingreso de unidades al inventario.
   *
   * El Backend se encarga de:
   * - Aumentar el stock del producto.
   * - Guardar el stock anterior y posterior.
   * - Tomar el usuario de la sesión iniciada.
   * - Recalcular el estado del producto.
   *
   * Endpoint:
   * POST /api/v1/movimientos/entrada
   */
  async registrarEntrada(
    movimiento: IMovimientoFormulario
  ): Promise<IMovimiento> {
    const respuesta = await api.post<RespuestaApi<IMovimiento>>(
      "/movimientos/entrada",
      movimiento
    );

    return respuesta.data.data;
  },

  /**
   * REGISTRAR SALIDA
   * ----------------
   * Registra la entrega de unidades a un cliente.
   *
   * El cliente es obligatorio y la cantidad no puede superar
   * el stock disponible; el Backend rechaza la operación en
   * esos casos.
   *
   * Endpoint:
   * POST /api/v1/movimientos/salida
   */
  async registrarSalida(
    movimiento: IMovimientoFormulario
  ): Promise<IMovimiento> {
    const respuesta = await api.post<RespuestaApi<IMovimiento>>(
      "/movimientos/salida",
      movimiento
    );

    return respuesta.data.data;
  },
};
