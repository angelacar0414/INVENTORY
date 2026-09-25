import { useState } from "react";
import type { FormEvent } from "react";
import { useNavigate } from "react-router-dom";

import { authService } from "../services/authService";

// Importamos el logo de INVENTORY desde la carpeta assets.
// La ruta parte desde pages/ y sube a auth/ y luego a src/.
import logoInventory from "../../assets/logo-inventory.jpeg";

// Archivo CSS específico para darle el diseño visual
// a la pantalla de inicio de sesión.
import "../auth.css";

// ---------------------------------------------------------
// PÁGINA DE INICIO DE SESIÓN
// ---------------------------------------------------------
// Esta página permite que un usuario registrado ingrese
// al sistema utilizando su nombre de usuario y contraseña.
//
// La autenticación se realiza mediante el backend de
// Spring Boot, utilizando sesiones HTTP.
// ---------------------------------------------------------

export function LoginPage() {
  // useNavigate permite cambiar de página desde React.
  // Lo utilizaremos para llevar al usuario al sistema
  // después de iniciar sesión o a la página de registro.
  const navigate = useNavigate();

  // -------------------------------------------------------
  // ESTADOS DEL FORMULARIO
  // -------------------------------------------------------

  // Guarda el nombre de usuario que escribe la persona.
  const [username, setUsername] = useState("");

  // Guarda la contraseña que escribe la persona.
  const [contraseña, setContraseña] = useState("");

  // Guarda el mensaje de error que se mostrará al usuario.
  const [mensajeError, setMensajeError] = useState("");

  // Indica si actualmente se está realizando el proceso
  // de inicio de sesión.
  const [cargando, setCargando] = useState(false);

  // Agregué este estado para poder mostrar u ocultar la
  // contraseña mientras la escribo, y así confirmar que
  // no me equivoqué antes de dar clic en "Accede".
  // Empieza en false porque por seguridad debe iniciar oculta.
  const [mostrarContraseña, setMostrarContraseña] = useState(false);

  // -------------------------------------------------------
  // FUNCIÓN PARA REALIZAR EL LOGIN
  // -------------------------------------------------------
  // Esta función se ejecuta cuando el usuario envía
  // el formulario.
  // -------------------------------------------------------

  async function manejarLogin(e: FormEvent) {
    // Evitamos que el navegador recargue la página
    // automáticamente al enviar el formulario.
    e.preventDefault();

    // Limpiamos cualquier mensaje de error anterior.
    setMensajeError("");

    // -----------------------------------------------------
    // VALIDACIÓN BÁSICA
    // -----------------------------------------------------
    // Verificamos que el usuario haya escrito ambos campos.
    // La validación definitiva también se realiza en
    // el backend.
    // -----------------------------------------------------

    if (!username.trim() || !contraseña.trim()) {
      setMensajeError("Usuario y contraseña son obligatorios.");
      return;
    }

    try {
      // Indicamos que el proceso de login está en curso.
      // Esto permite cambiar el texto del botón.
      setCargando(true);

      // Enviamos las credenciales al backend mediante
      // nuestro servicio de autenticación.
      //
      // El backend espera:
      // {
      //   username: "...",
      //   contraseña: "..."
      // }
      const respuesta = await authService.login({
        username: username.trim(),
        contraseña,
      });

      // ---------------------------------------------------
      // LOGIN EXITOSO
      // ---------------------------------------------------

      if (respuesta.success) {
        // Guardamos temporalmente el nombre del usuario
        // para que ProtectedRoute pueda comprobar que existe
        // una sesión iniciada en el frontend.
        //
        // La sesión real es administrada por el backend
        // mediante la sesión HTTP.
        sessionStorage.setItem(
          "usuario",
          respuesta.username || username
        );

        // El backend ahora también nos devuelve el rol del
        // usuario (Administrador u Operador) dentro de la
        // respuesta del login. Lo guardamos aquí para poder
        // mostrarlo después en el Sidebar, sin tener que
        // pedírselo de nuevo al backend en cada pantalla.
        sessionStorage.setItem("rol", respuesta.rol || "");

        // Después de iniciar sesión enviamos al usuario
        // al Dashboard, que ahora es la pantalla principal
        // del sistema.
        navigate("/dashboard");
      } else {
        // Si el backend responde que la autenticación
        // no fue satisfactoria, mostramos su mensaje.
        setMensajeError(respuesta.message);
      }
    } catch (error: any) {
      // ---------------------------------------------------
      // MANEJO DE ERRORES
      // ---------------------------------------------------
      // Si ocurre un error en la comunicación con el
      // backend, mostramos el mensaje enviado por el
      // servidor cuando esté disponible.
      // ---------------------------------------------------

      setMensajeError(
        error.response?.data?.message ||
          "Usuario o contraseña incorrectos."
      );
    } finally {
      // Independientemente de si el login fue exitoso
      // o falló, terminamos el estado de carga.
      setCargando(false);
    }
  }

  // -------------------------------------------------------
  // INTERFAZ VISUAL
  // -------------------------------------------------------

  return (
    <div className="auth-page">

      {/* -------------------------------------------------
          PARTE IZQUIERDA
          -------------------------------------------------
          Contiene el logotipo de INVENTORY.
          ------------------------------------------------- */}

      <div className="auth-brand">
        <div className="auth-brand-content">

          {/* Logo real del proyecto INVENTORY */}
          <img
            src={logoInventory}
            alt="Logo de Inventory"
            className="auth-logo"
          />

        </div>
      </div>

      {/* -------------------------------------------------
          PARTE DERECHA
          -------------------------------------------------
          Contiene el formulario de inicio de sesión.
          ------------------------------------------------- */}

      <div className="auth-panel">
        <div className="auth-card">

          {/* ------------------------------------------------
              ICONO DE USUARIO
              ------------------------------------------------
              Por ahora utilizamos un emoji como icono.
              Más adelante podemos reemplazarlo por un
              icono de Bootstrap si queremos una apariencia
              más profesional.
              ------------------------------------------------ */}

          <div className="auth-user-icon">
            👤
          </div>

          {/* Título principal */}
          <h2 className="auth-title">
            Accede
          </h2>

          {/* Texto descriptivo */}
          <p className="auth-subtitle">
            Inicia sesión para continuar
          </p>

          {/* ------------------------------------------------
              MENSAJE DE ERROR
              ------------------------------------------------
              Solo se muestra cuando existe un error.
              ------------------------------------------------ */}

          {mensajeError && (
            <div className="auth-error">
              {mensajeError}
            </div>
          )}

          {/* ------------------------------------------------
              FORMULARIO
              ------------------------------------------------ */}

          <form onSubmit={manejarLogin}>

            {/* Campo de usuario */}
            <label className="auth-label">
              ESCRIBE TU USUARIO
            </label>

            <input
              type="text"
              className="auth-input"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Ingrese su usuario"
              autoComplete="username"
            />

            {/* Campo de contraseña */}
            <label className="auth-label">
              ESCRIBE TU CONTRASEÑA
            </label>

            {/*
              Envolví el input de contraseña en un div con
              position: relative, para poder colocar el botón
              del ojito flotando "dentro" del campo (position
              absolute), sin necesidad de tocar auth.css.
              El paddingRight del input evita que el texto
              escrito quede tapado por el ícono.
            */}
            <div style={{ position: "relative" }}>
              <input
                type={mostrarContraseña ? "text" : "password"}
                className="auth-input"
                style={{ paddingRight: "2.5rem" }}
                value={contraseña}
                onChange={(e) => setContraseña(e.target.value)}
                placeholder="Ingrese su contraseña"
                autoComplete="current-password"
              />
              <button
                type="button"
                onClick={() => setMostrarContraseña(!mostrarContraseña)}
                tabIndex={-1}
                style={{
                  position: "absolute",
                  right: "0.75rem",
                  top: "50%",
                  transform: "translateY(-50%)",
                  background: "none",
                  border: "none",
                  padding: 0,
                  color: "#6c757d",
                  cursor: "pointer",
                }}
              >
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

            {/* ------------------------------------------------
                BOTÓN DE INICIO DE SESIÓN
                ------------------------------------------------
                Mientras se realiza la petición al backend,
                el botón queda deshabilitado.
                ------------------------------------------------ */}

            <button
              type="submit"
              className="auth-button"
              disabled={cargando}
            >
              {cargando ? "Ingresando..." : "Accede"}
            </button>

          </form>

          {/* ------------------------------------------------
              ENLACE PARA REGISTRO
              ------------------------------------------------
              Lleva al usuario a la página de registro.
              ------------------------------------------------ */}

          <button
            type="button"
            className="auth-link"
            onClick={() => navigate("/registro")}
            style={{
              background: "none",
              border: "none",
              width: "100%",
            }}
          >
            ¿No tienes una cuenta? Regístrate
          </button>

        </div>
      </div>

    </div>
  );
}