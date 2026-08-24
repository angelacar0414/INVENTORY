import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Sidebar } from "../../components/Sidebar";
import { categoriaService } from "../services/categoriaService";

// PÁGINA: Formulario de Categoría (sirve para Crear y para Editar)
// --------------------------------------------------------------------
// useParams() lee el "id" que viene en la URL. Si la ruta es
// "/categorias/nueva", no hay id -> es una categoría nueva.
// Si la ruta es "/categorias/editar/5", sí hay id -> se está editando.

export function FormularioCategoria() {
  const { id } = useParams();
  const navegar = useNavigate();
  const esEdicion = Boolean(id);

  const [nombre, setNombre] = useState("");
  const [descripcion, setDescripcion] = useState("");
  const [mensajeError, setMensajeError] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);

  // Si estamos editando, cargamos los datos actuales de la categoría
  // apenas se abre la página, para llenar el formulario.
  useEffect(() => {
    if (esEdicion && id) {
      categoriaService
        .buscarPorId(Number(id))
        .then((categoria) => {
          setNombre(categoria.nombre);
          setDescripcion(categoria.descripcion);
        })
        .catch(() => setMensajeError("No se pudo cargar la categoría."));
    }
  }, [id, esEdicion]);

  async function manejarEnvio(evento: React.FormEvent) {
    evento.preventDefault();
    setMensajeError(null);

    // Validación básica en el Frontend, solo para mejorar la
    // experiencia del usuario. La validación que realmente protege
    // los datos es la que ya existe en el Backend (Spring Boot).
    if (nombre.trim().length === 0) {
      setMensajeError("El nombre de la categoría es obligatorio.");
      return;
    }
    if (nombre.trim().length > 30) {
      setMensajeError("El nombre no puede superar los 30 caracteres.");
      return;
    }

    try {
      setGuardando(true);
      if (esEdicion && id) {
        await categoriaService.actualizar(Number(id), { nombre, descripcion });
      } else {
        await categoriaService.crear({ nombre, descripcion });
      }
      navegar("/categorias");
    } catch (error: any) {
      // Si el Backend devuelve un mensaje claro (por ejemplo "nombre
      // ya registrado"), lo mostramos tal cual llega.
      const mensajeBackend = error?.response?.data?.message;
      setMensajeError(mensajeBackend || "Ocurrió un error al guardar la categoría.");
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
            <h1>{esEdicion ? "Editar categoría" : "Nueva categoría"}</h1>
            <p className="text-muted">Complete los datos y guarde los cambios</p>
          </div>
        </div>

        {mensajeError && <div className="alert alert-danger">{mensajeError}</div>}

        <form className="formulario" onSubmit={manejarEnvio}>
          <div className="mb-3">
            <label className="form-label fw-bold">
              Nombre de la categoría (máx. 30 caracteres)
            </label>
            <input
              type="text"
              className="form-control"
              maxLength={30}
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              required
            />
          </div>

          <div className="mb-3">
            <label className="form-label fw-bold">Descripción</label>
            <textarea
              className="form-control"
              rows={3}
              maxLength={255}
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
            />
          </div>

          <button type="submit" className="btn btn-primary me-2" disabled={guardando}>
            {guardando ? "Guardando..." : "Guardar"}
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => navegar("/categorias")}
          >
            Cancelar
          </button>
        </form>
      </div>
    </div>
  );
}
