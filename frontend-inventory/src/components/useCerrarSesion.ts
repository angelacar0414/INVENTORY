import { useNavigate } from "react-router-dom";
import { authService } from "../auth/services/authService";
import { limpiarResumenPerfil } from "./resumenPerfil";

// ---------------------------------------------------------
// HOOK: cerrar sesión
// ---------------------------------------------------------
// Antes esta lógica estaba solo en el Sidebar. Ahora el cierre de
// sesión también se puede hacer desde el menú de la barra superior,
// así que lo dejamos en un solo lugar para no repetir el código.
// ---------------------------------------------------------

export function useCerrarSesion() {
  const navigate = useNavigate();

  return async function cerrarSesion() {
    try {
      await authService.logout();
    } catch (error) {
      console.error("Error al cerrar sesión:", error);
    } finally {
      // Borramos todo lo que guardamos de la sesión para que la
      // siguiente persona no vea datos del usuario anterior.
      sessionStorage.removeItem("usuario");
      sessionStorage.removeItem("rol");
      limpiarResumenPerfil();
      navigate("/login");
    }
  };
}
