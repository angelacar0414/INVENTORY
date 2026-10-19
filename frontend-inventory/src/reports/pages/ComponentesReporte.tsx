import type { FormatoDescarga } from "../services/reporteService";

/**
 * PIEZAS COMPARTIDAS DE LAS PANTALLAS DE REPORTES
 * -----------------------------------------------
 * Las dos pestañas (Productos y Movimientos) usan los mismos
 * bloques: tarjetas de indicadores, paginación, botones de
 * descarga y algunos formatos. Se definen aquí una sola vez
 * para no repetir código.
 */

/* ============================================================
   FORMATOS
   ============================================================ */

export function formatoEntero(valor: number): string {
  return valor.toLocaleString("es-CO");
}

export function formatoDinero(valor: number, decimales = 2): string {
  return "$ " + valor.toLocaleString("es-CO", { maximumFractionDigits: decimales });
}

export function formatoFechaHora(fechaIso: string): string {
  return new Date(fechaIso).toLocaleString("es-CO", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

/**
 * Convierte una fecha a AAAA-MM-DD usando la hora LOCAL.
 * (toISOString() usa hora UTC y, en Colombia, después de las 7 p. m.
 * devolvería el día siguiente.)
 */
export function aTextoFecha(fecha: Date): string {
  const dos = (n: number) => String(n).padStart(2, "0");
  return `${fecha.getFullYear()}-${dos(fecha.getMonth() + 1)}-${dos(fecha.getDate())}`;
}

/* ============================================================
   TARJETA DE INDICADOR
   ============================================================
   Usa las mismas clases del Dashboard (tarjeta-kpi...).
   ============================================================ */

interface PropsTarjeta {
  icono: string;
  titulo: string;
  valor: string;
  // Clase opcional para colorear el valor (por ejemplo text-danger).
  claseValor?: string;
}

export function TarjetaIndicador({ icono, titulo, valor, claseValor }: PropsTarjeta) {
  return (
    <div className="tarjeta tarjeta-kpi">
      <span className="tarjeta-kpi-icono">{icono}</span>
      <div>
        <p className="tarjeta-kpi-titulo">{titulo}</p>
        <p className={"tarjeta-kpi-valor " + (claseValor ?? "")}>{valor}</p>
      </div>
    </div>
  );
}

/* ============================================================
   PAGINACIÓN
   ============================================================ */

interface PropsPaginacion {
  pagina: number;
  totalPaginas: number;
  onCambiar: (pagina: number) => void;
}

export function Paginacion({ pagina, totalPaginas, onCambiar }: PropsPaginacion) {
  return (
    <div className="d-flex align-items-center gap-2">
      <button
        type="button"
        className="btn btn-sm btn-outline-secondary"
        onClick={() => onCambiar(pagina - 1)}
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
        onClick={() => onCambiar(pagina + 1)}
        disabled={pagina >= totalPaginas}
      >
        Siguiente
      </button>
    </div>
  );
}

/* ============================================================
   BOTONES DE DESCARGA
   ============================================================ */

interface PropsDescarga {
  // true cuando no hay un reporte con registros para exportar.
  deshabilitado: boolean;
  registros: number;
  // Formato que se está descargando en este momento (si hay alguno).
  descargando: FormatoDescarga | null;
  onDescargar: (formato: FormatoDescarga) => void;
}

export function BotonesDescarga({
  deshabilitado,
  registros,
  descargando,
  onDescargar,
}: PropsDescarga) {

  const ocupado = descargando !== null;

  return (
    <div className="d-flex flex-wrap align-items-center gap-2">

      <button
        type="button"
        className="btn btn-outline-danger"
        disabled={deshabilitado || ocupado}
        onClick={() => onDescargar("pdf")}
      >
        {descargando === "pdf" ? "Generando PDF..." : "Descargar PDF"}
      </button>

      <button
        type="button"
        className="btn btn-outline-success"
        disabled={deshabilitado || ocupado}
        onClick={() => onDescargar("excel")}
      >
        {descargando === "excel" ? "Generando Excel..." : "Descargar Excel"}
      </button>

      <small className="text-muted">
        {deshabilitado
          ? "Genera un reporte con resultados para poder exportarlo."
          : `Se exportarán ${formatoEntero(registros)} registro(s) con los filtros aplicados.`}
      </small>

    </div>
  );
}
