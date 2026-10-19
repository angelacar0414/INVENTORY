import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { Sidebar } from "../../components/Sidebar";
import { Icono, type NombreIcono } from "../../components/Iconos";
import { TarjetaKpi } from "../../components/TarjetaKpi";
import { dashboardService } from "../services/dashboardService";
import { IDashboardResumen } from "../types/IDashboard";

/**
 * PÁGINA: Dashboard
 * -----------------
 * Pantalla principal del sistema. Es de solo lectura (SSD 7.10): solo
 * muestra información consolidada del inventario.
 *
 * Bloques (EKB 20_FLUJO_GENERAL_DEL_SISTEMA):
 *   1. Alerta de reposición (stock bajo y agotados).
 *   2. Totales: productos, categorías, proveedores, clientes y movimientos.
 *   3. Actividad de hoy y estado del inventario.
 *   4. Últimos movimientos registrados.
 *   5. Accesos rápidos a los módulos permitidos por el rol.
 */
export const DashboardPage = () => {
  const [resumen, setResumen] = useState<IDashboardResumen | null>(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // El Reporte solo lo puede ver el ADMINISTRADOR (RNF-07).
  const esAdministrador = sessionStorage.getItem("rol") === "ADMINISTRADOR";

  useEffect(() => {
    cargarResumen();
  }, []);

  const cargarResumen = async () => {
    try {
      setCargando(true);
      setError(null);
      const datos = await dashboardService.obtenerResumen();
      setResumen(datos);
    } catch (err) {
      setError("No pudimos cargar el resumen del dashboard. Intentemos de nuevo más tarde.");
    } finally {
      setCargando(false);
    }
  };

  const formatearFechaHora = (fechaTexto: string) => {
    const fecha = new Date(fechaTexto);
    if (isNaN(fecha.getTime())) {
      return fechaTexto;
    }
    return fecha.toLocaleString("es-CO", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const hoy = new Date().toLocaleDateString("es-CO", {
    weekday: "long",
    day: "numeric",
    month: "long",
    year: "numeric",
  });

  return (
    <div className="contenedor">
      <Sidebar />

      <div className="contenido">

        <div className="encabezado-pagina">
          <div>
            <h2>Dashboard</h2>
            <p className="text-muted mb-0">Resumen general del inventario</p>
          </div>
          <span className="fecha-encabezado">{hoy}</span>
        </div>

        {cargando && <p className="text-muted">Cargando indicadores...</p>}
        {error && <div className="alert alert-danger">{error}</div>}

        {!cargando && !error && resumen && (
          <ContenidoDashboard
            resumen={resumen}
            esAdministrador={esAdministrador}
            formatearFechaHora={formatearFechaHora}
          />
        )}

      </div>
    </div>
  );
};

/* ============================================================
   CONTENIDO (se separa para que la página principal sea corta)
   ============================================================ */

interface PropsContenido {
  resumen: IDashboardResumen;
  esAdministrador: boolean;
  formatearFechaHora: (fecha: string) => string;
}

function ContenidoDashboard({ resumen, esAdministrador, formatearFechaHora }: PropsContenido) {

  const necesitaReposicion = resumen.stockBajo + resumen.agotados > 0;

  // Estado del inventario: el stock bajo es un subconjunto de los
  // productos con existencias, así que se separa para que las tres
  // partes sumen el total de productos activos.
  const disponibles = Math.max(
    0,
    resumen.totalProductos - resumen.stockBajo - resumen.agotados
  );

  return (
    <>
      {/* ---------------- 1. ALERTA ---------------- */}
      {necesitaReposicion && (
        <div className="dash-alerta">
          <span className="dash-alerta-icono">
            <Icono nombre="alerta" tamano={20} />
          </span>

          <span>
            {resumen.stockBajo > 0 && (
              <>
                <strong>
                  {resumen.stockBajo} {resumen.stockBajo === 1 ? "producto" : "productos"} con stock bajo
                </strong>
              </>
            )}
            {resumen.stockBajo > 0 && resumen.agotados > 0 && " y "}
            {resumen.agotados > 0 && (
              <strong>
                {resumen.agotados} {resumen.agotados === 1 ? "agotado" : "agotados"}
              </strong>
            )}{" "}
            necesitan reposición.
          </span>

          <Link
            to={esAdministrador ? "/reportes" : "/productos"}
            className="dash-alerta-boton"
          >
            {esAdministrador ? "Ver reporte" : "Ver productos"}
            <Icono nombre="derecha" tamano={15} />
          </Link>
        </div>
      )}

      {/* ---------------- 2. TOTALES ---------------- */}
      <div className="kpi-fila">
        <TarjetaKpi icono="paquete" color="azul" titulo="Productos" valor={resumen.totalProductos} to="/productos" />
        <TarjetaKpi icono="capas" color="morado" titulo="Categorías" valor={resumen.totalCategorias} to="/categorias" />
        <TarjetaKpi icono="camion" color="gris" titulo="Proveedores" valor={resumen.totalProveedores} to="/proveedores" />
        <TarjetaKpi icono="personas" color="verde" titulo="Clientes" valor={resumen.totalClientes} to="/clientes" />
        <TarjetaKpi icono="intercambio" color="azul" titulo="Movimientos" valor={resumen.totalMovimientos} to="/movimientos" />
      </div>

      {/* ---------------- 3. ACTIVIDAD Y ESTADO ---------------- */}
      <div className="dash-fila">

        <div className="tarjeta dash-actividad">
          <div className="dash-titulo-fila">
            <h5>Actividad de hoy</h5>
            <small className="text-muted">
              {resumen.entradasHoy + resumen.salidasHoy} movimiento(s)
            </small>
          </div>

          <div className="dash-actividad-cuerpo">
            <div className="dash-actividad-item">
              <span className="kpi-chip kpi-azul"><Icono nombre="entrada" tamano={21} /></span>
              <span>
                <span className="kpi-titulo">Entradas</span>
                <span className="kpi-valor texto-entrada">
                  {resumen.unidadesEntradasHoy}
                  <small className="dash-unidades"> unidades</small>
                </span>
                <span className="dash-detalle">
                  {resumen.entradasHoy} movimiento(s)
                </span>
              </span>
            </div>

            <div className="dash-separador" />

            <div className="dash-actividad-item">
              <span className="kpi-chip kpi-naranja"><Icono nombre="salida" tamano={21} /></span>
              <span>
                <span className="kpi-titulo">Salidas</span>
                <span className="kpi-valor texto-salida">
                  {resumen.unidadesSalidasHoy}
                  <small className="dash-unidades"> unidades</small>
                </span>
                <span className="dash-detalle">
                  {resumen.salidasHoy} movimiento(s)
                </span>
              </span>
            </div>
          </div>
        </div>

        <div className="tarjeta dash-estado">
          <h5>Estado del inventario</h5>
          <BarraEstado
            disponibles={disponibles}
            stockBajo={resumen.stockBajo}
            agotados={resumen.agotados}
          />
        </div>

      </div>

      {/* ---------------- 4. ÚLTIMOS MOVIMIENTOS ---------------- */}
      <div className="tarjeta mb-3">
        <div className="dash-titulo-fila">
          <h5>Últimos movimientos</h5>
          <Link to="/movimientos" className="enlace-ver-todos">
            Ver todos <Icono nombre="derecha" tamano={15} />
          </Link>
        </div>

        {resumen.ultimosMovimientos.length === 0 ? (
          <p className="text-muted mb-0">
            Todavía no hay movimientos registrados. Esta tabla se llena al
            registrar entradas y salidas en el módulo de Movimientos.
          </p>
        ) : (
          <div className="table-responsive">
            <table className="table align-middle mb-0">
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Producto</th>
                  <th>Tipo</th>
                  <th className="text-end">Cantidad</th>
                  <th>Usuario</th>
                  <th>Cliente</th>
                </tr>
              </thead>
              <tbody>
                {resumen.ultimosMovimientos.map((m, indice) => {
                  const esEntrada = m.tipoMovimiento === "ENTRADA";
                  return (
                    <tr key={indice}>
                      <td className="text-muted">{formatearFechaHora(m.fecha)}</td>
                      <td><strong>{m.producto}</strong></td>
                      <td>
                        <span className={"etiqueta-tipo " + (esEntrada ? "etiqueta-entrada" : "etiqueta-salida")}>
                          <Icono nombre={esEntrada ? "entrada" : "salida"} tamano={13} />
                          {m.tipoMovimiento}
                        </span>
                      </td>
                      <td className={"text-end fw-semibold " + (esEntrada ? "texto-entrada" : "texto-salida")}>
                        {esEntrada ? "+" : "−"}{m.cantidad}
                      </td>
                      <td>{m.usuario}</td>
                      <td className="text-muted">{m.cliente ?? "—"}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* ---------------- 5. ACCESOS RÁPIDOS ---------------- */}
      <div className="tarjeta">
        <h5 className="mb-3">Accesos rápidos</h5>
        <div className="dash-accesos">
          <AccesoRapido to="/movimientos/entrada" icono="mas" texto="Registrar entrada" estilo="entrada" />
          <AccesoRapido to="/movimientos/salida" icono="mas" texto="Registrar salida" estilo="salida" />
          <AccesoRapido to="/productos" icono="paquete" texto="Productos" />
          {esAdministrador && (
            <AccesoRapido to="/reportes" icono="grafica" texto="Reportes" />
          )}
        </div>
      </div>
    </>
  );
}

/* ============================================================
   PIEZAS PROPIAS DEL DASHBOARD
   ============================================================ */

function AccesoRapido(props: {
  to: string;
  icono: NombreIcono;
  texto: string;
  estilo?: "entrada" | "salida";
}) {
  return (
    <Link to={props.to} className={"acceso-rapido " + (props.estilo ? "acceso-" + props.estilo : "")}>
      <Icono nombre={props.icono} tamano={17} />
      {props.texto}
    </Link>
  );
}

/**
 * Barra apilada con los productos disponibles, con stock bajo y
 * agotados. Siempre lleva etiquetas con texto y números (el color
 * nunca es la única forma de entender el dato).
 */
function BarraEstado(props: { disponibles: number; stockBajo: number; agotados: number }) {
  const total = props.disponibles + props.stockBajo + props.agotados;

  const partes = [
    { nombre: "Disponibles", valor: props.disponibles, color: "#0CA30C" },
    { nombre: "Stock bajo", valor: props.stockBajo, color: "#E8710A" },
    { nombre: "Agotados", valor: props.agotados, color: "#D03B3B" },
  ];

  if (total === 0) {
    return <p className="text-muted mb-0">Todavía no hay productos activos.</p>;
  }

  return (
    <>
      <div
        className="dash-barra"
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

      <div className="dash-leyenda">
        {partes.map((p) => (
          <span key={p.nombre}>
            <i style={{ background: p.color }} />
            {p.nombre} <strong>{p.valor}</strong> ({Math.round((p.valor / total) * 100)}%)
          </span>
        ))}
      </div>
    </>
  );
}
