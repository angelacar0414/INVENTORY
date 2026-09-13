import axios from "axios";
import type { IProveedor, IProveedorFormulario } from "../types/IProveedor";

// SERVICE (capa de servicios)
// -----------------------------
// Esta es la ÚNICA clase que habla directamente con la API del Backend.
// Ningún componente visual (página o formulario) llama a axios
// directamente: siempre pasa primero por aquí. Así, si el día de
// mañana cambia la URL del backend, solo se edita en un lugar.

const API_URL = "http://localhost:8080/api/v1/proveedores";

// Forma en la que el Backend envuelve TODAS sus respuestas
interface RespuestaApi<T> {
  success: boolean;
  message: string;
  data: T;
}

export const proveedorService = {
  // Consultar solo los proveedores activos
  async listar(): Promise<IProveedor[]> {
    const respuesta = await axios.get<RespuestaApi<IProveedor[]>>(`${API_URL}/activos`);
    return respuesta.data.data;
  },

  // Consultar TODOS los proveedores, incluyendo los inactivos
  async listarTodas(): Promise<IProveedor[]> {
    const respuesta = await axios.get<RespuestaApi<IProveedor[]>>(API_URL);
    return respuesta.data.data;
  },

  // Buscar proveedores por nombre (búsqueda parcial)
  async buscar(nombre: string): Promise<IProveedor[]> {
    const respuesta = await axios.get<RespuestaApi<IProveedor[]>>(
      `${API_URL}/buscar?nombre=${nombre}`
    );
    return respuesta.data.data;
  },

  // Consultar un solo proveedor por su id (para el formulario de edición)
  async buscarPorId(id: number): Promise<IProveedor> {
    const respuesta = await axios.get<RespuestaApi<IProveedor>>(`${API_URL}/${id}`);
    return respuesta.data.data;
  },

  // Registrar proveedor nuevo
  async crear(proveedor: IProveedorFormulario): Promise<IProveedor> {
    const respuesta = await axios.post<RespuestaApi<IProveedor>>(API_URL, proveedor);
    return respuesta.data.data;
  },

  // Editar proveedor existente
  async actualizar(id: number, proveedor: IProveedorFormulario): Promise<IProveedor> {
    const respuesta = await axios.put<RespuestaApi<IProveedor>>(`${API_URL}/${id}`, proveedor);
    return respuesta.data.data;
  },

  // Desactivar proveedor (eliminación lógica)
  async desactivar(id: number): Promise<void> {
    await axios.delete(`${API_URL}/${id}`);
  },

  // Volver a activar un proveedor que estaba inactivo
  async reactivar(id: number): Promise<void> {
    await axios.put(`${API_URL}/${id}/reactivar`);
  },
};