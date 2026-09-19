import api from "../../services/api";
import type { IProveedor, IProveedorFormulario } from "../types/IProveedor";

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

export const proveedorService = {
  async listar(): Promise<IProveedor[]> {
    const respuesta = await api.get<RespuestaApi<IProveedor[]>>("/proveedores/activos");
    return respuesta.data.data;
  },

  async listarTodas(): Promise<IProveedor[]> {
    const respuesta = await api.get<RespuestaApi<IProveedor[]>>("/proveedores");
    return respuesta.data.data;
  },

  async buscar(nombre: string): Promise<IProveedor[]> {
    const respuesta = await api.get<RespuestaApi<IProveedor[]>>(
      `/proveedores/buscar?nombre=${nombre}`
    );
    return respuesta.data.data;
  },

  async buscarPorId(id: number): Promise<IProveedor> {
    const respuesta = await api.get<RespuestaApi<IProveedor>>(`/proveedores/${id}`);
    return respuesta.data.data;
  },

  async crear(proveedor: IProveedorFormulario): Promise<IProveedor> {
    const respuesta = await api.post<RespuestaApi<IProveedor>>("/proveedores", proveedor);
    return respuesta.data.data;
  },

  async actualizar(id: number, proveedor: IProveedorFormulario): Promise<IProveedor> {
    const respuesta = await api.put<RespuestaApi<IProveedor>>(`/proveedores/${id}`, proveedor);
    return respuesta.data.data;
  },

  async desactivar(id: number): Promise<void> {
    await api.delete(`/proveedores/${id}`);
  },

  async reactivar(id: number): Promise<void> {
    await api.put(`/proveedores/${id}/reactivar`);
  },
};