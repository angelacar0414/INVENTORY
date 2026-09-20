import { useNavigate, useLocation, Link } from "react-router-dom";
import { authService } from "../auth/services/authService";

// COMPONENTE REUTILIZABLE: Sidebar
// -----------------------------------
// Se utiliza en las páginas internas del sistema.
// Permite navegar entre los módulos y cerrar la sesión
// mediante el endpoint de logout del backend.

export function Sidebar() {
  const ubicacion = useLocation();
  const navigate = useNavigate();

  // ---------------------------------------------------------
  // DATOS DEL USUARIO QUE INICIÓ SESIÓN
  // ---------------------------------------------------------
  // El LoginPage guardó estos dos valores en sessionStorage
  // apenas el backend confirmó el inicio de sesión. Los leemos
  // aquí para poder mostrarlos en pantalla (nombre de usuario
  // y su rol: Administrador u Operador), sin tener que volver
  // a preguntarle al backend quién es el usuario actual.
  const usuario = sessionStorage.getItem("usuario") || "";
  const rol = sessionStorage.getItem("rol") || "";

  const opciones = [
    { texto: "Dashboard", ruta: "/" },
    { texto: "Usuarios", ruta: "/usuarios" },
    { texto: "Categorías", ruta: "/categorias" },
    { texto: "Productos", ruta: "/productos" },
    { texto: "Proveedores", ruta: "/proveedores" },
    { texto: "Clientes", ruta: "/clientes" },
    { texto: "Movimientos", ruta: "/movimientos" },
    { texto: "Reportes", ruta: "/reportes" },
  ];

  async function cerrarSesion() {
    try {
      await authService.logout();
    } catch (error) {
      console.error("Error al cerrar sesión:", error);
    } finally {
      // Borramos tanto el usuario como el rol guardados en
      // sessionStorage. Si no borráramos el rol, el siguiente
      // usuario que inicie sesión podría ver por un instante
      // el rol del usuario anterior, antes de que cargue el suyo.
      sessionStorage.removeItem("usuario");
      sessionStorage.removeItem("rol");
      navigate("/login");
    }
  }

  return (
    <div className="sidebar d-flex flex-column">
      <div className="logo">INVENTORY</div>

      {/* ---------------------------------------------------
          BLOQUE DE USUARIO ACTUAL
          ---------------------------------------------------
          Muestra quién inició sesión y con qué rol, justo
          debajo del logo. Solo se muestra si de verdad hay
          un usuario guardado (por seguridad, en caso de que
          alguien entre a esta pantalla sin haber iniciado
          sesión correctamente).
          --------------------------------------------------- */}
      {usuario && (
        <div className="sidebar-usuario">
          <div className="sidebar-usuario-nombre">{usuario}</div>
          <div className="sidebar-usuario-rol">{rol}</div>
        </div>
      )}

      {opciones.map((opcion) => (
        <Link
          key={opcion.ruta}
          to={opcion.ruta}
          className={
            "sidebar-link " +
            (ubicacion.pathname.startsWith(opcion.ruta) &&
            opcion.ruta !== "/"
              ? "activo"
              : "")
          }
        >
          {opcion.texto}
        </Link>
      ))}

      <button
        type="button"
        onClick={cerrarSesion}
        className="cerrar-sesion mt-auto"
      >
        Cerrar sesión
      </button>
    </div>
  );
}