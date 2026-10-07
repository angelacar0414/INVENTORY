import api from "../../services/api";
import type {
  IProducto,
  IProductoFormulario,
} from "../types/IProducto";

/**
 * SERVICE DE PRODUCTOS
 * --------------------
 * Esta capa es la encargada de realizar las peticiones
 * HTTP al Backend relacionadas con el módulo Productos.
 *
 * Las páginas del módulo NO deben llamar directamente
 * a axios. En su lugar, utilizan este servicio.
 *
 * De esta manera mantenemos separadas las responsabilidades:
 *
 * Página → Servicio → API Backend
 *
 * La instancia "api" ya contiene:
 * - La URL base del Backend.
 * - withCredentials: true
 *
 * Esto permite enviar la cookie de sesión HTTP utilizada
 * por Spring Security.
 */

/**
 * Estructura general de respuesta utilizada por el Backend.
 *
 * El backend responde normalmente con:
 *
 * {
 *   success: true,
 *   message: "...",
 *   data: ...
 * }
 *
 * El tipo genérico <T> permite reutilizar esta estructura
 * para listas, objetos individuales, etc.
 */
interface RespuestaApi<T> {
  success: boolean;
  message: string;
  data: T;
}

/**
 * Objeto que contiene todas las operaciones disponibles
 * para trabajar con productos.
 */
export const productoService = {

  /**
   * CONSULTAR PRODUCTOS ACTIVOS
   * ----------------------------
   * Obtiene únicamente los productos que se encuentran
   * activos en el sistema.
   *
   * Endpoint:
   * GET /api/v1/productos
   */
  async listar(): Promise<IProducto[]> {
    const respuesta = await api.get<RespuestaApi<IProducto[]>>(
      "/productos"
    );

    return respuesta.data.data;
  },

  /**
   * CONSULTAR TODOS LOS PRODUCTOS
   * -----------------------------
   * Obtiene los productos activos e inactivos.
   *
   * Esta opción será utilizada cuando el usuario active
   * la opción "Ver todos" en la interfaz.
   *
   * Endpoint:
   * GET /api/v1/productos/todas
   */
  async listarTodas(): Promise<IProducto[]> {
    const respuesta = await api.get<RespuestaApi<IProducto[]>>(
      "/productos/todas"
    );

    return respuesta.data.data;
  },

  /**
   * BUSCAR PRODUCTOS
   * ----------------
   * Permite buscar productos utilizando un criterio.
   *
   * El Backend realiza la búsqueda por:
   * - Código
   * - Nombre
   * - Categoría
   *
   * Ejemplo:
   * /productos/buscar?criterio=arroz
   *
   * Endpoint:
   * GET /api/v1/productos/buscar?criterio=...
   */
  async buscar(criterio: string): Promise<IProducto[]> {
    const respuesta = await api.get<RespuestaApi<IProducto[]>>(
      `/productos/buscar?criterio=${encodeURIComponent(criterio)}`
    );

    return respuesta.data.data;
  },

  /**
   * BUSCAR PRODUCTO POR ID
   * ----------------------
   * Obtiene la información completa de un producto
   * utilizando su identificador.
   *
   * Endpoint:
   * GET /api/v1/productos/{id}
   */
  async buscarPorId(id: number): Promise<IProducto> {
    const respuesta = await api.get<RespuestaApi<IProducto>>(
      `/productos/${id}`
    );

    return respuesta.data.data;
  },

  /**
   * CREAR PRODUCTO
   * --------------
   * Envía al Backend los datos necesarios para registrar
   * un nuevo producto.
   *
   * El Backend se encarga de:
   * - Generar el código del producto.
   * - Validar categoría.
   * - Validar proveedor.
   * - Establecer el estado.
   * - Establecer el producto como activo.
   *
   * Endpoint:
   * POST /api/v1/productos
   */
  async crear(
    producto: IProductoFormulario
  ): Promise<IProducto> {
    const respuesta = await api.post<RespuestaApi<IProducto>>(
      "/productos",
      producto
    );

    return respuesta.data.data;
  },

  /**
   * ACTUALIZAR PRODUCTO
   * -------------------
   * Modifica la información permitida de un producto
   * existente.
   *
   * El Backend mantiene bajo su control:
   * - Código.
   * - Stock actual.
   * - Estado.
   * - Activo.
   *
   * Endpoint:
   * PUT /api/v1/productos/{id}
   */
  async actualizar(
    id: number,
    producto: IProductoFormulario
  ): Promise<IProducto> {
    const respuesta = await api.put<RespuestaApi<IProducto>>(
      `/productos/${id}`,
      producto
    );

    return respuesta.data.data;
  },

  /**
   * DESACTIVAR PRODUCTO
   * -------------------
   * Realiza una desactivación lógica del producto.
   *
   * El producto NO se elimina físicamente de la base
   * de datos. Simplemente pasa a estado INACTIVO.
   *
   * Endpoint:
   * DELETE /api/v1/productos/{id}
   */
  async desactivar(id: number): Promise<void> {
    await api.delete(`/productos/${id}`);
  },

  /**
   * REACTIVAR PRODUCTO
   * ------------------
   * Permite volver a activar un producto previamente
   * desactivado.
   *
   * El Backend vuelve a calcular su estado dependiendo
   * del stock actual.
   *
   * Endpoint:
   * PUT /api/v1/productos/{id}/reactivar
   */
  async reactivar(id: number): Promise<void> {
    await api.put(`/productos/${id}/reactivar`);
  },
};