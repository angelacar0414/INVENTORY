import { useEffect, useState, type FormEvent } from "react";

import { Sidebar } from "../../components/Sidebar";
import { perfilService } from "../services/perfilService";
import type { IPerfil } from "../types/IPerfil";

const LARGO_MINIMO_CONTRASENA = 6;

/**
 * PÁGINA: Mi perfil
 * -----------------
 * Cada usuario (Administrador u Operador) puede:
 *  - Consultar su información (RF-032).
 *  - Actualizar su nombre, apellido y correo (RF-033).
 *  - Cambiar su contraseña (RF-034).
 *
 * El username y el rol se muestran pero no se pueden editar.
 */
export function PerfilPage() {

  const [perfil, setPerfil] = useState<IPerfil | null>(null);
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState("");

  // ----- Formulario de datos personales -----
  const [nombre, setNombre] = useState("");
  const [apellido, setApellido] = useState("");
  const [email, setEmail] = useState("");
  const [guardando, setGuardando] = useState(false);
  const [errorDatos, setErrorDatos] = useState("");
  const [exitoDatos, setExitoDatos] = useState("");

  // ----- Formulario de contraseña -----
  const [contrasenaActual, setContrasenaActual] = useState("");
  const [contrasenaNueva, setContrasenaNueva] = useState("");
  const [confirmacion, setConfirmacion] = useState("");
  const [cambiando, setCambiando] = useState(false);
  const [errorClave, setErrorClave] = useState("");
  const [exitoClave, setExitoClave] = useState("");

  useEffect(() => {
    const cargar = async () => {
      try {
        const datos = await perfilService.consultar();
        mostrar(datos);
      } catch (error: any) {
        setErrorCarga(
          error?.response?.data?.message || "No se pudo cargar tu perfil."
        );
      } finally {
        setCargando(false);
      }
    };

    cargar();
  }, []);

  /** Guarda el perfil recibido y rellena el formulario con sus datos. */
  const mostrar = (datos: IPerfil) => {
    setPerfil(datos);
    setNombre(datos.nombre ?? "");
    setApellido(datos.apellido ?? "");
    setEmail(datos.email ?? "");
  };

  const guardarDatos = async (evento: FormEvent) => {
    evento.preventDefault();
    setErrorDatos("");
    setExitoDatos("");

    // Validación básica en el navegador (el Backend la repite).
    if (!nombre.trim() || !apellido.trim() || !email.trim()) {
      setErrorDatos("Nombre, apellido y correo son obligatorios.");
      return;
    }
    if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email.trim())) {
      setErrorDatos("El correo electrónico no tiene un formato válido.");
      return;
    }

    setGuardando(true);
    try {
      const actualizado = await perfilService.actualizar({
        nombre: nombre.trim(),
        apellido: apellido.trim(),
        email: email.trim(),
      });
      mostrar(actualizado);
      setExitoDatos("Tus datos se actualizaron correctamente.");
    } catch (error: any) {
      setErrorDatos(
        error?.response?.data?.message || "No se pudieron guardar los cambios."
      );
    } finally {
      setGuardando(false);
    }
  };

  const guardarContrasena = async (evento: FormEvent) => {
    evento.preventDefault();
    setErrorClave("");
    setExitoClave("");

    if (!contrasenaActual) {
      setErrorClave("Escribe tu contraseña actual.");
      return;
    }
    if (contrasenaNueva.length < LARGO_MINIMO_CONTRASENA) {
      setErrorClave(
        `La nueva contraseña debe tener al menos ${LARGO_MINIMO_CONTRASENA} caracteres.`
      );
      return;
    }
    if (contrasenaNueva !== confirmacion) {
      setErrorClave("La confirmación no coincide con la nueva contraseña.");
      return;
    }
    if (contrasenaNueva === contrasenaActual) {
      setErrorClave("La nueva contraseña debe ser diferente a la actual.");
      return;
    }

    setCambiando(true);
    try {
      await perfilService.cambiarContrasena({
        contrasenaActual,
        contrasenaNueva,
        confirmacion,
      });
      setContrasenaActual("");
      setContrasenaNueva("");
      setConfirmacion("");
      setExitoClave("Tu contraseña se cambió correctamente.");
    } catch (error: any) {
      setErrorClave(
        error?.response?.data?.message || "No se pudo cambiar la contraseña."
      );
    } finally {
      setCambiando(false);
    }
  };

  const fechaCreacion = perfil?.fechaCreacion
    ? new Date(perfil.fechaCreacion).toLocaleDateString("es-CO")
    : "—";

  return (
    <div className="d-flex">
      <Sidebar />

      <main className="contenido flex-grow-1">

        <div className="encabezado-pagina">
          <div>
            <h1>Mi perfil</h1>
            <p className="text-muted mb-0">
              Consulta y actualiza tu información personal.
            </p>
          </div>
        </div>

        {cargando && <p className="text-muted">Cargando tu perfil...</p>}
        {errorCarga && <div className="alert alert-danger">{errorCarga}</div>}

        {perfil && (
          <div className="row g-3">

            {/* ---------------- DATOS PERSONALES ---------------- */}
            <div className="col-lg-6">
              <form className="tarjeta" onSubmit={guardarDatos} noValidate>
                <h5 className="mb-3">Datos personales</h5>

                <div className="row g-3">
                  <div className="col-md-6">
                    <label className="form-label" htmlFor="pf-username">Usuario</label>
                    <input
                      id="pf-username"
                      className="form-control"
                      value={perfil.username}
                      disabled
                    />
                  </div>

                  <div className="col-md-6">
                    <label className="form-label" htmlFor="pf-rol">Rol</label>
                    <input
                      id="pf-rol"
                      className="form-control"
                      value={perfil.rol}
                      disabled
                    />
                  </div>

                  <div className="col-md-6">
                    <label className="form-label" htmlFor="pf-nombre">Nombre</label>
                    <input
                      id="pf-nombre"
                      className="form-control"
                      value={nombre}
                      onChange={(e) => setNombre(e.target.value)}
                    />
                  </div>

                  <div className="col-md-6">
                    <label className="form-label" htmlFor="pf-apellido">Apellido</label>
                    <input
                      id="pf-apellido"
                      className="form-control"
                      value={apellido}
                      onChange={(e) => setApellido(e.target.value)}
                    />
                  </div>

                  <div className="col-12">
                    <label className="form-label" htmlFor="pf-email">Correo electrónico</label>
                    <input
                      id="pf-email"
                      type="email"
                      className="form-control"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                    />
                  </div>
                </div>

                <small className="text-muted d-block mt-3">
                  Cuenta creada el {fechaCreacion}.
                </small>

                {errorDatos && <div className="alert alert-danger mt-3 mb-0">{errorDatos}</div>}
                {exitoDatos && <div className="alert alert-success mt-3 mb-0">{exitoDatos}</div>}

                <button
                  type="submit"
                  className="btn btn-primary mt-3"
                  disabled={guardando}
                >
                  {guardando ? "Guardando..." : "Guardar cambios"}
                </button>
              </form>
            </div>

            {/* ---------------- CAMBIAR CONTRASEÑA ---------------- */}
            <div className="col-lg-6">
              <form className="tarjeta" onSubmit={guardarContrasena} noValidate>
                <h5 className="mb-3">Cambiar contraseña</h5>

                <div className="mb-3">
                  <label className="form-label" htmlFor="pf-actual">Contraseña actual</label>
                  <input
                    id="pf-actual"
                    type="password"
                    className="form-control"
                    autoComplete="current-password"
                    value={contrasenaActual}
                    onChange={(e) => setContrasenaActual(e.target.value)}
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label" htmlFor="pf-nueva">Nueva contraseña</label>
                  <input
                    id="pf-nueva"
                    type="password"
                    className="form-control"
                    autoComplete="new-password"
                    value={contrasenaNueva}
                    onChange={(e) => setContrasenaNueva(e.target.value)}
                  />
                  <small className="text-muted">
                    Mínimo {LARGO_MINIMO_CONTRASENA} caracteres.
                  </small>
                </div>

                <div className="mb-3">
                  <label className="form-label" htmlFor="pf-confirmar">Confirmar nueva contraseña</label>
                  <input
                    id="pf-confirmar"
                    type="password"
                    className="form-control"
                    autoComplete="new-password"
                    value={confirmacion}
                    onChange={(e) => setConfirmacion(e.target.value)}
                  />
                </div>

                {errorClave && <div className="alert alert-danger mb-3">{errorClave}</div>}
                {exitoClave && <div className="alert alert-success mb-3">{exitoClave}</div>}

                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={cambiando}
                >
                  {cambiando ? "Cambiando..." : "Cambiar contraseña"}
                </button>
              </form>
            </div>

          </div>
        )}

      </main>
    </div>
  );
}
