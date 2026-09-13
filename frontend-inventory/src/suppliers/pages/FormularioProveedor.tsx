import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Sidebar } from "../../components/Sidebar";
import { proveedorService } from "../services/proveedorService";

// PÁGINA: Formulario de Proveedor (sirve para Crear y para Editar)
// --------------------------------------------------------------------
// useParams() lee el "id" que viene en la URL. Si la ruta es
// "/proveedores/nuevo", no hay id -> es un proveedor nuevo.
// Si la ruta es "/proveedores/editar/5", sí hay id -> se está editando.

export function FormularioProveedor() {
  const { id } = useParams();
  const navegar = useNavigate();
  const esEdicion = Boolean(id);

  const [nombre, setNombre] = useState("");
  const [documento, setDocumento] = useState("");
  const [telefono, setTelefono] = useState("");
  const [correo, setCorreo] = useState("");
  const [direccion, setDireccion] = useState("");
  const [mensajeError, setMensajeError] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);

  // Si estamos editando, cargamos los datos actuales del proveedor
  // apenas se abre la página, para llenar el formulario.
  useEffect(() => {
    if (esEdicion && id) {
      proveedorService
        .buscarPorId(Number(id))
        .then((proveedor) => {
          setNombre(proveedor.nombre);
          setDocumento(proveedor.documento);
          setTelefono(proveedor.telefono);
          setCorreo(proveedor.correo);
          setDireccion(proveedor.direccion);
        })
        .catch(() => setMensajeError("No se pudo cargar el proveedor."));
    }
  }, [id, esEdicion]);

  async function manejarEnvio(evento: React.FormEvent) {
    evento.preventDefault();
    setMensajeError(null);

    // Validación básica en el Frontend, solo para mejorar la
    // experiencia del usuario. La validación que realmente protege
    // los datos es la que ya existe en el Backend (Spring Boot).
    if (nombre.trim().length === 0) {
      setMensajeError("El nombre del proveedor es obligatorio.");
      return;
    }
    if (correo.trim().length === 0) {
      setMensajeError("El correo del proveedor es obligatorio.");
      return;
    }

    try {
      setGuardando(true);
      const datos = { nombre, documento, telefono, correo, direccion };
      if (esEdicion && id) {
        await proveedorService.actualizar(Number(id), datos);
      } else {
        await proveedorService.crear(datos);
      }
      navegar("/proveedores");
    } catch (error: any) {
      // Si el Backend devuelve un mensaje claro (por ejemplo "nombre
      // ya registrado"), lo mostramos tal cual llega.
      const mensajeBackend = error?.response?.data?.message;
      setMensajeError(mensajeBackend || "Ocurrió un error al guardar el proveedor.");
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
            <h1>{esEdicion ? "Editar proveedor" : "Nuevo proveedor"}</h1>
            <p className="text-muted">Complete los datos y guarde los cambios</p>
          </div>
        </div>

        {mensajeError && <div className="alert alert-danger">{mensajeError}</div>}

        <form className="formulario" onSubmit={manejarEnvio}>
          <div className="mb-3">
            <label className="form-label fw-bold">Nombre del proveedor</label>
            <input
              type="text"
              className="form-control"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              required
            />
          </div>

          <div className="mb-3">
            <label className="form-label fw-bold">Documento (NIT / Cédula)</label>
            <input
              type="text"
              className="form-control"
              value={documento}
              onChange={(e) => setDocumento(e.target.value)}
            />
          </div>

          <div className="mb-3">
            <label className="form-label fw-bold">Teléfono</label>
            <input
              type="text"
              className="form-control"
              value={telefono}
              onChange={(e) => setTelefono(e.target.value)}
            />
          </div>

          <div className="mb-3">
            <label className="form-label fw-bold">Correo electrónico</label>
            <input
              type="email"
              className="form-control"
              value={correo}
              onChange={(e) => setCorreo(e.target.value)}
              required
            />
          </div>

          <div className="mb-3">
            <label className="form-label fw-bold">Dirección</label>
            <input
              type="text"
              className="form-control"
              value={direccion}
              onChange={(e) => setDireccion(e.target.value)}
            />
          </div>

          <button type="submit" className="btn btn-primary me-2" disabled={guardando}>
            {guardando ? "Guardando..." : "Guardar"}
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => navegar("/proveedores")}
          >
            Cancelar
          </button>
        </form>
      </div>
    </div>
  );
}