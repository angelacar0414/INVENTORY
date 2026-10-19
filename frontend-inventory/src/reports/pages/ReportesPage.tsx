import { useEffect, useState } from "react";

import { Sidebar } from "../../components/Sidebar";
import { categoriaService } from "../../categories/services/categoriaService";
import { clienteService } from "../../clients/services/clienteService";
import { productoService } from "../../products/services/productoService";
import { proveedorService } from "../../suppliers/services/proveedorService";
import type { ICategoria } from "../../categories/types/ICategoria";
import type { ICliente } from "../../clients/types/ICliente";
import type { IProducto } from "../../products/types/IProducto";
import type { IProveedor } from "../../suppliers/types/IProveedor";
import { ReporteMovimientosTab } from "./ReporteMovimientosTab";
import { ReporteProductosTab } from "./ReporteProductosTab";

type Pestana = "productos" | "movimientos";

/**
 * PÁGINA: Reportes
 * ----------------
 * Pantalla con dos pestañas (Productos y Movimientos). Cada una
 * permite filtrar, ver el resultado y descargarlo en PDF o Excel.
 *
 * Esta página solo carga los catálogos que alimentan los filtros
 * (proveedores, categorías, productos y clientes) y decide qué
 * pestaña mostrar. El trabajo de cada reporte vive en su pestaña.
 *
 * El acceso es solo para el rol ADMINISTRADOR (lo exige el Backend).
 */
export function ReportesPage() {

  const [pestana, setPestana] = useState<Pestana>("productos");

  const [proveedores, setProveedores] = useState<IProveedor[]>([]);
  const [categorias, setCategorias] = useState<ICategoria[]>([]);
  const [productos, setProductos] = useState<IProducto[]>([]);
  const [clientes, setClientes] = useState<ICliente[]>([]);
  const [errorCatalogos, setErrorCatalogos] = useState("");

  useEffect(() => {
    const cargarCatalogos = async () => {
      try {
        // Se piden todos (activos e inactivos): un reporte histórico
        // puede necesitar filtrar por algo que hoy está inactivo.
        const [prov, cat, prod, cli] = await Promise.all([
          proveedorService.listarTodas(),
          categoriaService.listarTodas(),
          productoService.listarTodas(),
          clienteService.listarTodas(),
        ]);

        setProveedores(prov);
        setCategorias(cat);
        setProductos(prod);
        setClientes(cli);
      } catch (error: any) {
        setErrorCatalogos(
          error?.response?.status === 403
            ? "Solo el administrador puede ver los reportes."
            : "No se pudieron cargar las listas de los filtros."
        );
      }
    };

    cargarCatalogos();
  }, []);

  return (
    <div className="d-flex">
      <Sidebar />

      <main className="contenido flex-grow-1">

        <div className="encabezado-pagina">
          <div>
            <h1>Reportes</h1>
            <p className="text-muted mb-0">
              Consulta el inventario y sus movimientos, y descárgalos en PDF o Excel.
            </p>
          </div>
        </div>

        {errorCatalogos && (
          <div className="alert alert-danger">{errorCatalogos}</div>
        )}

        <ul className="nav nav-tabs mb-3">
          <li className="nav-item">
            <button
              type="button"
              className={"nav-link " + (pestana === "productos" ? "active" : "")}
              onClick={() => setPestana("productos")}
            >
              Productos
            </button>
          </li>
          <li className="nav-item">
            <button
              type="button"
              className={"nav-link " + (pestana === "movimientos" ? "active" : "")}
              onClick={() => setPestana("movimientos")}
            >
              Movimientos
            </button>
          </li>
        </ul>

        {pestana === "productos" ? (
          <ReporteProductosTab proveedores={proveedores} categorias={categorias} />
        ) : (
          <ReporteMovimientosTab productos={productos} clientes={clientes} />
        )}

      </main>
    </div>
  );
}
