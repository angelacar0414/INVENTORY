import { useMemo, useState } from "react";

import type { ICliente } from "../../clients/types/ICliente";
import type { IProducto } from "../../products/types/IProducto";
import {
  mensajeDeError,
  reporteService,
  type FormatoDescarga,
} from "../services/reporteService";
import type {
  IFiltrosMovimientos,
  IReporteMovimientos,
} from "../types/IReporte";
import {
  BotonesDescarga,
  Paginacion,
  TarjetaIndicador,
  aTextoFecha,
  formatoEntero,
  formatoFechaHora,
} from "./ComponentesReporte";

const FILAS_POR_PAGINA = 10;

/** Por defecto el reporte de movimientos muestra los últimos 30 días. */
function filtrosIniciales(): IFiltrosMovimientos {
  const hoy = new Date();
  const inicio = new Date();
  inicio.setDate(hoy.getDate() - 29);

  return {
    desde: aTextoFecha(inicio),
    hasta: aTextoFecha(hoy),
    tipo: "",
    idProducto: "",
    idCliente: "",
  };
}

interface Props {
  productos: IProducto[];
  clientes: ICliente[];
}

/**
 * PESTAÑA: Reporte de movimientos
 * -------------------------------
 * Filtra las entradas y salidas por rango de fechas, tipo,
 * producto y cliente, y permite descargarlas en PDF o Excel.
 */
