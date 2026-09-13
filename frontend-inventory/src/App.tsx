import { Navigate, Route, Routes } from "react-router-dom";

import { LoginPage } from "./auth/pages/LoginPage";
import { RegistroPage } from "./auth/pages/RegistroPage";
import { ProtectedRoute } from "./auth/pages/ProtectedRoute";

import { ListaCategorias } from "./categories/pages/ListaCategorias";
import { FormularioCategoria } from "./categories/pages/FormularioCategoria";

import { ListaProveedores } from "./suppliers/pages/ListaProveedores";
import { FormularioProveedor } from "./suppliers/pages/FormularioProveedor";

function App() {
  return (
    <Routes>

      {/* AUTH */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/registro" element={<RegistroPage />} />

      {/* INICIO */}
      <Route
        path="/"
        element={<Navigate to="/login" replace />}
      />

      {/* CATEGORÍAS */}
      <Route
        path="/categorias"
        element={
          <ProtectedRoute>
            <ListaCategorias />
          </ProtectedRoute>
        }
      />

      <Route
        path="/categorias/nueva"
        element={
          <ProtectedRoute>
            <FormularioCategoria />
          </ProtectedRoute>
        }
      />

      <Route
        path="/categorias/editar/:id"
        element={
          <ProtectedRoute>
            <FormularioCategoria />
          </ProtectedRoute>
        }
      />

      {/* PROVEEDORES */}
      <Route
        path="/proveedores"
        element={
          <ProtectedRoute>
            <ListaProveedores />
          </ProtectedRoute>
        }
      />

      <Route
        path="/proveedores/nuevo"
        element={
          <ProtectedRoute>
            <FormularioProveedor />
          </ProtectedRoute>
        }
      />

      <Route
        path="/proveedores/editar/:id"
        element={
          <ProtectedRoute>
            <FormularioProveedor />
          </ProtectedRoute>
        }
      />

    </Routes>
  );
}

export default App;