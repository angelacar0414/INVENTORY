/**
 * TIPOS DEL MÓDULO PERFIL
 * -----------------------
 * Deben coincidir con los DTO del Backend (paquete com.inventory.profile.dto).
 */

// Datos del perfil tal como los devuelve el Backend (ProfileDTO).
export interface IPerfil {
  idUsuario: number;
  nombre: string;
  apellido: string;
  username: string;
  email: string;
  rol: string;
  activo: boolean;
  fechaCreacion: string | null;
}

// Datos que el usuario puede modificar (ProfileUpdateDTO).
export interface IPerfilActualizar {
  nombre: string;
  apellido: string;
  email: string;
}

// Datos para cambiar la contraseña (PasswordChangeDTO).
export interface ICambioContrasena {
  contrasenaActual: string;
  contrasenaNueva: string;
  confirmacion: string;
}
