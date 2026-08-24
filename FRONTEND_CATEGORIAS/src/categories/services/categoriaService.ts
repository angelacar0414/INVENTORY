import axios from "axios";
import type { ICategoria, ICategoriaFormulario } from "../types/ICategoria";

// SERVICE (capa de servicios)
// -----------------------------
// Esta es la ÚNICA clase que habla directamente con la API del Backend.
// Ningún componente visual (página o formulario) llama a axios
// directamente: siempre pasa primero por aquí. Así, si el día de
// mañana cambia la URL del backend, solo se edita en un lugar.

const API_URL = "http://localhost:8080/api/v1/categorias";

// Forma en la que el Backend envuelve TODAS sus respuestas
// (ver Documento 14 - API REST del proyecto)
interface RespuestaApi<T> {
  success: boolean;
  message: string;
  data: T;
}

export const categoriaService = {
  // RF-12: Consultar categorías activas
  async listar(): Promise<ICategoria[]> {
    const respuesta = await axios.get<RespuestaApi<ICategoria[]>>(API_URL);
    return respuesta.data.data;
  },

  // Consultar TODAS las categorías, incluyendo las inactivas
async listarTodas(): Promise<ICategoria[]> {
  const respuesta = await axios.get<RespuestaApi<ICategoria[]>>(`${API_URL}/todas`);
  return respuesta.data.data;
},

  // Consultar una sola categoría por su id (para el formulario de edición)
  async buscarPorId(id: number): Promise<ICategoria> {
    const respuesta = await axios.get<RespuestaApi<ICategoria>>(`${API_URL}/${id}`);
    return respuesta.data.data;
  },

  // RF-9: Registrar categoría nueva
  async crear(categoria: ICategoriaFormulario): Promise<ICategoria> {
    const respuesta = await axios.post<RespuestaApi<ICategoria>>(API_URL, categoria);
    return respuesta.data.data;
  },

  // RF-10: Editar categoría existente
  async actualizar(id: number, categoria: ICategoriaFormulario): Promise<ICategoria> {
    const respuesta = await axios.put<RespuestaApi<ICategoria>>(`${API_URL}/${id}`, categoria);
    return respuesta.data.data;
  },

  // RF-11: Desactivar categoría (eliminación lógica)
  async desactivar(id: number): Promise<void> {
    await axios.delete(`${API_URL}/${id}`);
  },

  // Volver a activar una categoría que estaba inactiva
async reactivar(id: number): Promise<void> {
  await axios.put(`${API_URL}/${id}/reactivar`);
},
};
