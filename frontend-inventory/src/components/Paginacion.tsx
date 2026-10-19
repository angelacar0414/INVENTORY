import { useEffect, useState } from "react";

/**
 * PAGINACIÓN COMPARTIDA
 * ---------------------
 * El estándar de interfaz de INVENTORY (EKB) pide que todas las listas
 * tengan paginación. En lugar de repetir el mismo código en cada módulo,
 * esta pieza se escribe una sola vez y todas las listas la reutilizan.
 *
 * Tiene dos partes:
 *
 *  1) usePaginacion(): un "hook" que recibe la lista completa y devuelve
 *     solo las filas de la página actual (de 10 en 10).
 *  2) <Paginacion />: los botones "Anterior / Siguiente" y el texto
 *     "Página X de Y".
 *
 * La paginación se hace en el navegador, sobre los datos que la pantalla
 * ya cargó (igual que el buscador). Si la lista cambia (por ejemplo al
 * buscar), vuelve a la página 1 para no quedarse en una página que ya
 * no existe.
 */

export const FILAS_POR_PAGINA = 10;

export function usePaginacion<T>(elementos: T[], porPagina = FILAS_POR_PAGINA) {
  const [pagina, setPagina] = useState(1);

  const totalPaginas = Math.max(1, Math.ceil(elementos.length / porPagina));

  // Si cambia la cantidad de resultados, regresamos a la primera página.
  useEffect(() => {
    setPagina(1);
  }, [elementos.length]);

  const paginaActual = Math.min(pagina, totalPaginas);

  const visibles = elementos.slice(
    (paginaActual - 1) * porPagina,
    paginaActual * porPagina
  );

  return {
    pagina: paginaActual,
    setPagina,
    totalPaginas,
    visibles,
    total: elementos.length,
  };
}

interface PropsPaginacion {
  pagina: number;
  totalPaginas: number;
  onCambiar: (pagina: number) => void;
}

export function Paginacion({ pagina, totalPaginas, onCambiar }: PropsPaginacion) {
  // Con una sola página no hace falta mostrar los botones.
  if (totalPaginas <= 1) return null;

  return (
    <div className="d-flex justify-content-end align-items-center gap-2 mt-3">
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
