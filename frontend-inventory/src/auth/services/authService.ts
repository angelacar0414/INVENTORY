import axios from "axios";
import type {
  ILogin,
  IRegistro,
  IRespuestaAuth,
} from "../types/IAuth";

const API_URL = "http://localhost:8080/api/v1/auth";

export const authService = {

  async login(datos: ILogin): Promise<IRespuestaAuth> {
    const respuesta = await axios.post<IRespuestaAuth>(
      `${API_URL}/login`,
      datos,
      {
        withCredentials: true,
      }
    );

    return respuesta.data;
  },

  async registrar(datos: IRegistro): Promise<IRespuestaAuth> {
    const respuesta = await axios.post<IRespuestaAuth>(
      `${API_URL}/registrar`,
      datos,
      {
        withCredentials: true,
      }
    );

    return respuesta.data;
  },

  async logout(): Promise<IRespuestaAuth> {
    const respuesta = await axios.post<IRespuestaAuth>(
      `${API_URL}/logout`,
      {},
      {
        withCredentials: true,
      }
    );

    return respuesta.data;
  },
};