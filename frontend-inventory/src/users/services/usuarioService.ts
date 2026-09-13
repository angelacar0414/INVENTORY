import axios from "axios";
import {
  IUsuario,
  IUsuarioActualizar,
  IUsuarioCrear,
} from "../types/IUsuario";

// URL base de los endpoints de usuarios del backend.
const API_URL = "http://localhost:8080/api/v1/usuarios";

// Servicio encargado de comunicarse con el backend de usuarios.
const usuarioService = {

  // Obtiene todos los usuarios registrados.
  listar: async (): Promise<IUsuario[]> => {
    const respuesta = await axios.get<IUsuario[]>(API_URL, {
      withCredentials: true,
    });

    return respuesta.data;
  },

  // Obtiene un usuario específico por su ID.
  obtenerPorId: async (id: number): Promise<IUsuario> => {
    const respuesta = await axios.get<IUsuario>(`${API_URL}/${id}`, {
      withCredentials: true,
    });

    return respuesta.data;
  },

  // Registra un nuevo usuario.
  crear: async (usuario: IUsuarioCrear): Promise<IUsuario> => {
    const respuesta = await axios.post<IUsuario>(API_URL, usuario, {
      withCredentials: true,
    });

    return respuesta.data;
  },

  // Actualiza los datos de un usuario existente.
  actualizar: async (
    id: number,
    usuario: IUsuarioActualizar
  ): Promise<IUsuario> => {
    const respuesta = await axios.put<IUsuario>(
      `${API_URL}/${id}`,
      usuario,
      {
        withCredentials: true,
      }
    );

    return respuesta.data;
  },

  // Desactiva un usuario.
  desactivar: async (id: number): Promise<IUsuario> => {
    const respuesta = await axios.delete<IUsuario>(`${API_URL}/${id}`, {
      withCredentials: true,
    });

    return respuesta.data;
  },

  // Reactiva un usuario previamente desactivado.
  reactivar: async (id: number): Promise<IUsuario> => {
    const respuesta = await axios.put<IUsuario>(
      `${API_URL}/${id}/reactivar`,
      {},
      {
        withCredentials: true,
      }
    );

    return respuesta.data;
  },
};

export default usuarioService;