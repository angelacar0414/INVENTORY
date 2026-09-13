// Representa los datos de un usuario recibidos desde el backend.
export interface IUsuario {
  idUsuario: number;
  nombre: string;
  apellido: string;
  username: string;
  email: string;
  rol: string;
  activo: boolean;
}

// Representa los datos necesarios para registrar un nuevo usuario.
export interface IUsuarioCrear {
  nombre: string;
  apellido: string;
  username: string;
  email: string;
  contraseña: string;
  rol: string;
}

// Representa los datos que se pueden modificar de un usuario existente.
export interface IUsuarioActualizar {
  nombre: string;
  apellido: string;
  username: string;
  email: string;
  rol: string;
}