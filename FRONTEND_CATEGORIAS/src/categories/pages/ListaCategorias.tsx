import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Sidebar } from "../../components/Sidebar";
import { categoriaService } from "../services/categoriaService";
import type { ICategoria } from "../types/ICategoria";

// PÁGINA: Listado de Categorías
// --------------------------------
// Es la pantalla principal del módulo. Al cargar, pide al Backend
// la lista de categorías activas y las muestra en una tabla, con
// buscador en tiempo real y acciones de Editar/Desactivar.

export function ListaCategorias() {
  const [categorias, setCategorias] = useState<ICategoria[]>([]);
  const [cargando, setCargando] = useState(true);
  const [mensajeError, setMensajeError] = useState<string | null>(null);
  const [textoBusqueda, setTextoBusqueda] = useState("");
  const [verTodas, setVerTodas] = useState(false);

  // useEffect con [] vacío = se ejecuta UNA sola vez, cuando el
  // componente se muestra por primera vez en pantalla.
  useEffect(() => {
    cargarCategorias();
  }, [verTodas]);

  async function cargarCategorias() {
    try {
      setCargando(true);
      setMensajeError(null);
      const datos = verTodas
  ? await categoriaService.listarTodas()
  : await categoriaService.listar();
      setCategorias(datos);
    } catch (error) {
      setMensajeError(
        "No se pudo conectar con el servidor. Verifica que el Backend esté corriendo en el puerto 8080."
      );
    } finally {
      setCargando(false);
    }
  }

  async function manejarDesactivar(id: number) {
    const confirmado = window.confirm("¿Desactivar esta categoría?");
    if (!confirmado) return;

    try {
      await categoriaService.desactivar(id);
      // Volvemos a pedir la lista al Backend para que se refleje el cambio
      cargarCategorias();
      } catch (error) {
    alert("Ocurrió un error al desactivar la categoría.");
  }
}

async function manejarReactivar(id: number) {
  const confirmado = window.confirm("¿Reactivar esta categoría?");
  if (!confirmado) return;

  try {
    await categoriaService.reactivar(id);
    cargarCategorias();
  } catch (error) {
    setMensajeError("No se pudo reactivar la categoría.");
  }
  }

  // Filtro en el navegador: no vuelve a pedir datos al Backend,
  // solo oculta/muestra filas ya cargadas en memoria.
  const categoriasFiltradas = categorias.filter((categoria) =>
    (categoria.nombre + " " + categoria.descripcion)
      .toLowerCase()
      .includes(textoBusqueda.toLowerCase())
  );

  return (
    <div className="contenedor">
      <Sidebar />

      <div className="contenido">
        <div className="encabezado-pagina">
          <div>
            <h1>Categorías</h1>
            <p className="text-muted">Gestión de categorías</p>
          </div>
          <Link to="/categorias/nueva" className="btn btn-primary">
            + Nueva categoría
          </Link>
        </div>

        {mensajeError && <div className="alert alert-danger">{mensajeError}</div>}

        <div className="d-flex gap-2 mb-3">
  <input
    type="text"
    className="form-control buscador"
    placeholder="Buscar categoría..."
    value={textoBusqueda}
    onChange={(e) => setTextoBusqueda(e.target.value)}
  />
  <button
    className="btn btn-outline-secondary text-nowrap"
    onClick={() => setVerTodas(!verTodas)}
  >
    {verTodas ? "Ver solo activas" : "Ver todas (incluye inactivas)"}
  </button>
</div>

        <div className="tarjeta">
          {cargando ? (
            <p className="text-center text-muted py-4">Cargando categorías...</p>
          ) : (
            <table className="table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Descripción</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {categoriasFiltradas.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="text-center text-muted py-4">
                      No se encontraron categorías.
                    </td>
                  </tr>
                ) : (
                  categoriasFiltradas.map((categoria) => (
                    <tr key={categoria.id}>
                      <td>{categoria.id}</td>
                      <td>{categoria.nombre}</td>
                      <td>{categoria.descripcion}</td>
                      <td>
                        <span
                          className={
                            "badge " +
                            (categoria.activo ? "bg-success" : "bg-danger")
                          }
                        >
                          {categoria.activo ? "Activo" : "Inactivo"}
                        </span>
                      </td>
                      <td>
                        <Link
                          to={`/categorias/editar/${categoria.id}`}
                          className="link-editar me-3"
                        >
                          Editar
                        </Link>
                        {categoria.activo ? (
  <button
    className="link-desactivar"
    onClick={() => manejarDesactivar(categoria.id)}
  >
    Desactivar
  </button>
) : (
  <button
  className="link-desactivar"
  onClick={() => manejarReactivar(categoria.id)}
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
