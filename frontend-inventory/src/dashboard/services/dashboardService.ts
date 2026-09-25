import api from "../../services/api";
import { IDashboardResumen } from "../types/IDashboard";

// Usamos la misma instancia compartida "api" que ya usan Categorías,
// Proveedores y Clientes (con baseURL y withCredentials configurados una
// sola vez), para no repetir esa configuración en cada módulo nuevo.

interface RespuestaApi<T> {
  success: boolean;
  message: string;
  data: T;
}

export const dashboardService = {
  async obtenerResumen(): Promise<IDashboardResumen> {
    const respuesta = await api.get<RespuestaApi<IDashboardResumen>>("/dashboard");
    return respuesta.data.data;
  },
};
