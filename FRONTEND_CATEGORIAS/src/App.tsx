import { Navigate, Route, Routes } from "react-router-dom";
import { ListaCategorias } from "./categories/pages/ListaCategorias";
import { FormularioCategoria } from "./categories/pages/FormularioCategoria";

// COMPONENTE RAÍZ: define hacia dónde navega la aplicación.
// -------------------------------------------------------------
// Por ahora, solo el módulo de Categorías está desarrollado, así
// que la ruta principal "/" redirige directamente ahí. Los demás
// módulos (Productos, Proveedores, etc.) se agregarán con esta
// misma estructura conforme se vayan completando.

function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/categorias" replace />} />
      <Route path="/categorias" element={<ListaCategorias />} />
      <Route path="/categorias/nueva" element={<FormularioCategoria />} />
      <Route path="/categorias/editar/:id" element={<FormularioCategoria />} />
    </Routes>
  );
}

export default App;
