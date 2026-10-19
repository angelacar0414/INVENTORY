import { useMemo, useState } from "react";

import type { ICategoria } from "../../categories/types/ICategoria";
import type { IProveedor } from "../../suppliers/types/IProveedor";
import {
  mensajeDeError,
  reporteService,
  type FormatoDescarga,
} from "../services/reporteService";
import type {
  IFiltrosProductos,
  IReporteProductos,
} from "../types/IReporte";
import {
  BotonesDescarga,
  Paginacion,
  TarjetaIndicador,
  formatoDinero,
  formatoEntero,
} from "./ComponentesReporte";

const FILAS_POR_PAGINA = 10;

const FILTROS_INICIALES: IFiltrosProductos = {
  estado: "",
  idProveedor: "",
  idCategoria: "",
  stockBajo: false,
};

interface Props {
  proveedores: IProveedor[];
  categorias: ICategoria[];
}

/**
 * PESTAÑA: Reporte de productos
 * -----------------------------
 * Permite filtrar el inventario por estado, proveedor, categoría
 * y stock bajo, ver el resultado en pantalla y descargarlo en
 * PDF (con gráficas) o Excel.
 */
export function ReporteProductosTab({ proveedores, categorias }: Props) {

  // Filtros que el usuario está editando en el formulario.
  const [filtros, setFiltros] = useState<IFiltrosProductos>(FILTROS_INICIALES);

  // Filtros con los que se generó el reporte que se ve en pantalla.
  // Las descargas usan ESTOS, para que el archivo sea idéntico a lo que se ve.
  const [filtrosAplicados, setFiltrosAplicados] =
    useState<IFiltrosProductos | null>(null);

  const [reporte, setReporte] = useState<IReporteProductos | null>(null);
  const [cargando, setCargando] = useState(false);
  const [descargando, setDescargando] = useState<FormatoDescarga | null>(null);
  const [error, setError] = useState("");

  const [busqueda, setBusqueda] = useState("");
  const [pagina, setPagina] = useState(1);

  // Hay cambios en el formulario que todavía no se han aplicado.
  const hayCambiosPendientes =
    reporte !== null &&
    filtrosAplicados !== null &&
    JSON.stringify(filtros) !== JSON.stringify(filtrosAplicados);

  const generar = async () => {
    setCargando(true);
    setError("");

    try {
      const datos = await reporteService.productos(filtros);
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
    setFiltros(FILTROS_INICIALES);
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
      await reporteService.descargarProductos(filtrosAplicados, formato);
    } catch (error: any) {
      setError(await mensajeDeError(error, "No se pudo descargar el reporte."));
    } finally {
      setDescargando(null);
    }
  };

  // Búsqueda sobre las filas ya cargadas (no vuelve a llamar al Backend).
  const filasFiltradas = useMemo(() => {
    if (!reporte) return [];
    const texto = busqueda.trim().toLowerCase();
    if (!texto) return reporte.items;

    return reporte.items.filter(
      (p) =>
        p.codigo.toLowerCase().includes(texto) ||
        p.nombre.toLowerCase().includes(texto)
    );
  }, [reporte, busqueda]);

  const totalPaginas = Math.max(1, Math.ceil(filasFiltradas.length / FILAS_POR_PAGINA));
  const paginaActual = Math.min(pagina, totalPaginas);
  const filasPagina = filasFiltradas.slice(
    (paginaActual - 1) * FILAS_POR_PAGINA,
    paginaActual * FILAS_POR_PAGINA
  );

  const resumen = reporte?.resumen;

  return (
    <>
      {/* ---------------- FILTROS ---------------- */}
      <div className="tarjeta mb-3">
        <div className="row g-3 align-items-end">

          <div className="col-md-3">
            <label className="form-label" htmlFor="rp-estado">Estado</label>
            <select
              id="rp-estado"
              className="form-select"
              value={filtros.estado}
              onChange={(e) =>
                setFiltros({ ...filtros, estado: e.target.value as IFiltrosProductos["estado"] })
              }
            >
              <option value="">Todos</option>
              <option value="DISPONIBLE">Disponible</option>
              <option value="AGOTADO">Agotado</option>
              <option value="INACTIVO">Inactivo</option>
            </select>
          </div>

          <div className="col-md-3">
            <label className="form-label" htmlFor="rp-proveedor">Proveedor</label>
            <select
              id="rp-proveedor"
              className="form-select"
              value={filtros.idProveedor}
              onChange={(e) => setFiltros({ ...filtros, idProveedor: e.target.value })}
            >
              <option value="">Todos</option>
              {proveedores.map((p) => (
                <option key={p.id} value={p.id}>{p.nombre}</option>
              ))}
            </select>
          </div>

          <div className="col-md-3">
            <label className="form-label" htmlFor="rp-categoria">Categoría</label>
            <select
              id="rp-categoria"
              className="form-select"
              value={filtros.idCategoria}
              onChange={(e) => setFiltros({ ...filtros, idCategoria: e.target.value })}
            >
              <option value="">Todas</option>
              {categorias.map((c) => (
                <option key={c.id} value={c.id}>{c.nombre}</option>
              ))}
            </select>
          </div>

          <div className="col-md-3">
            <div className="form-check">
              <input
                id="rp-stockbajo"
                type="checkbox"
                className="form-check-input"
                checked={filtros.stockBajo}
                onChange={(e) => setFiltros({ ...filtros, stockBajo: e.target.checked })}
              />
              <label className="form-check-label" htmlFor="rp-stockbajo">
                Solo stock bajo
              </label>
            </div>
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
          Elige los filtros que necesites y pulsa «Generar reporte».
        </div>
      )}

      {reporte && resumen && (
        <>
          {/* ---------------- INDICADORES ---------------- */}
          <div className="tarjetas-kpi mb-3">
            <TarjetaIndicador icono="📦" titulo="Productos" valor={formatoEntero(resumen.totalProductos)} />
            <TarjetaIndicador icono="🧮" titulo="Unidades en stock" valor={formatoEntero(resumen.unidadesEnStock)} />
            <TarjetaIndicador icono="💰" titulo="Valor inventario" valor={formatoDinero(resumen.valorInventario, 0)} />
            <TarjetaIndicador
              icono="⚠️"
              titulo="Stock bajo"
              valor={formatoEntero(resumen.stockBajo)}
              claseValor={resumen.stockBajo > 0 ? "text-warning" : ""}
            />
            <TarjetaIndicador
              icono="⛔"
              titulo="Agotados"
              valor={formatoEntero(resumen.agotados)}
              claseValor={resumen.agotados > 0 ? "text-danger" : ""}
            />
          </div>

          {/* ---------------- DISTRIBUCIÓN POR ESTADO ---------------- */}
          {resumen.totalProductos > 0 && (
            <div className="tarjeta mb-3">
              <p className="mb-2"><strong>Distribución por estado</strong></p>
              <BarraEstados
                disponibles={resumen.disponibles}
                agotados={resumen.agotados}
                inactivos={resumen.inactivos}
              />
            </div>
          )}

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
                placeholder="Buscar por código o nombre..."
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
                No hay productos que coincidan con los filtros.
              </p>
            ) : (
              <>
                <div className="table-responsive">
                  <table className="table table-hover align-middle">
                    <thead>
                      <tr>
                        <th>Código</th>
                        <th>Producto</th>
                        <th>Categoría</th>
                        <th>Proveedor</th>
                        <th className="text-end">Stock</th>
                        <th className="text-end">Mínimo</th>
                        <th className="text-end">P. compra</th>
                        <th className="text-end">P. venta</th>
                        <th>Estado</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filasPagina.map((p) => (
                        <tr key={p.idProducto}>
                          <td>{p.codigo}</td>
                          <td>{p.nombre}</td>
                          <td>{p.categoria}</td>
                          <td>{p.proveedor}</td>
                          <td className="text-end">{formatoEntero(p.stockActual)}</td>
                          <td className="text-end">{formatoEntero(p.stockMinimo)}</td>
                          <td className="text-end">{formatoDinero(p.precioCompra)}</td>
                          <td className="text-end">{formatoDinero(p.precioVenta)}</td>
                          <td>
                            <InsigniaEstado estado={p.estado} />
                            {p.stockBajo && (
                              <span className="badge bg-warning text-dark ms-1">Stock bajo</span>
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                <div className="d-flex flex-wrap justify-content-between align-items-center gap-2">
                  <small className="text-muted">
                    {formatoEntero(filasFiltradas.length)} producto(s)
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

/* ============================================================
   PIEZAS PROPIAS DE ESTA PESTAÑA
   ============================================================ */

function InsigniaEstado({ estado }: { estado: string }) {
  const clase =
    estado === "DISPONIBLE" ? "bg-success"
    : estado === "AGOTADO" ? "bg-danger"
    : "bg-secondary";

  return <span className={"badge " + clase}>{estado}</span>;
}

/**
 * Barra apilada con la proporción de productos por estado.
 * Siempre lleva etiquetas de texto (no depende solo del color).
 */
function BarraEstados(props: { disponibles: number; agotados: number; inactivos: number }) {
  const total = props.disponibles + props.agotados + props.inactivos;

  const partes = [
    { nombre: "Disponibles", valor: props.disponibles, color: "#0CA30C" },
    { nombre: "Agotados", valor: props.agotados, color: "#D03B3B" },
    { nombre: "Inactivos", valor: props.inactivos, color: "#8A8F98" },
  ];

  return (
    <>
      <div
        className="d-flex"
        style={{ height: 14, borderRadius: 7, overflow: "hidden", gap: 2 }}
        role="img"
        aria-label={partes.map((p) => `${p.nombre}: ${p.valor}`).join(", ")}
      >
        {partes
          .filter((p) => p.valor > 0)
          .map((p) => (
            <div
              key={p.nombre}
              title={`${p.nombre}: ${p.valor}`}
              style={{ width: `${(p.valor / total) * 100}%`, background: p.color }}
            />
          ))}
      </div>

      <div className="d-flex flex-wrap gap-3 mt-2">
        {partes.map((p) => (
          <small key={p.nombre} className="text-muted">
            <span
              style={{
                display: "inline-block",
                width: 10,
                height: 10,
                borderRadius: 2,
                background: p.color,
                marginRight: 6,
              }}
            />
            {p.nombre}: <strong>{formatoEntero(p.valor)}</strong>
            {" "}({Math.round((p.valor / total) * 100)}%)
          </small>
        ))}
      </div>
    </>
  );
}
