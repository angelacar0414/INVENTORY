import { Link, useLocation } from "react-router-dom";
import { LogoInventory } from "./LogoInventory";
import { Topbar } from "./Topbar";
import { useCerrarSesion } from "./useCerrarSesion";

// COMPONENTE REUTILIZABLE: Sidebar
// -----------------------------------
// Se utiliza en las páginas internas del sistema.
// Permite navegar entre los módulos y cerrar la sesión.
//
// Como todas las pantallas internas ya incluyen el Sidebar, este
// componente también monta la barra superior (Topbar). Así la barra
// aparece en todos los módulos sin tener que editar cada página.
// La barra está fija en la parte de arriba (position: fixed) y el
// contenido de cada página se baja con CSS (ver index.css).
//
// El usuario y su rol ya no se muestran aquí: ahora están en la barra
// superior, junto con el acceso a "Mi perfil".

export function Sidebar() {
  const ubicacion = useLocation();
  const cerrarSesion = useCerrarSesion();

  const opciones = [
    { texto: "Dashboard", ruta: "/dashboard" },
    { texto: "Usuarios", ruta: "/usuarios" },
    { texto: "Categorías", ruta: "/categorias" },
    { texto: "Productos", ruta: "/productos" },
    { texto: "Proveedores", ruta: "/proveedores" },
    { texto: "Clientes", ruta: "/clientes" },
    { texto: "Movimientos", ruta: "/movimientos" },
    { texto: "Reportes", ruta: "/reportes" },
  ];

  return (
    <>
      <div className="sidebar d-flex flex-column">
        <div className="logo">
          <LogoInventory tamano={72} />
        </div>

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
      <Topbar />
    </>
  );
}