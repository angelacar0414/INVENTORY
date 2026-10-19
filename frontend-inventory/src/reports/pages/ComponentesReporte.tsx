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

/**
 * Versión corta para las tarjetas de indicadores, donde el espacio es
 * limitado: 12.345.679 se muestra como "$ 12,3 M".
 */
export function formatoDineroCorto(valor: number): string {
  if (Math.abs(valor) >= 1_000_000) {
    return "$ " + (valor / 1_000_000).toLocaleString("es-CO", { maximumFractionDigits: 1 }) + " M";
  }
  return formatoDinero(valor, 0);
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
   Se reutiliza la tarjeta compartida (components/TarjetaKpi.tsx),
   la misma del Dashboard, para que se vean igual.
   ============================================================ */

export { TarjetaKpi as TarjetaIndicador } from "../../components/TarjetaKpi";

/* ============================================================
   PAGINACIÓN
   ============================================================
   Se reutiliza la paginación compartida del proyecto
   (components/Paginacion.tsx) para no duplicar código.
   ============================================================ */

export { Paginacion } from "../../components/Paginacion";

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
