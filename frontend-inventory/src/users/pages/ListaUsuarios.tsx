import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Sidebar } from "../../components/Sidebar";
import usuarioService from "../services/usuarioService";
import { IUsuario } from "../types/IUsuario";

const ListaUsuarios = () => {
  // Guarda la lista de usuarios obtenida desde el backend.
  const [usuarios, setUsuarios] = useState<IUsuario[]>([]);

  // Controla el estado de carga y los mensajes de error.
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState("");

  // Permite filtrar usuarios desde el frontend.
  const [busqueda, setBusqueda] = useState("");

  // Carga los usuarios al entrar a la página.
  useEffect(() => {
    cargarUsuarios();
  }, []);

  // Obtiene todos los usuarios desde el backend.
  const cargarUsuarios = async () => {
    try {
      setCargando(true);
      setError("");

      const datos = await usuarioService.listar();
      setUsuarios(datos);
    } catch (error) {
      console.error("Error al cargar usuarios:", error);
      setError("No fue posible cargar los usuarios.");
    } finally {
      setCargando(false);
    }
  };

  // Desactiva un usuario después de confirmar la acción.
  const desactivarUsuario = async (id: number) => {
    const confirmar = window.confirm(
      "¿Está seguro de que desea desactivar este usuario?"
    );

    if (!confirmar) {
      return;
    }

    try {
      await usuarioService.desactivar(id);
      await cargarUsuarios();
    } catch (error) {
      console.error("Error al desactivar usuario:", error);
      setError("No fue posible desactivar el usuario.");
    }
  };

  // Reactiva un usuario después de confirmar la acción.
  const reactivarUsuario = async (id: number) => {
    const confirmar = window.confirm(
      "¿Está seguro de que desea reactivar este usuario?"
    );

    if (!confirmar) {
      return;
    }

    try {
      await usuarioService.reactivar(id);
      await cargarUsuarios();
    } catch (error) {
      console.error("Error al reactivar usuario:", error);
      setError("No fue posible reactivar el usuario.");
    }
  };

  // Filtra los usuarios según los datos escritos en el buscador.
  const usuariosFiltrados = usuarios.filter((usuario) => {
    const texto = `
      ${usuario.nombre}
      ${usuario.apellido}
      ${usuario.username}
      ${usuario.email}
      ${usuario.rol}
    `.toLowerCase();

    return texto.includes(busqueda.toLowerCase());
  });

  return (
    <div className="d-flex">
      {/* Menú lateral principal del sistema. */}
      <Sidebar />

      {/* Contenido principal de la página. */}
      <main className="flex-grow-1 p-4">
        <div className="container-fluid">

          {/* Encabezado de la gestión de usuarios. */}
          <div className="d-flex justify-content-between align-items-center mb-4">
            <div>
              <h2 className="mb-1">Gestión de Usuarios</h2>
              <p className="text-muted mb-0">
                Administra los usuarios registrados en el sistema.
              </p>
            </div>

            {/* Botón para registrar un nuevo usuario. */}
            <Link to="/usuarios/nuevo" className="btn btn-primary">
              Nuevo Usuario
            </Link>
          </div>

          {/* Mensaje de error. */}
          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          {/* Buscador de usuarios. */}
          <div className="card shadow-sm mb-4">
            <div className="card-body">
              <label htmlFor="busqueda" className="form-label">
                Buscar usuario
              </label>

              <input
                id="busqueda"
                type="text"
                className="form-control"
                placeholder="Buscar por nombre, username, correo o rol..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
              />
            </div>
          </div>

          {/* Tabla de usuarios. */}
          <div className="card shadow-sm">
            <div className="card-body">

              {cargando ? (
                <div className="text-center py-4">
                  <div className="spinner-border" role="status">
                    <span className="visually-hidden">Cargando...</span>
                  </div>
                  <p className="mt-2 mb-0">Cargando usuarios...</p>
                </div>
              ) : usuariosFiltrados.length === 0 ? (
                <div className="alert alert-info mb-0">
                  No se encontraron usuarios.
                </div>
              ) : (
                <div className="table-responsive">
                  <table className="table table-hover align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Apellido</th>
                        <th>Username</th>
                        <th>Correo</th>
                        <th>Rol</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                      </tr>
                    </thead>

                    <tbody>
                      {usuariosFiltrados.map((usuario) => (
                        <tr key={usuario.idUsuario}>
                          <td>{usuario.idUsuario}</td>
                          <td>{usuario.nombre}</td>
                          <td>{usuario.apellido}</td>
                          <td>{usuario.username}</td>
                          <td>{usuario.email}</td>
                          <td>{usuario.rol}</td>

                          {/* Muestra el estado actual del usuario. */}
                          <td>
                            {usuario.activo ? (
                              <span className="badge bg-success">
                                Activo
                              </span>
                            ) : (
                              <span className="badge bg-danger">
                                Inactivo
                              </span>
                            )}
                          </td>

                          {/* Acciones disponibles para cada usuario. */}
                          <td>
                            <div className="d-flex gap-2">
                              <Link
                                to={`/usuarios/editar/${usuario.idUsuario}`}
                                className="btn btn-sm btn-outline-primary"
                              >
                                Editar
                              </Link>

                              {usuario.activo ? (
                                <button
                                  type="button"
                                  className="btn btn-sm btn-outline-danger"
                                  onClick={() =>
                                    desactivarUsuario(usuario.idUsuario)
                                  }
                                >
                                  Desactivar
                                </button>
                              ) : (
                                <button
                                  type="button"
                                  className="btn btn-sm btn-outline-success"
                                  onClick={() =>
                                    reactivarUsuario(usuario.idUsuario)
                                  }
                                >
                                  Reactivar
                                </button>
                              )}
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}

            </div>
          </div>

        </div>
      </main>
    </div>
  );
};

export default ListaUsuarios;