import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Sidebar } from "../../components/Sidebar";
import { clienteService } from "../services/clienteService";
import type { IClienteFormulario } from "../types/ICliente";

// PÁGINA: Formulario de Cliente
// -----------------------------
// Un solo componente atiende tanto "Nuevo cliente" como "Editar
// cliente": si useParams() trae un id, sabemos que estamos editando
// y precargamos los datos; si no trae id, es un registro nuevo.
// No pedimos dirección porque decidimos que no es un dato necesario
// para el cliente (el campo sigue existiendo en la base de datos,
// solo que no lo llenamos desde acá).

const datosVacios: IClienteFormulario = {
  nombre: "",
  documento: "",
  telefono: "",
  correo: "",
};

export function FormularioCliente() {
  const { id } = useParams();
  const navigate = useNavigate();
  const modoEdicion = Boolean(id);

  const [datos, setDatos] = useState<IClienteFormulario>(datosVacios);
  const [cargando, setCargando] = useState(modoEdicion);
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!modoEdicion) return;

    async function cargarCliente() {
      try {
        const cliente = await clienteService.buscarPorId(Number(id));
        setDatos({
          nombre: cliente.nombre,
          documento: cliente.documento || "",
          telefono: cliente.telefono || "",
          correo: cliente.correo || "",
        });
      } catch (error) {
        setError("No pudimos cargar la información de este cliente.");
      } finally {
        setCargando(false);
      }
    }

    cargarCliente();
  }, [id, modoEdicion]);

  function manejarCambio(e: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = e.target;
    setDatos((prev) => ({ ...prev, [name]: value }));
  }

  async function manejarEnvio(e: React.FormEvent) {
    e.preventDefault();
    setError(null);

    if (!datos.nombre.trim()) {
      setError("El nombre del cliente es obligatorio.");
      return;
    }

    try {
      setGuardando(true);
      if (modoEdicion) {
        await clienteService.actualizar(Number(id), datos);
      } else {
        await clienteService.crear(datos);
      }
      navigate("/clientes");
    } catch (err: any) {
      const mensaje = err?.response?.data?.message || "No pudimos guardar el cliente.";
      setError(mensaje);
    } finally {
      setGuardando(false);
    }
  }

  return (
    <div className="contenedor">
      <Sidebar />

      <div className="contenido">
        <div className="encabezado-pagina">
          <div>
            <h1>{modoEdicion ? "Editar cliente" : "Nuevo cliente"}</h1>
            <p className="text-muted">Gestión de clientes</p>
          </div>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}

        <div className="tarjeta">
          {cargando ? (
            <p className="text-center text-muted py-4">Cargando información del cliente...</p>
          ) : (
            <form onSubmit={manejarEnvio} style={{ maxWidth: "500px" }}>
              <div className="mb-3">
                <label className="form-label">Nombre *</label>
                <input
                  type="text"
                  name="nombre"
                  className="form-control"
                  value={datos.nombre}
                  onChange={manejarCambio}
                  required
                />
              </div>

              <div className="mb-3">
                <label className="form-label">Documento</label>
                <input
                  type="text"
                  name="documento"
                  className="form-control"
                  value={datos.documento}
                  onChange={manejarCambio}
                />
              </div>

              <div className="mb-3">
                <label className="form-label">Teléfono</label>
                <input
                  type="text"
                  name="telefono"
                  className="form-control"
                  value={datos.telefono}
                  onChange={manejarCambio}
                />
              </div>

              <div className="mb-3">
                <label className="form-label">Correo</label>
                <input
                  type="email"
                  name="correo"
                  className="form-control"
                  value={datos.correo}
                  onChange={manejarCambio}
                />
              </div>

              <div className="d-flex gap-2">
                <button type="submit" className="btn btn-primary" disabled={guardando}>
                  {guardando ? "Guardando..." : "Guardar"}
                </button>
                <button
                  type="button"
                  className="btn btn-outline-secondary"
                  onClick={() => navigate("/clientes")}
                  disabled={guardando}
                >
                  Cancelar
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}