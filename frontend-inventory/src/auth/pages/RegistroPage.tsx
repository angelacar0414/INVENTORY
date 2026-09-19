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

  // Agregué este estado para poder mostrar u ocultar la contraseña
  // que el usuario va escribiendo. Empieza en false porque por
  // seguridad la contraseña debe iniciar oculta.
  const [mostrarContraseña, setMostrarContraseña] = useState(false);

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

            {/*
              Aquí modifiqué el campo de contraseña. Antes era un input
              solo, y ahora lo metí dentro de un input-group de Bootstrap
              para poder ponerle el botón del ojito pegado al lado derecho.
              Así, si me equivoco escribiendo la contraseña, puedo darle
              clic al ojito y confirmar qué escribí antes de enviar el
              formulario.
            */}
            <div className="mb-3">
              <label className="form-label">Contraseña</label>
              <div className="input-group">
                <input
                  type={mostrarContraseña ? "text" : "password"}
                  name="contraseña"
                  className="form-control"
                  value={formulario.contraseña}
                  onChange={manejarCambio}
                />
                <button
                  type="button"
                  className="btn btn-outline-secondary"
                  onClick={() => setMostrarContraseña(!mostrarContraseña)}
                  tabIndex={-1}
                >
                  {/*
                    Uso un ícono distinto según el estado: un ojo abierto
                    cuando la contraseña está oculta (invitando a mostrarla),
                    y un ojo tachado cuando ya se está mostrando.
                  */}
                  {mostrarContraseña ? (
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16">
                      <path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755q-.247.246-.517.482zm-2.943 1.299.772.772a6.7 6.7 0 0 1-1.913.591c-.62.089-1.243.089-1.865 0C4 13.5 1.5 8 1.5 8a13 13 0 0 1 2.343-2.923l.772.772A11.9 11.9 0 0 0 2.679 8c.058.087.122.183.195.288.335.48.83 1.12 1.465 1.755C5.503 11.207 6.649 12 8 12c.994 0 1.929-.328 2.416-.463zM8 5.5a2.5 2.5 0 0 1 2.5 2.5c0 .524-.185 1.005-.492 1.379l-3.887-3.887A2.49 2.49 0 0 1 8 5.5m-.5 2.5a.5.5 0 1 0-1 0 .5.5 0 0 0 1 0"/>
                      <path d="M1.646 1.646a.5.5 0 0 1 .708 0l12 12a.5.5 0 0 1-.708.708l-12-12a.5.5 0 0 1 0-.708"/>
                    </svg>
                  ) : (
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16">
                      <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8M1.173 8a13 13 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8q-.086.13-.195.288c-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z"/>
                      <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5M4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0"/>
                    </svg>
                  )}
                </button>
              </div>
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