import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Sidebar } from "../../components/Sidebar";
import { clienteService } from "../services/clienteService";
import type { ICliente } from "../types/ICliente";

// PÁGINA: Listado de Clientes
// --------------------------------
// Es la pantalla principal del módulo. Al cargar, pide al Backend
// la lista de clientes activos y los muestra en una tabla, con
// buscador en tiempo real (filtrado en el navegador, igual que en
// Proveedores) y acciones de Editar/Desactivar.

export function ListaClientes() {
  const [clientes, setClientes] = useState<ICliente[]>([]);
  const [cargando, setCargando] = useState(true);
  const [mensajeError, setMensajeError] = useState<string | null>(null);
  const [textoBusqueda, setTextoBusqueda] = useState("");
  const [verTodas, setVerTodas] = useState(false);

  useEffect(() => {
    cargarClientes();
  }, [verTodas]);

  async function cargarClientes() {
    try {
      setCargando(true);
      setMensajeError(null);
      const datos = verTodas
        ? await clienteService.listarTodas()
        : await clienteService.listar();
      setClientes(datos);
    } catch (error) {
      setMensajeError(
        "No se pudo conectar con el servidor. Verifica que el Backend esté corriendo en el puerto 8080."
      );
    } finally {
      setCargando(false);
    }
  }

  async function manejarDesactivar(id: number) {
    const confirmado = window.confirm("¿Desactivar este cliente?");
    if (!confirmado) return;

    try {
      await clienteService.desactivar(id);
      cargarClientes();
    } catch (error) {
      alert("Ocurrió un error al desactivar el cliente.");
    }
  }

  async function manejarReactivar(id: number) {
    const confirmado = window.confirm("¿Reactivar este cliente?");
    if (!confirmado) return;

    try {
      await clienteService.reactivar(id);
      cargarClientes();
    } catch (error) {
      setMensajeError("No se pudo reactivar el cliente.");
    }
  }

  // Filtro en el navegador: no vuelve a pedir datos al Backend,
  // solo oculta/muestra filas ya cargadas en memoria.
  const clientesFiltrados = clientes.filter((cliente) =>
    (cliente.nombre + " " + (cliente.correo || ""))
      .toLowerCase()
      .includes(textoBusqueda.toLowerCase())
  );

  return (
    <div className="contenedor">
      <Sidebar />

      <div className="contenido">
        <div className="encabezado-pagina">
          <div>
            <h1>Clientes</h1>
            <p className="text-muted">Gestión de clientes</p>
          </div>
          <Link to="/clientes/nuevo" className="btn btn-primary">
            + Nuevo cliente
          </Link>
        </div>

        {mensajeError && <div className="alert alert-danger">{mensajeError}</div>}

        <div className="d-flex gap-2 mb-3">
          <input
            type="text"
            className="form-control buscador"
            placeholder="Buscar cliente..."
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
            <p className="text-center text-muted py-4">Cargando clientes...</p>
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
                {clientesFiltrados.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="text-center text-muted py-4">
                      No se encontraron clientes.
                    </td>
                  </tr>
                ) : (
                  clientesFiltrados.map((cliente) => (
                    <tr key={cliente.idCliente}>
                      <td>{cliente.idCliente}</td>
                      <td>{cliente.nombre}</td>
                      <td>{cliente.documento}</td>
                      <td>{cliente.telefono}</td>
                      <td>{cliente.correo}</td>
                      <td>
                        <span
                          className={
                            "badge " +
                            (cliente.activo ? "bg-success" : "bg-danger")
                          }
                        >
                          {cliente.activo ? "Activo" : "Inactivo"}
                        </span>
                      </td>
                      <td>
                        <Link
                          to={`/clientes/editar/${cliente.idCliente}`}
                          className="link-editar me-3"
                        >
                          Editar
                        </Link>
                        {cliente.activo ? (
                          <button
                            className="link-desactivar"
                            onClick={() => manejarDesactivar(cliente.idCliente)}
                          >
                            Desactivar
                          </button>
                        ) : (
                          <button
                            className="link-desactivar"
                            onClick={() => manejarReactivar(cliente.idCliente)}
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