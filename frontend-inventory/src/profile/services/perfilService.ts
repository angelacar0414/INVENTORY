import api from "../../services/api";
import type {
  ICambioContrasena,
  IPerfil,
  IPerfilActualizar,
} from "../types/IPerfil";

/**
 * SERVICE DE PERFIL
 * -----------------
 * Peticiones HTTP del módulo Perfil.
 *
 * Página → Servicio → API Backend
 *
 * No se envía ningún id: el Backend identifica al usuario por la sesión,
 * así que cada persona solo puede ver y modificar SU propio perfil.
 */

interface RespuestaApi<T> {
  success: boolean;
  message: string;
  data: T;
}

export const perfilService = {

  /** GET /api/v1/perfil: consulta el perfil del usuario con sesión activa. */
  async consultar(): Promise<IPerfil> {
    const respuesta = await api.get<RespuestaApi<IPerfil>>("/perfil");
    return respuesta.data.data;
  },

  /** PUT /api/v1/perfil: actualiza nombre, apellido y correo. */
  async actualizar(datos: IPerfilActualizar): Promise<IPerfil> {
    const respuesta = await api.put<RespuestaApi<IPerfil>>("/perfil", datos);
    return respuesta.data.data;
  },

  /** PUT /api/v1/perfil/contrasena: cambia la contraseña. */
  async cambiarContrasena(datos: ICambioContrasena): Promise<void> {
    await api.put("/perfil/contrasena", datos);
  },
};
