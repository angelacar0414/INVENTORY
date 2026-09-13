import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Sidebar } from "../../components/Sidebar";
import { proveedorService } from "../services/proveedorService";
import type { IProveedor } from "../types/IProveedor";

// PÁGINA: Listado de Proveedores
// --------------------------------
// Es la pantalla principal del módulo. Al cargar, pide al Backend
// la lista de proveedores activos y los muestra en una tabla, con
// buscador en tiempo real y acciones de Editar/Desactivar.

export function ListaProveedores() {
  const [proveedores, setProveedores] = useState<IProveedor[]>([]);
  const [cargando, setCargando] = useState(true);
  const [mensajeError, setMensajeError] = useState<string | null>(null);
  const [textoBusqueda, setTextoBusqueda] = useState("");
  const [verTodas, setVerTodas] = useState(false);

  useEffect(() => {
    cargarProveedores();
  }, [verTodas]);

  async function cargarProveedores() {
    try {
      setCargando(true);
      setMensajeError(null);
      const datos = verTodas
        ? await proveedorService.listarTodas()
        : await proveedorService.listar();
      setProveedores(datos);
    } catch (error) {
      setMensajeError(
        "No se pudo conectar con el servidor. Verifica que el Backend esté corriendo en el puerto 8080."
      );
    } finally {
      setCargando(false);
    }
  }

  async function manejarDesactivar(id: number) {
    const confirmado = window.confirm("¿Desactivar este proveedor?");
    if (!confirmado) return;

    try {
      await proveedorService.desactivar(id);
      cargarProveedores();
    } catch (error) {
      alert("Ocurrió un error al desactivar el proveedor.");
    }
  }

  async function manejarReactivar(id: number) {
    const confirmado = window.confirm("¿Reactivar este proveedor?");
    if (!confirmado) return;

    try {
      await proveedorService.reactivar(id);
      cargarProveedores();
    } catch (error) {
      setMensajeError("No se pudo reactivar el proveedor.");
    }
  }

  // Filtro en el navegador: no vuelve a pedir datos al Backend,
  // solo oculta/muestra filas ya cargadas en memoria.
  const proveedoresFiltrados = proveedores.filter((proveedor) =>
    (proveedor.nombre + " " + proveedor.correo)
      .toLowerCase()
      .includes(textoBusqueda.toLowerCase())
  );

  return (
    <div className="contenedor">
      <Sidebar />

      <div className="contenido">
        <div className="encabezado-pagina">
          <div>
            <h1>Proveedores</h1>
            <p className="text-muted">Gestión de proveedores</p>
          </div>
          <Link to="/proveedores/nuevo" className="btn btn-primary">
            + Nuevo proveedor
          </Link>
        </div>

        {mensajeError && <div className="alert alert-danger">{mensajeError}</div>}

        <div className="d-flex gap-2 mb-3">
          <input
            type="text"
            className="form-control buscador"
            placeholder="Buscar proveedor..."
            value={textoBusqueda}
            onChange={(e) => setTextoBusqueda(e.target.value)}
          />
          <button
            className="btn btn-outline-secondary text-nowrap"
            onClick={() => setVerTodas(!verTodas)}
          >
            {verTodas ? "Ver solo activos" : "Ver todos (incluye inactivos)"}
          </button>
        </div>

        <div className="tarjeta">
          {cargando ? (
            <p className="text-center text-muted py-4">Cargando proveedores...</p>
          ) : (
            <table className="table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Documento</th>
                  <th>Teléfono</th>
                  <th>Correo</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {proveedoresFiltrados.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="text-center text-muted py-4">
                      No se encontraron proveedores.
                    </td>
                  </tr>
                ) : (
                  proveedoresFiltrados.map((proveedor) => (
                    <tr key={proveedor.id}>
                      <td>{proveedor.id}</td>
                      <td>{proveedor.nombre}</td>
                      <td>{proveedor.documento}</td>
                      <td>{proveedor.telefono}</td>
                      <td>{proveedor.correo}</td>
                      <td>
                        <span
                          className={
                            "badge " +
                            (proveedor.activo ? "bg-success" : "bg-danger")
                          }
                        >
                          {proveedor.activo ? "Activo" : "Inactivo"}
                        </span>
                      </td>
                      <td>
                        <Link
                          to={`/proveedores/editar/${proveedor.id}`}
                          className="link-editar me-3"
                        >
                          Editar
                        </Link>
                        {proveedor.activo ? (
                          <button
                            className="link-desactivar"
                            onClick={() => manejarDesactivar(proveedor.id)}
                          >
                            Desactivar
                          </button>
                        ) : (
                          <button
                            className="link-desactivar"
                            onClick={() => manejarReactivar(proveedor.id)}
                          >
                            Reactivar
                          </button>
                        )}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}