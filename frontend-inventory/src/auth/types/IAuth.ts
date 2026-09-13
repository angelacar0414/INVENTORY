export interface ILogin {
  username: string;
  contraseña: string;
}

export interface IRegistro {
  nombre: string;
  apellido: string;
  username: string;
  email: string;
  contraseña: string;
}

export interface IRespuestaAuth {
  success: boolean;
  message: string;
  username?: string;
  data?: string;
  errors?: string;
}