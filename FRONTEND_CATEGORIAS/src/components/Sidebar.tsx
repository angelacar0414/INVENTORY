import { Link, useLocation } from "react-router-dom";

// COMPONENTE REUTILIZABLE: Sidebar
// -----------------------------------
// Se usa en todas las páginas del sistema (por ahora solo Categorías
// está desarrollada, las demás son enlaces "de vitrina" para más
// adelante). useLocation() permite saber en qué página está el
// usuario, para resaltar la opción activa en azul, igual que en
// el prototipo de diseño.

export function Sidebar() {
  const ubicacion = useLocation();

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

  return (
    <div className="sidebar d-flex flex-column">
      <div className="logo">INVENTORY</div>

      {opciones.map((opcion) => (
        <Link
          key={opcion.ruta}
          to={opcion.ruta}
          className={
            "sidebar-link " +
            (ubicacion.pathname.startsWith(opcion.ruta) && opcion.ruta !== "/"
              ? "activo"
              : "")
          }
        >
          {opcion.texto}
        </Link>
      ))}

      <div className="cerrar-sesion mt-auto">Cerrar sesión</div>
    </div>
  );
}
