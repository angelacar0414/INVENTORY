import api from "../../services/api";
import type { ICategoria, ICategoriaFormulario } from "../types/ICategoria";

// SERVICE (capa de servicios)
// -----------------------------
// Aquí se realizan las peticiones al Backend.
// Las páginas y formularios utilizan este servicio.

export const categoriaService = {
  // RF-12: Consultar categorías activas.
  async listar(): Promise<ICategoria[]> {
    const respuesta = await api.get<RespuestaApi<ICategoria[]>>("/categorias");
    return respuesta.data.data;
  },

  // Consultar todas las categorías, incluyendo las inactivas.
  async listarTodas(): Promise<ICategoria[]> {
    const respuesta = await api.get<RespuestaApi<ICategoria[]>>("/categorias/todas");
    return respuesta.data.data;
  },

  // Consultar una categoría por su ID.
  async buscarPorId(id: number): Promise<ICategoria> {
    const respuesta = await api.get<RespuestaApi<ICategoria>>(`/categorias/${id}`);
    return respuesta.data.data;
  },

  // RF-9: Registrar una categoría nueva.
  async crear(categoria: ICategoriaFormulario): Promise<ICategoria> {
    const respuesta = await api.post<RespuestaApi<ICategoria>>("/categorias", categoria);
    return respuesta.data.data;
  },

  // RF-10: Editar una categoría existente.
  async actualizar(
    id: number,
    categoria: ICategoriaFormulario
  ): Promise<ICategoria> {
    const respuesta = await api.put<RespuestaApi<ICategoria>>(`/categorias/${id}`, categoria);
    return respuesta.data.data;
  },

  // RF-11: Desactivar una categoría.
  async desactivar(id: number): Promise<void> {
    await api.delete(`/categorias/${id}`);
  },

  // Reactivar una categoría.
  async reactivar(id: number): Promise<void> {
    await api.put(`/categorias/${id}/reactivar`, {});
  },
};

// Forma en la que el Backend envuelve sus respuestas.
interface RespuestaApi<T> {
  success: boolean;
  message: string;
  data: T;
}