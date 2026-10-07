import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { Sidebar } from "../../components/Sidebar";
import { movimientoService } from "../services/movimientoService";
import type {
  IMovimiento,
  TipoMovimiento,
} from "../types/IMovimiento";

/**
 * Cantidad de movimientos que se muestran por página.
 *
 * El historial crece con cada operación, por eso se divide en
 * páginas para que la tabla siga siendo fácil de leer.
 */
const MOVIMIENTOS_POR_PAGINA = 10;

/**
 * PÁGINA: Historial de Movimientos
 * --------------------------------
 * Esta página permite consultar todas las entradas y salidas
 * registradas en el inventario.
 *
 * Funcionalidades principales:
 *
 * - Consultar el historial (del más reciente al más antiguo).
 * - Buscar por nombre o código del producto.
 * - Filtrar por tipo (entrada o salida).
 * - Ver el stock antes y después de cada movimiento.
 * - Ir a registrar una entrada o una salida.
 *
 * El historial es permanente: los movimientos no se pueden
 * modificar ni eliminar, por eso la tabla no tiene botones
 * de acciones. Los permisos también se controlan en el Backend.
 */
export function ListaMovimientos() {

  // Movimientos que devolvió el Backend con los filtros actuales.
  const [movimientos, setMovimientos] = useState<IMovimiento[]>([]);

  // Texto del buscador (nombre o código del producto).
  const [criterio, setCriterio] = useState("");

  // Filtro por tipo: vacío significa "todos".
  const [tipo, setTipo] = useState<TipoMovimiento | "">("");

  // Página actual de la tabla (empieza en 1).
  const [pagina, setPagina] = useState(1);

  // Controla el estado de carga de la información.
  const [cargando, setCargando] = useState(true);

  // Permite mostrar mensajes de error en pantalla.
  const [error, setError] = useState("");

  /**
   * CARGAR MOVIMIENTOS
   * ------------------
   * Consulta el historial cada vez que cambia el texto de búsqueda
   * o el tipo seleccionado.
   *
   * - Mientras el usuario escribe, esperamos 300 ms antes de
   *   consultar para no hacer una petición por cada letra.
   * - La variable "cancelado" evita que una respuesta lenta y vieja
   *   reemplace a una más reciente.
   */
  useEffect(() => {

    let cancelado = false;

    const temporizador = setTimeout(
      async () => {

        try {
          setCargando(true);
          setError("");

          const resultado =
            await movimientoService.listar(criterio, tipo);

          if (!cancelado) {
            setMovimientos(resultado);

            // Con un filtro nuevo volvemos a la primera página.
            setPagina(1);
          }

        } catch (error: any) {

          if (!cancelado) {
            const mensaje =
              error?.response?.data?.message ||
              "No fue posible cargar los movimientos.";

            setError(mensaje);
          }

        } finally {

          if (!cancelado) {
            setCargando(false);
          }
        }
      },
      criterio.trim() ? 300 : 0
    );

    return () => {
      cancelado = true;
      clearTimeout(temporizador);
    };

  }, [criterio, tipo]);

  /**
   * PAGINACIÓN
   * ----------
   * Calculamos cuántas páginas hay y qué movimientos se muestran
   * en la página actual.
   */
  const totalPaginas = Math.max(
    1,
    Math.ceil(movimientos.length / MOVIMIENTOS_POR_PAGINA)
  );

  const movimientosVisibles = movimientos.slice(
    (pagina - 1) * MOVIMIENTOS_POR_PAGINA,
    pagina * MOVIMIENTOS_POR_PAGINA
  );

  /**
   * FORMATEAR FECHA
   * ---------------
   * El Backend envía la fecha en formato ISO. Aquí la convertimos
   * a un formato fácil de leer: 06/10/2026, 10:15 p. m.
   */
  const formatearFecha = (fecha: string) => {
    return new Date(fecha).toLocaleString("es-CO", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  return (
    <div className="d-flex">

      {/* Menú lateral principal de INVENTORY */}
      <Sidebar />

      {/* Contenido principal */}
      <main className="contenido flex-grow-1">

        {/* Encabezado de la página */}
        <div className="encabezado-pagina">

          <div>
            <h1>Movimientos</h1>

            <p className="text-muted mb-0">
              Historial de entradas y salidas del inventario.
            </p>
          </div>

          {/* Los dos roles pueden registrar movimientos */}
          <div className="d-flex gap-2">

            <Link
              to="/movimientos/entrada"
              className="btn btn-primary"
            >
              + Registrar entrada
            </Link>

            <Link
              to="/movimientos/salida"
              className="btn btn-outline-primary"
            >
              + Registrar salida
            </Link>

          </div>

        </div>

        {/* Tarjeta principal */}
        <div className="tarjeta">

          {/* Barra superior de búsqueda y filtros */}
          <div className="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">

            {/* Buscador */}
            <div className="buscador flex-grow-1 mb-0">

              <input
                type="text"
                className="form-control"
                placeholder="Buscar por código o nombre del producto..."
                value={criterio}
                onChange={(e) => setCriterio(e.target.value)}
              />

            </div>

            {/* Filtro por tipo */}
            <div>

              <select
                className="form-select"
                aria-label="Filtrar por tipo de movimiento"
                value={tipo}
                onChange={(e) =>
                  setTipo(e.target.value as TipoMovimiento | "")
                }
              >
                <option value="">Todos los movimientos</option>
                <option value="ENTRADA">Solo entradas</option>
                <option value="SALIDA">Solo salidas</option>
              </select>

            </div>

          </div>

          {/* Mensaje de error */}
          {error && (
            <div className="alert alert-danger">
              {error}
            </div>
          )}

          {/* Indicador de carga */}
          {cargando ? (

            <div className="text-center py-5">
              <div
                className="spinner-border"
                role="status"
              >
                <span className="visually-hidden">
                  Cargando...
                </span>
              </div>

              <p className="mt-2 text-muted">
                Cargando movimientos...
              </p>
            </div>

          ) : (

            <>

              {/* Tabla de movimientos */}
              <div className="table-responsive">

                <table className="table table-hover align-middle">

                  <thead>

                    <tr>
                      <th>Fecha</th>
                      <th>Tipo</th>
                      <th>Producto</th>
                      <th className="text-end">Cantidad</th>
                      <th className="text-center">Stock</th>
                      <th>Cliente</th>
                      <th>Usuario</th>
                      <th>Observación</th>
                    </tr>

                  </thead>

                  <tbody>

                    {movimientosVisibles.length === 0 ? (

                      <tr>

                        <td
                          colSpan={8}
                          className="text-center py-4 text-muted"
                        >
                          No se encontraron movimientos.
                        </td>

                      </tr>

                    ) : (

                      movimientosVisibles.map((movimiento) => {

                        const esEntrada =
                          movimiento.tipoMovimiento === "ENTRADA";

                        return (

                          <tr key={movimiento.idMovimiento}>

                            {/* Fecha y hora */}
                            <td className="text-nowrap">
                              {formatearFecha(
                                movimiento.fechaMovimiento
                              )}
                            </td>

                            {/* Tipo de movimiento */}
                            <td>
                              <span
                                className={
                                  esEntrada
                                    ? "badge bg-success"
                                    : "badge bg-danger"
                                }
                              >
                                {movimiento.tipoMovimiento}
                              </span>
                            </td>

                            {/* Producto */}
                            <td>
                              <div>
                                {movimiento.nombreProducto}
                              </div>

                              <small className="text-muted">
                                {movimiento.codigoProducto}
                              </small>
                            </td>

                            {/* Cantidad con signo */}
                            <td
                              className={
                                "text-end fw-bold " +
                                (esEntrada
                                  ? "text-success"
                                  : "text-danger")
                              }
                            >
                              {esEntrada ? "+" : "−"}
                              {movimiento.cantidad}
                            </td>

                            {/* Stock antes → después */}
                            <td className="text-center text-nowrap">
                              {movimiento.stockAnterior}
                              {" → "}
                              <strong>
                                {movimiento.stockPosterior}
                              </strong>
                            </td>

                            {/* Cliente (solo existe en las salidas) */}
                            <td>
                              {movimiento.nombreCliente || "-"}
                            </td>

                            {/* Usuario que registró el movimiento */}
                            <td>
                              {movimiento.nombreUsuario}
                            </td>

                            {/* Observación opcional */}
                            <td>
                              {movimiento.observacion || "-"}
                            </td>

                          </tr>

                        );
                      })

                    )}

                  </tbody>

                </table>

              </div>

              {/* Paginación y recordatorio del historial */}
              <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mt-3">

                <small className="text-muted">
                  {movimientos.length} movimiento(s). El historial
                  es permanente: no se puede modificar ni eliminar.
                </small>

                <div className="d-flex align-items-center gap-2">

                  <button
                    type="button"
                    className="btn btn-sm btn-outline-secondary"
                    onClick={() => setPagina(pagina - 1)}
                    disabled={pagina <= 1}
                  >
                    Anterior
                  </button>

                  <span className="text-muted">
                    Página {pagina} de {totalPaginas}
                  </span>

                  <button
                    type="button"
                    className="btn btn-sm btn-outline-secondary"
                    onClick={() => setPagina(pagina + 1)}
                    disabled={pagina >= totalPaginas}
                  >
                    Siguiente
                  </button>

                </div>

              </div>

            </>

          )}

        </div>

      </main>

    </div>
  );
}
