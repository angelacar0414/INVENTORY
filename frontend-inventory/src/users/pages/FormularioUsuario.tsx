import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Sidebar } from "../../components/Sidebar";
import usuarioService from "../services/usuarioService";
import {
  IUsuarioActualizar,
  IUsuarioCrear,
} from "../types/IUsuario";

const FormularioUsuario = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();

  // Determina si el formulario corresponde a un usuario existente.
  const esEdicion = Boolean(id);

  // Guarda los datos básicos del formulario.
  const [nombre, setNombre] = useState("");
  const [apellido, setApellido] = useState("");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [contraseña, setContraseña] = useState("");
  const [rol, setRol] = useState("OPERADOR");

  // Controla los estados de carga, guardado y errores.
  const [cargando, setCargando] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState("");

  // Si estamos editando, obtiene los datos actuales del usuario.
  useEffect(() => {
    if (esEdicion && id) {
      cargarUsuario(Number(id));
    }
  }, [esEdicion, id]);

  // Carga un usuario por su ID.
  const cargarUsuario = async (idUsuario: number) => {
    try {
      setCargando(true);
      setError("");

      const usuario = await usuarioService.obtenerPorId(idUsuario);

      setNombre(usuario.nombre);
      setApellido(usuario.apellido);
      setUsername(usuario.username);
      setEmail(usuario.email);
      setRol(usuario.rol);
    } catch (error) {
      console.error("Error al cargar usuario:", error);
      setError("No fue posible cargar la información del usuario.");
    } finally {
      setCargando(false);
    }
  };

  // Valida los datos básicos antes de enviarlos al backend.
  const validarFormulario = (): boolean => {
    if (!nombre.trim()) {
      setError("El nombre es obligatorio.");
      return false;
    }

    if (!apellido.trim()) {
      setError("El apellido es obligatorio.");
      return false;
    }

    if (!username.trim()) {
      setError("El username es obligatorio.");
      return false;
    }

    if (!email.trim()) {
      setError("El correo electrónico es obligatorio.");
      return false;
    }

    if (!esEdicion && !contraseña.trim()) {
      setError("La contraseña es obligatoria para registrar un usuario.");
      return false;
    }

    if (!rol) {
      setError("Debe seleccionar un rol.");
      return false;
    }

    return true;
  };

  // Guarda un usuario nuevo o actualiza uno existente.
  const guardarUsuario = async (e: React.FormEvent) => {
    e.preventDefault();

    setError("");

    if (!validarFormulario()) {
      return;
    }

    try {
      setGuardando(true);

      if (esEdicion && id) {
        // Para editar, enviamos únicamente los campos aceptados por el PUT.
        const usuario: IUsuarioActualizar = {
          nombre: nombre.trim(),
          apellido: apellido.trim(),
          username: username.trim(),
          email: email.trim(),
          rol,
        };

        await usuarioService.actualizar(Number(id), usuario);
      } else {
        // Para crear, también enviamos la contraseña.
        const usuario: IUsuarioCrear = {
          nombre: nombre.trim(),
          apellido: apellido.trim(),
          username: username.trim(),
          email: email.trim(),
          contraseña,
          rol,
        };

        await usuarioService.crear(usuario);
      }

      // Regresa a la lista después de guardar correctamente.
      navigate("/usuarios");
    } catch (error: any) {
      console.error("Error al guardar usuario:", error);

      // Muestra el mensaje enviado por el backend cuando está disponible.
      const mensaje =
        error?.response?.data?.message ||
        error?.response?.data ||
        "No fue posible guardar el usuario.";

      setError(
        typeof mensaje === "string"
          ? mensaje
          : "No fue posible guardar el usuario."
      );
    } finally {
      setGuardando(false);
    }
  };

  return (
    <div className="d-flex">
      {/* Menú lateral principal del sistema. */}
      <Sidebar />

      {/* Contenido principal del formulario. */}
      <main className="flex-grow-1 p-4">
        <div className="container">

          {/* Título del formulario. */}
          <div className="mb-4">
            <h2>{esEdicion ? "Editar Usuario" : "Nuevo Usuario"}</h2>
            <p className="text-muted mb-0">
              {esEdicion
                ? "Actualiza la información del usuario."
                : "Registra un nuevo usuario en el sistema."}
            </p>
          </div>

          {/* Mensaje de error. */}
          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          {cargando ? (
            // Indicador mientras se carga el usuario para editar.
            <div className="text-center py-4">
              <div className="spinner-border" role="status">
                <span className="visually-hidden">Cargando...</span>
              </div>
              <p className="mt-2">Cargando usuario...</p>
            </div>
          ) : (
            <div className="card shadow-sm">
              <div className="card-body">

                <form onSubmit={guardarUsuario}>

                  {/* Nombre y apellido. */}
                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label htmlFor="nombre" className="form-label">
                        Nombre
                      </label>

                      <input
                        id="nombre"
                        type="text"
                        className="form-control"
                        value={nombre}
                        onChange={(e) => setNombre(e.target.value)}
                        required
                      />
                    </div>

                    <div className="col-md-6 mb-3">
                      <label htmlFor="apellido" className="form-label">
                        Apellido
                      </label>

                      <input
                        id="apellido"
                        type="text"
                        className="form-control"
                        value={apellido}
                        onChange={(e) => setApellido(e.target.value)}
                        required
                      />
                    </div>
                  </div>

                  {/* Username y correo electrónico. */}
                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label htmlFor="username" className="form-label">
                        Username
                      </label>

                      <input
                        id="username"
                        type="text"
                        className="form-control"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                      />
                    </div>

                    <div className="col-md-6 mb-3">
                      <label htmlFor="email" className="form-label">
                        Correo electrónico
                      </label>

                      <input
                        id="email"
                        type="email"
                        className="form-control"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                      />
                    </div>
                  </div>

                  {/* La contraseña solamente aparece al registrar. */}
                  {!esEdicion && (
                    <div className="mb-3">
                      <label htmlFor="contraseña" className="form-label">
                        Contraseña
                      </label>

                      <input
                        id="contraseña"
                        type="password"
                        className="form-control"
                        value={contraseña}
                        onChange={(e) => setContraseña(e.target.value)}
                        required
                      />
                    </div>
                  )}

                  {/* Selección del rol. */}
                  <div className="mb-4">
                    <label htmlFor="rol" className="form-label">
                      Rol
                    </label>

                    <select
                      id="rol"
                      className="form-select"
                      value={rol}
                      onChange={(e) => setRol(e.target.value)}
                      required
                    >
                      <option value="OPERADOR">OPERADOR</option>
                      <option value="ADMINISTRADOR">ADMINISTRADOR</option>
                    </select>
                  </div>

                  {/* Botones del formulario. */}
                  <div className="d-flex gap-2">
                    <button
                      type="submit"
                      className="btn btn-primary"
                      disabled={guardando}
                    >
                      {guardando ? "Guardando..." : "Guardar"}
                    </button>

                    <button
                      type="button"
                      className="btn btn-secondary"
                      onClick={() => navigate("/usuarios")}
                      disabled={guardando}
                    >
                      Cancelar
                    </button>
                  </div>

                </form>
              </div>
            </div>
          )}

        </div>
      </main>
    </div>
  );
};

export default FormularioUsuario;