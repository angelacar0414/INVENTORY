import api from "../../services/api";
import type {
  IFiltrosMovimientos,
  IFiltrosProductos,
  IReporteMovimientos,
  IReporteProductos,
} from "../types/IReporte";

/**
 * SERVICE DE REPORTES
 * -------------------
 * Esta capa realiza las peticiones HTTP al Backend relacionadas con
 * el módulo Reportes.
 *
 * Página → Servicio → API Backend
 *
 * Los reportes son solo de consulta (GET). Cada reporte tiene tres
 * formas de obtenerse, todas con los mismos filtros:
 *
 *   /reportes/productos          datos para mostrar en pantalla
 *   /reportes/productos/pdf      PDF con gráficas
 *   /reportes/productos/excel    archivo Excel
 *
 * (y lo mismo con /reportes/movimientos)
 */

interface RespuestaApi<T> {
  success: boolean;
  message: string;
  data: T;
}

export type FormatoDescarga = "pdf" | "excel";

/**
 * Convierte los filtros del formulario en parámetros de la URL,
 * enviando solo los que realmente tienen valor.
 */
function parametrosProductos(filtros: IFiltrosProductos) {
  const params: Record<string, string> = {};

  if (filtros.estado) params.estado = filtros.estado;
  if (filtros.idProveedor) params.idProveedor = filtros.idProveedor;
  if (filtros.idCategoria) params.idCategoria = filtros.idCategoria;
  if (filtros.stockBajo) params.stockBajo = "true";

  return params;
}

function parametrosMovimientos(filtros: IFiltrosMovimientos) {
  const params: Record<string, string> = {};

  if (filtros.desde) params.desde = filtros.desde;
  if (filtros.hasta) params.hasta = filtros.hasta;
  if (filtros.tipo) params.tipo = filtros.tipo;
  if (filtros.idProducto) params.idProducto = filtros.idProducto;
  if (filtros.idCliente) params.idCliente = filtros.idCliente;

  return params;
}

/**
 * DESCARGAR UN ARCHIVO
 * --------------------
 * Pide el archivo al Backend como "blob" (datos binarios) y le
 * indica al navegador que lo guarde, igual que cuando se pulsa
 * un enlace de descarga.
 *
 * El nombre del archivo se arma aquí (reporte-productos-20261007-2010.pdf)
 * porque el navegador no deja leer la cabecera Content-Disposition
 * en peticiones entre orígenes distintos (5173 → 8080).
 */
async function descargar(
  ruta: "productos" | "movimientos",
  formato: FormatoDescarga,
  params: Record<string, string>
): Promise<void> {

  const respuesta = await api.get<Blob>(
    `/reportes/${ruta}/${formato}`,
    { params, responseType: "blob" }
  );

  const ahora = new Date();
  const dos = (n: number) => String(n).padStart(2, "0");
  const marca =
    `${ahora.getFullYear()}${dos(ahora.getMonth() + 1)}${dos(ahora.getDate())}` +
    `-${dos(ahora.getHours())}${dos(ahora.getMinutes())}`;

  const extension = formato === "pdf" ? "pdf" : "xlsx";
  const nombre = `reporte-${ruta}-${marca}.${extension}`;

  const url = URL.createObjectURL(respuesta.data);
  const enlace = document.createElement("a");
  enlace.href = url;
  enlace.download = nombre;
  document.body.appendChild(enlace);
  enlace.click();
  enlace.remove();
  URL.revokeObjectURL(url);
}

/**
 * MENSAJE DE ERROR
 * ----------------
 * Obtiene un texto claro para mostrar al usuario a partir del error
 * de axios. Cuando se descarga un archivo, el cuerpo del error llega
 * también como "blob", así que primero hay que leerlo como texto.
 */
export async function mensajeDeError(
  error: any,
  mensajePorDefecto: string
): Promise<string> {

  if (error?.response?.status === 403) {
    return "Solo el administrador puede consultar los reportes.";
  }

  let datos = error?.response?.data;

  if (datos instanceof Blob) {
    try {
      datos = JSON.parse(await datos.text());
    } catch {
      datos = null;
    }
  }

  return datos?.message || mensajePorDefecto;
}

export const reporteService = {

  /**
   * Reporte de productos para mostrar en pantalla.
   * Endpoint: GET /api/v1/reportes/productos
   */
  async productos(filtros: IFiltrosProductos): Promise<IReporteProductos> {
    const respuesta = await api.get<RespuestaApi<IReporteProductos>>(
      "/reportes/productos",
      { params: parametrosProductos(filtros) }
    );

    return respuesta.data.data;
  },

  /**
   * Reporte de movimientos para mostrar en pantalla.
   * Endpoint: GET /api/v1/reportes/movimientos
   */
  async movimientos(filtros: IFiltrosMovimientos): Promise<IReporteMovimientos> {
    const respuesta = await api.get<RespuestaApi<IReporteMovimientos>>(
      "/reportes/movimientos",
      { params: parametrosMovimientos(filtros) }
    );

    return respuesta.data.data;
  },

  /**
   * Descarga el reporte de productos en PDF o Excel.
   * Endpoint: GET /api/v1/reportes/productos/{pdf|excel}
   */
  descargarProductos(
    filtros: IFiltrosProductos,
    formato: FormatoDescarga
  ): Promise<void> {
    return descargar("productos", formato, parametrosProductos(filtros));
  },

  /**
   * Descarga el reporte de movimientos en PDF o Excel.
   * Endpoint: GET /api/v1/reportes/movimientos/{pdf|excel}
   */
  descargarMovimientos(
    filtros: IFiltrosMovimientos,
    formato: FormatoDescarga
  ): Promise<void> {
    return descargar("movimientos", formato, parametrosMovimientos(filtros));
  },
};
