import axios from "axios";

// Instancia compartida de axios para toda la aplicación.
// Configuramos aquí una sola vez lo que necesitan TODOS los módulos
// (la URL base del backend y que se manden las cookies de sesión),
// para no repetirlo en cada servicio y no volver a olvidar algo
// como pasó con proveedorService.
const api = axios.create({
  baseURL: "http://localhost:8080/api/v1",
  withCredentials: true,
});

export default api;