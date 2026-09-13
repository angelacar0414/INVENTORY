import { Navigate, Route, Routes } from "react-router-dom";
import { ListaCategorias } from "./categories/pages/ListaCategorias";
import { FormularioCategoria } from "./categories/pages/FormularioCategoria";
import { ListaProveedores } from "./suppliers/pages/ListaProveedores"; // NUEVO
import { FormularioProveedor } from "./suppliers/pages/FormularioProveedor"; // NUEVO

// COMPONENTE RAÍZ: define hacia dónde navega la aplicación.
// -------------------------------------------------------------
// Por ahora, solo los módulos de Categorías y Proveedores están
// desarrollados, así que la ruta principal "/" redirige directamente
// a Categorías. Los demás módulos (Productos, Usuarios, etc.) se
// agregarán con esta misma estructura conforme se vayan completando.

function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/categorias" replace />} />
      <Route path="/categorias" element={<ListaCategorias />} />
      <Route path="/categorias/nueva" element={<FormularioCategoria />} />
      <Route path="/categorias/editar/:id" element={<FormularioCategoria />} />

      {/* NUEVO: rutas de Proveedores */}
      <Route path="/proveedores" element={<ListaProveedores />} />
      <Route path="/proveedores/nuevo" element={<FormularioProveedor />} />
      <Route path="/proveedores/editar/:id" element={<FormularioProveedor />} />
    </Routes>
  );
}

export default App;