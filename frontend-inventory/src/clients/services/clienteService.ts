import api from "../../services/api";
import type { ICliente, IClienteFormulario } from "../types/ICliente";

// SERVICE (capa de servicios)
// -----------------------------
// Esta es la ÚNICA clase que habla directamente con la API del Backend.
// Ningún componente visual (página o formulario) llama a axios
// directamente: siempre pasa primero por aquí.

interface RespuestaApi<T> {
  success: boolean;
  message: string;
  data: T;
}

export const clienteService = {
  async listar(): Promise<ICliente[]> {
    const respuesta = await api.get<RespuestaApi<ICliente[]>>("/clientes/activos");
    return respuesta.data.data;
  },

  async listarTodas(): Promise<ICliente[]> {
    const respuesta = await api.get<RespuestaApi<ICliente[]>>("/clientes");
    return respuesta.data.data;
  },

  async buscar(nombre: string): Promise<ICliente[]> {
    const respuesta = await api.get<RespuestaApi<ICliente[]>>(
      `/clientes/buscar?nombre=${nombre}`
    );
    return respuesta.data.data;
  },

  async buscarPorId(id: number): Promise<ICliente> {
    const respuesta = await api.get<RespuestaApi<ICliente>>(`/clientes/${id}`);
    return respuesta.data.data;
  },

  async crear(cliente: IClienteFormulario): Promise<ICliente> {
    const respuesta = await api.post<RespuestaApi<ICliente>>("/clientes", cliente);
    return respuesta.data.data;
  },

  async actualizar(id: number, cliente: IClienteFormulario): Promise<ICliente> {
    const respuesta = await api.put<RespuestaApi<ICliente>>(`/clientes/${id}`, cliente);
    return respuesta.data.data;
  },

  async desactivar(id: number): Promise<void> {
    await api.delete(`/clientes/${id}`);
  },

  async reactivar(id: number): Promise<void> {
    await api.put(`/clientes/${id}/reactivar`);
  },
};
