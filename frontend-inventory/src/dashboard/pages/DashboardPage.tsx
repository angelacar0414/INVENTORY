import { useEffect, useState } from "react";
import { Sidebar } from "../../components/Sidebar";
import { dashboardService } from "../services/dashboardService";
import { IDashboardResumen } from "../types/IDashboard";

// Esta es la pantalla que se ve apenas iniciamos sesión. Seguimos el mismo
// esqueleto que ya usamos en las demás páginas (contenedor + Sidebar +
// contenido), para que se vea igual de consistente que el resto del sistema.
export const DashboardPage = () => {
  const [resumen, setResumen] = useState<IDashboardResumen | null>(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);

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

  const formatearFecha = (fechaTexto: string) => {
    const fecha = new Date(fechaTexto);
    if (isNaN(fecha.getTime())) {
      return fechaTexto;
    }
    return fecha.toLocaleDateString("es-CO");
  };

  return (
    <div className="contenedor">
      <Sidebar />
      <div className="contenido">
        <div className="encabezado-pagina">
          <div>
            <h2>Dashboard</h2>
            <p>Resumen general del inventario</p>
          </div>
        </div>

        {cargando && <p>Cargando indicadores...</p>}

        {error && <div className="alert alert-danger">{error}</div>}

        {!cargando && !error && resumen && (
          <>
            {/*
              Estas 5 tarjetas muestran los indicadores principales.
              Productos totales, stock bajo y agotados van a quedar en 0
              hasta que armemos el módulo de Productos, y entradas/salidas
              de hoy van a quedar en 0 hasta que armemos Movimientos. El
              backend ya está listo para devolver los números reales apenas
              esas tablas tengan información.
            */}
            <div className="tarjetas-kpi">
              <div className="tarjeta tarjeta-kpi">
                <span className="tarjeta-kpi-icono">📦</span>
                <div>
                  <p className="tarjeta-kpi-titulo">PRODUCTOS TOTALES</p>
                  <p className="tarjeta-kpi-valor">{resumen.totalProductos}</p>
                </div>
              </div>

              <div className="tarjeta tarjeta-kpi">
                <span className="tarjeta-kpi-icono">⚠️</span>
                <div>
                  <p className="tarjeta-kpi-titulo">STOCK BAJO</p>
                  <p className="tarjeta-kpi-valor">{resumen.stockBajo}</p>
                </div>
              </div>

              <div className="tarjeta tarjeta-kpi">
                <span className="tarjeta-kpi-icono">⛔</span>
                <div>
                  <p className="tarjeta-kpi-titulo">AGOTADOS</p>
                  <p className="tarjeta-kpi-valor">{resumen.agotados}</p>
                </div>
              </div>

              <div className="tarjeta tarjeta-kpi">
                <span className="tarjeta-kpi-icono">⬆️</span>
                <div>
                  <p className="tarjeta-kpi-titulo">ENTRADAS HOY</p>
                  <p className="tarjeta-kpi-valor">{resumen.entradasHoy}</p>
                </div>
              </div>

              <div className="tarjeta tarjeta-kpi">
                <span className="tarjeta-kpi-icono">🔄</span>
                <div>
                  <p className="tarjeta-kpi-titulo">SALIDAS HOY</p>
                  <p className="tarjeta-kpi-valor">{resumen.salidasHoy}</p>
                </div>
              </div>
            </div>

            <div className="tarjeta">
              <h5>Últimos movimientos</h5>
              {resumen.ultimosMovimientos.length === 0 ? (
                <p>
                  Todavía no hay movimientos registrados. Esta tabla se va a
                  ir llenando apenas empecemos a registrar entradas y salidas
                  en el módulo de Movimientos.
                </p>
              ) : (
                <table className="table">
                  <thead>
                    <tr>
                      <th>Fecha</th>
                      <th>Producto</th>
                      <th>Tipo</th>
                      <th>Cantidad</th>
                      <th>Usuario</th>
                      <th>Cliente</th>
                    </tr>
                  </thead>
                  <tbody>
                    {resumen.ultimosMovimientos.map((movimiento, indice) => (
                      <tr key={indice}>
                        <td>{formatearFecha(movimiento.fecha)}</td>
                        <td>{movimiento.producto}</td>
                        <td>{movimiento.tipoMovimiento}</td>
                        <td>{movimiento.cantidad}</td>
                        <td>{movimiento.usuario}</td>
                        <td>{movimiento.cliente ?? "-"}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
};
