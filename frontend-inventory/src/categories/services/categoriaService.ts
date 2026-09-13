import axios from "axios";
import type { ICategoria, ICategoriaFormulario } from "../types/ICategoria";

// SERVICE (capa de servicios)
// -----------------------------
// Aquí se realizan las peticiones al Backend.
// Las páginas y formularios utilizan este servicio.

const API_URL = "http://localhost:8080/api/v1/categorias";

// Forma en la que el Backend envuelve sus respuestas.
interface RespuestaApi<T> {
  success: boolean;
  message: string;
  data: T;
}

export const categoriaService = {
  // RF-12: Consultar categorías activas.
  // withCredentials permite enviar la sesión HTTP al Backend.
  async listar(): Promise<ICategoria[]> {
    const respuesta = await axios.get<RespuestaApi<ICategoria[]>>(
      API_URL,
      { withCredentials: true }
    );

    return respuesta.data.data;
  },

  // Consultar todas las categorías, incluyendo las inactivas.
  async listarTodas(): Promise<ICategoria[]> {
    const respuesta = await axios.get<RespuestaApi<ICategoria[]>>(
      `${API_URL}/todas`,
      { withCredentials: true }
    );

    return respuesta.data.data;
  },

  // Consultar una categoría por su ID.
  async buscarPorId(id: number): Promise<ICategoria> {
    const respuesta = await axios.get<RespuestaApi<ICategoria>>(
      `${API_URL}/${id}`,
      { withCredentials: true }
    );

    return respuesta.data.data;
  },

  // RF-9: Registrar una categoría nueva.
  async crear(categoria: ICategoriaFormulario): Promise<ICategoria> {
    const respuesta = await axios.post<RespuestaApi<ICategoria>>(
      API_URL,
      categoria,
      { withCredentials: true }
    );

    return respuesta.data.data;
  },

  // RF-10: Editar una categoría existente.
  async actualizar(
    id: number,
    categoria: ICategoriaFormulario
  ): Promise<ICategoria> {
    const respuesta = await axios.put<RespuestaApi<ICategoria>>(
      `${API_URL}/${id}`,
      categoria,
      { withCredentials: true }
    );

    return respuesta.data.data;
  },

  // RF-11: Desactivar una categoría.
  async desactivar(id: number): Promise<void> {
    await axios.delete(
      `${API_URL}/${id}`,
      { withCredentials: true }
    );
  },

  // Reactivar una categoría.
  async reactivar(id: number): Promise<void> {
    await axios.put(
      `${API_URL}/${id}/reactivar`,
      {},
      { withCredentials: true }
    );
  },
};
