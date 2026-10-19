import { Link } from "react-router-dom";
import { Icono, type NombreIcono } from "./Iconos";

/**
 * TARJETA DE INDICADOR (KPI)
 * --------------------------
 * Tarjeta pequeña con un icono, un título y un número. La usan el
 * Dashboard y los Reportes para que los indicadores se vean igual en
 * todo el sistema.
 *
 * Si se le pasa "to", toda la tarjeta es un enlace al módulo
 * correspondiente.
 */

export type ColorKpi = "azul" | "naranja" | "verde" | "rojo" | "gris" | "morado";

interface Props {
  icono: NombreIcono;
  color?: ColorKpi;
  titulo: string;
  valor: string | number;
  // Clase opcional para colorear el valor (por ejemplo "text-danger").
  claseValor?: string;
  // Ruta a la que lleva la tarjeta (opcional).
  to?: string;
}

export function TarjetaKpi({
  icono,
  color = "azul",
  titulo,
  valor,
  claseValor,
  to,
}: Props) {
  const contenido = (
    <>
      <span className={"kpi-chip kpi-" + color}>
        <Icono nombre={icono} tamano={21} />
      </span>
      <span>
        <span className="kpi-titulo">{titulo}</span>
        <span className={"kpi-valor " + (claseValor ?? "")}>{valor}</span>
      </span>
    </>
  );

  if (to) {
    return (
      <Link to={to} className="kpi-tarjeta kpi-enlace">
        {contenido}
      </Link>
    );
  }

  return <div className="kpi-tarjeta">{contenido}</div>;
}
