// ---------------------------------------------------------
// RESUMEN DEL PERFIL (para la barra superior)
// ---------------------------------------------------------
// La barra superior muestra el nombre completo y las iniciales
// del usuario. Ese dato vive en el Backend (GET /perfil), pero
// como la barra se dibuja en todas las pantallas, lo guardamos
// en sessionStorage para no pedirlo al servidor en cada cambio
// de página.
//
// Guardamos también el username: si por algún motivo entra otra
// persona en el mismo navegador, el dato anterior no se usa.
// ---------------------------------------------------------

export interface IResumenPerfil {
  username: string;
  nombre: string;
  apellido: string;
  email: string;
}

const CLAVE = "perfilResumen";

// Nombre del evento que avisa a la barra superior que el perfil cambió.
export const EVENTO_PERFIL = "perfil:actualizado";

export function leerResumenPerfil(username: string): IResumenPerfil | null {
  try {
    const texto = sessionStorage.getItem(CLAVE);
    if (!texto) return null;
    const dato = JSON.parse(texto) as IResumenPerfil;
    return dato.username === username ? dato : null;
  } catch {
    return null;
  }
}

// Guarda el resumen y avisa a la barra superior para que se actualice
// sin recargar la página (por ejemplo, después de editar el perfil).
export function guardarResumenPerfil(resumen: IResumenPerfil): void {
  try {
    sessionStorage.setItem(CLAVE, JSON.stringify(resumen));
  } catch {
    // Si el navegador no deja guardar, la barra igual se actualiza por el evento.
  }
  window.dispatchEvent(new CustomEvent(EVENTO_PERFIL, { detail: resumen }));
}

export function limpiarResumenPerfil(): void {
  try {
    sessionStorage.removeItem(CLAVE);
  } catch {
    // Sin acción: no es crítico.
  }
}

// Iniciales para el avatar: primera letra del nombre y del apellido.
// Si todavía no tenemos el nombre, usamos las dos primeras del username.
export function obtenerIniciales(resumen: IResumenPerfil | null, username: string): string {
  if (resumen && (resumen.nombre || resumen.apellido)) {
    const a = resumen.nombre.trim().charAt(0);
    const b = resumen.apellido.trim().charAt(0);
    return (a + b).toUpperCase() || "?";
  }
  return username.slice(0, 2).toUpperCase() || "?";
}
