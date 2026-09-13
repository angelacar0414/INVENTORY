import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { authService } from "../services/authService";

export function RegistroPage() {
  const navigate = useNavigate();

  const [formulario, setFormulario] = useState({
    nombre: "",
    apellido: "",
    username: "",
    email: "",
    contraseña: "",
  });

  const [mensaje, setMensaje] = useState("");
  const [error, setError] = useState("");
  const [cargando, setCargando] = useState(false);

  function manejarCambio(
    e: React.ChangeEvent<HTMLInputElement>
  ) {
    setFormulario({
      ...formulario,
      [e.target.name]: e.target.value,
    });
  }

  async function manejarRegistro(e: FormEvent) {
    e.preventDefault();

    setMensaje("");
    setError("");

    if (
      !formulario.nombre ||
      !formulario.apellido ||
      !formulario.username ||
      !formulario.email ||
      !formulario.contraseña
    ) {
      setError("Todos los campos son obligatorios.");
      return;
    }

    try {
      setCargando(true);

      const respuesta = await authService.registrar(formulario);

      if (respuesta.success) {
        setMensaje(
          "Usuario registrado correctamente. Ahora puede iniciar sesión."
        );

        setTimeout(() => {
          navigate("/login");
        }, 1500);
      } else {
        setError(respuesta.message);
      }

    } catch (error: any) {
      if (error.response?.data?.message) {
        setError(error.response.data.message);
      } else {
        setError("No se pudo conectar con el servidor.");
      }
    } finally {
      setCargando(false);
    }
  }

  return (
    <div className="d-flex align-items-center justify-content-center min-vh-100 bg-light">
      <div className="card shadow-sm border-0" style={{ width: "500px" }}>
        <div className="card-body p-4">

          <div className="text-center mb-4">
            <h2 className="fw-bold">INVENTORY</h2>
            <p className="text-muted">
              Crear cuenta
            </p>
          </div>

          {mensaje && (
            <div className="alert alert-success">
              {mensaje}
            </div>
          )}

          {error && (
            <div className="alert alert-danger">
              {error}
            </div>
          )}

          <form onSubmit={manejarRegistro}>

            <div className="row">

              <div className="col-md-6 mb-3">
                <label className="form-label">Nombre</label>
                <input
                  type="text"
                  name="nombre"
                  className="form-control"
                  value={formulario.nombre}
                  onChange={manejarCambio}
                />
              </div>

              <div className="col-md-6 mb-3">
                <label className="form-label">Apellido</label>
                <input
                  type="text"
                  name="apellido"
                  className="form-control"
                  value={formulario.apellido}
                  onChange={manejarCambio}
                />
              </div>

            </div>

            <div className="mb-3">
              <label className="form-label">Username</label>
              <input
                type="text"
                name="username"
                className="form-control"
                value={formulario.username}
                onChange={manejarCambio}
              />
            </div>

            <div className="mb-3">
              <label className="form-label">Correo electrónico</label>
              <input
                type="email"
                name="email"
                className="form-control"
                value={formulario.email}
                onChange={manejarCambio}
              />
            </div>

            <div className="mb-3">
              <label className="form-label">Contraseña</label>
              <input
                type="password"
                name="contraseña"
                className="form-control"
                value={formulario.contraseña}
                onChange={manejarCambio}
              />
            </div>

            <button
              type="submit"
              className="btn btn-primary w-100"
              disabled={cargando}
            >
              {cargando ? "Registrando..." : "Crear cuenta"}
            </button>

          </form>

          <div className="text-center mt-3">
            <button
              className="btn btn-link"
              onClick={() => navigate("/login")}
            >
              Volver al inicio de sesión
            </button>
          </div>

        </div>
      </div>
    </div>
  );
}