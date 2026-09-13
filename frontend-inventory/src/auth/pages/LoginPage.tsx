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

        // Después de iniciar sesión enviamos al usuario
        // al módulo de categorías, que actualmente es
        // uno de los módulos desarrollados.
        navigate("/categorias");
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

            <input
              type="password"
              className="auth-input"
              value={contraseña}
              onChange={(e) => setContraseña(e.target.value)}
              placeholder="Ingrese su contraseña"
              autoComplete="current-password"
            />

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