export function ReporteMovimientosTab({ productos, clientes }: Props) {

  const [filtros, setFiltros] = useState<IFiltrosMovimientos>(filtrosIniciales);
  const [filtrosAplicados, setFiltrosAplicados] =
    useState<IFiltrosMovimientos | null>(null);

  const [reporte, setReporte] = useState<IReporteMovimientos | null>(null);
  const [cargando, setCargando] = useState(false);
  const [descargando, setDescargando] = useState<FormatoDescarga | null>(null);
  const [error, setError] = useState("");

  const [busqueda, setBusqueda] = useState("");
  const [pagina, setPagina] = useState(1);

  const hayCambiosPendientes =
    reporte !== null &&
    filtrosAplicados !== null &&
    JSON.stringify(filtros) !== JSON.stringify(filtrosAplicados);

  /** Atajos para elegir el rango de fechas con un clic. */
  const aplicarRango = (dias: number | "mes" | "todo") => {
    const hoy = new Date();
    let desde = "";
    let hasta = aTextoFecha(hoy);

    if (dias === "todo") {
      hasta = "";
    } else if (dias === "mes") {
      desde = aTextoFecha(new Date(hoy.getFullYear(), hoy.getMonth(), 1));
    } else {
      const inicio = new Date();
      inicio.setDate(hoy.getDate() - (dias - 1));
      desde = aTextoFecha(inicio);
    }

    setFiltros({ ...filtros, desde, hasta });
  };

  const generar = async () => {
    // Validación básica en el navegador (el Backend la repite).
    if (filtros.desde && filtros.hasta && filtros.desde > filtros.hasta) {
      setError("La fecha inicial no puede ser posterior a la fecha final.");
      return;
    }

    setCargando(true);
    setError("");

    try {
      const datos = await reporteService.movimientos(filtros);
      setReporte(datos);
      setFiltrosAplicados({ ...filtros });
      setBusqueda("");
      setPagina(1);
    } catch (error: any) {
      setError(await mensajeDeError(error, "No se pudo generar el reporte."));
    } finally {
      setCargando(false);
    }
  };

  const limpiar = () => {
    setFiltros(filtrosIniciales());
    setFiltrosAplicados(null);
    setReporte(null);
    setError("");
    setBusqueda("");
    setPagina(1);
  };

  const descargar = async (formato: FormatoDescarga) => {
    if (!filtrosAplicados) return;

    setDescargando(formato);
    setError("");

    try {
      await reporteService.descargarMovimientos(filtrosAplicados, formato);
    } catch (error: any) {
      setError(await mensajeDeError(error, "No se pudo descargar el reporte."));
    } finally {
      setDescargando(null);
    }
  };

  const filasFiltradas = useMemo(() => {
    if (!reporte) return [];
    const texto = busqueda.trim().toLowerCase();
    if (!texto) return reporte.items;

    return reporte.items.filter(
      (m) =>
        m.codigoProducto.toLowerCase().includes(texto) ||
        m.nombreProducto.toLowerCase().includes(texto) ||
        (m.cliente ?? "").toLowerCase().includes(texto)
    );
  }, [reporte, busqueda]);

  const totalPaginas = Math.max(1, Math.ceil(filasFiltradas.length / FILAS_POR_PAGINA));
  const paginaActual = Math.min(pagina, totalPaginas);
  const filasPagina = filasFiltradas.slice(
    (paginaActual - 1) * FILAS_POR_PAGINA,
    paginaActual * FILAS_POR_PAGINA
  );

  const resumen = reporte?.resumen;
  const esEntrada = filtros.tipo === "ENTRADA";

  return (
    <>
      {/* ---------------- FILTROS ---------------- */}
      <div className="tarjeta mb-3">
        <div className="row g-3 align-items-end">

          <div className="col-md-3">
            <label className="form-label" htmlFor="rm-desde">Desde</label>
            <input
              id="rm-desde"
              type="date"
              className="form-control"
              value={filtros.desde}
              max={filtros.hasta || undefined}
              onChange={(e) => setFiltros({ ...filtros, desde: e.target.value })}
            />
          </div>

          <div className="col-md-3">
            <label className="form-label" htmlFor="rm-hasta">Hasta</label>
            <input
              id="rm-hasta"
              type="date"
              className="form-control"
              value={filtros.hasta}
              min={filtros.desde || undefined}
              onChange={(e) => setFiltros({ ...filtros, hasta: e.target.value })}
            />
          </div>

          <div className="col-md-6">
            <div className="d-flex flex-wrap gap-2">
              <button type="button" className="btn btn-sm btn-outline-secondary" onClick={() => aplicarRango(1)}>Hoy</button>
              <button type="button" className="btn btn-sm btn-outline-secondary" onClick={() => aplicarRango(7)}>7 días</button>
              <button type="button" className="btn btn-sm btn-outline-secondary" onClick={() => aplicarRango(30)}>30 días</button>
              <button type="button" className="btn btn-sm btn-outline-secondary" onClick={() => aplicarRango("mes")}>Este mes</button>
              <button type="button" className="btn btn-sm btn-outline-secondary" onClick={() => aplicarRango("todo")}>Todo</button>
            </div>
          </div>

          <div className="col-md-4">
            <label className="form-label" htmlFor="rm-tipo">Tipo</label>
            <select
              id="rm-tipo"
              className="form-select"
              value={filtros.tipo}
              onChange={(e) => {
                const tipo = e.target.value as IFiltrosMovimientos["tipo"];
                // El cliente solo aplica a las salidas.
                setFiltros({
                  ...filtros,
                  tipo,
                  idCliente: tipo === "ENTRADA" ? "" : filtros.idCliente,
                });
              }}
            >
              <option value="">Entradas y salidas</option>
              <option value="ENTRADA">Entradas</option>
              <option value="SALIDA">Salidas</option>
            </select>
          </div>

          <div className="col-md-4">
            <label className="form-label" htmlFor="rm-producto">Producto</label>
            <select
              id="rm-producto"
              className="form-select"
              value={filtros.idProducto}
              onChange={(e) => setFiltros({ ...filtros, idProducto: e.target.value })}
            >
              <option value="">Todos</option>
              {productos.map((p) => (
                <option key={p.idProducto} value={p.idProducto}>
                  {p.codigo} - {p.nombre}
                </option>
              ))}
            </select>
          </div>

          <div className="col-md-4">
            <label className="form-label" htmlFor="rm-cliente">Cliente</label>
            <select
              id="rm-cliente"
              className="form-select"
              value={filtros.idCliente}
              disabled={esEntrada}
              onChange={(e) => setFiltros({ ...filtros, idCliente: e.target.value })}
            >
              <option value="">{esEntrada ? "Solo aplica a salidas" : "Todos"}</option>
              {clientes.map((c) => (
                <option key={c.idCliente} value={c.idCliente}>{c.nombre}</option>
              ))}
            </select>
          </div>

        </div>

        <div className="d-flex flex-wrap align-items-center gap-2 mt-3">
          <button
            type="button"
            className="btn btn-primary"
            onClick={generar}
            disabled={cargando}
          >
            {cargando ? "Generando..." : "Generar reporte"}
          </button>

          <button
            type="button"
            className="btn btn-outline-secondary"
            onClick={limpiar}
            disabled={cargando}
          >
            Limpiar filtros
          </button>

          {hayCambiosPendientes && (
            <small className="text-warning">
              Cambiaste los filtros: vuelve a generar el reporte para actualizar los resultados.
            </small>
          )}
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {!reporte && !error && (
        <div className="tarjeta text-center text-muted">
          Elige el rango de fechas y los filtros, y pulsa «Generar reporte».
        </div>
      )}

      {reporte && resumen && (
        <>
          {/* ---------------- INDICADORES ---------------- */}
          <div className="tarjetas-kpi mb-3">
            <TarjetaIndicador icono="intercambio" color="azul" titulo="Movimientos" valor={formatoEntero(resumen.totalMovimientos)} />
            <TarjetaIndicador icono="entrada" color="azul" titulo="Entradas (unidades)" valor={formatoEntero(resumen.unidadesEntradas)} />
            <TarjetaIndicador icono="salida" color="naranja" titulo="Salidas (unidades)" valor={formatoEntero(resumen.unidadesSalidas)} />
            <TarjetaIndicador
              icono="balanza" color="gris"
              titulo="Balance neto"
              valor={(resumen.balanceNeto > 0 ? "+" : "") + formatoEntero(resumen.balanceNeto)}
              claseValor={resumen.balanceNeto < 0 ? "text-danger" : ""}
            />
          </div>

          {/* ---------------- DESCARGAS ---------------- */}
          <div className="tarjeta mb-3">
            <BotonesDescarga
              deshabilitado={reporte.items.length === 0 || hayCambiosPendientes}
              registros={reporte.items.length}
              descargando={descargando}
              onDescargar={descargar}
            />
            {hayCambiosPendientes && (
              <small className="text-muted d-block mt-1">
                Las descargas usan los filtros del último reporte generado.
              </small>
            )}
          </div>

          {/* ---------------- TABLA ---------------- */}
          <div className="tarjeta">
            <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
              <div>
                <strong>Detalle</strong>
                {reporte.filtros.length > 0 && (
                  <small className="text-muted d-block">
                    Filtros: {reporte.filtros.join(" · ")}
                  </small>
                )}
              </div>

              <input
                type="text"
                className="form-control buscador"
                placeholder="Buscar por producto o cliente..."
                value={busqueda}
                onChange={(e) => {
                  setBusqueda(e.target.value);
                  setPagina(1);
                }}
                style={{ maxWidth: 320 }}
              />
            </div>

            {filasFiltradas.length === 0 ? (
              <p className="text-center text-muted mb-0">
                No hay movimientos que coincidan con los filtros.
              </p>
            ) : (
              <>
                <div className="table-responsive">
                  <table className="table table-hover align-middle">
                    <thead>
                      <tr>
                        <th>Fecha</th>
                        <th>Tipo</th>
                        <th>Producto</th>
                        <th className="text-end">Cantidad</th>
                        <th className="text-end">Stock antes</th>
                        <th className="text-end">Stock después</th>
                        <th>Cliente</th>
                        <th>Usuario</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filasPagina.map((m) => (
                        <tr key={m.idMovimiento}>
                          <td>{formatoFechaHora(m.fecha)}</td>
                          <td>
                            <span className={"badge " + (m.tipo === "ENTRADA" ? "bg-primary" : "bg-warning text-dark")}>
                              {m.tipo}
                            </span>
                          </td>
                          <td>{m.codigoProducto} - {m.nombreProducto}</td>
                          <td className="text-end">
                            {m.tipo === "ENTRADA" ? "+" : "-"}{formatoEntero(m.cantidad)}
                          </td>
                          <td className="text-end">{formatoEntero(m.stockAnterior)}</td>
                          <td className="text-end">{formatoEntero(m.stockPosterior)}</td>
                          <td>{m.cliente ?? "—"}</td>
                          <td>{m.usuario}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                <div className="d-flex flex-wrap justify-content-between align-items-center gap-2">
                  <small className="text-muted">
                    {formatoEntero(filasFiltradas.length)} movimiento(s)
                  </small>
                  <Paginacion
                    pagina={paginaActual}
                    totalPaginas={totalPaginas}
                    onCambiar={setPagina}
                  />
                </div>
              </>
            )}
          </div>
        </>
      )}
    </>
  );
}
