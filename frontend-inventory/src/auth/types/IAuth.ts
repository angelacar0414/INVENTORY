/**
 * INTERFAZ: Login
 * ----------------
 * Representa los datos que el usuario debe enviar
 * al Backend para iniciar sesión.
 */
export interface ILogin {
  // Nombre de usuario utilizado para autenticarse.
  username: string;

  // Contraseña del usuario.
  contraseña: string;
}

/**
 * INTERFAZ: Registro
 * -------------------
 * Representa los datos necesarios para registrar
 * un nuevo usuario.
 */
export interface IRegistro {
  // Nombre del usuario.
  nombre: string;

  // Apellido del usuario.
  apellido: string;

  // Nombre de usuario para iniciar sesión.
  username: string;

  // Correo electrónico.
  email: string;

  // Contraseña del nuevo usuario.
  contraseña: string;
}

/**
 * INTERFAZ: Respuesta de autenticación
 * -------------------------------------
 * Representa la respuesta que devuelve el Backend
 * después de una operación de autenticación.
 *
 * Esta interfaz debe coincidir con los datos que
 * realmente utiliza LoginPage.tsx.
 */
export interface IRespuestaAuth {

  // Indica si la operación fue exitosa.
  success: boolean;

  // Mensaje enviado por el Backend.
  message: string;

  // Nombre de usuario autenticado.
  username?: string;

  // Rol del usuario autenticado.
  //
  // Este valor es utilizado por LoginPage.tsx
  // para almacenarlo en sessionStorage.
  //
  // Ejemplos:
  // - ADMINISTRADOR
  // - OPERADOR
  rol?: string;

  // Datos adicionales que pueda devolver
  // el Backend.
  data?: string;

  // Mensaje o información adicional de errores.
  errors?: string;
}