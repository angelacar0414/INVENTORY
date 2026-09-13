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
      sessionStorage.removeItem("usuario");
      navigate("/login");
    }
  }

  return (
    <div className="sidebar d-flex flex-column">
      <div className="logo">INVENTORY</div>

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