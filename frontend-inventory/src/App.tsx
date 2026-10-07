import { Navigate, Route, Routes } from "react-router-dom";

/**
 * Páginas y componentes del módulo de autenticación.
 */
import { LoginPage } from "./auth/pages/LoginPage";
import { RegistroPage } from "./auth/pages/RegistroPage";
import { ProtectedRoute } from "./auth/pages/ProtectedRoute";

/**
 * Páginas del módulo Categorías.
 */
import { ListaCategorias } from "./categories/pages/ListaCategorias";
import { FormularioCategoria } from "./categories/pages/FormularioCategoria";

/**
 * Páginas del módulo Proveedores.
 */
import { ListaProveedores } from "./suppliers/pages/ListaProveedores";
import { FormularioProveedor } from "./suppliers/pages/FormularioProveedor";

/**
 * Páginas del módulo Clientes.
 */
import { ListaClientes } from "./clients/pages/ListaClientes";
import { FormularioCliente } from "./clients/pages/FormularioCliente";

/**
 * Páginas del módulo Usuarios.
 */
import ListaUsuarios from "./users/pages/ListaUsuarios";
import FormularioUsuario from "./users/pages/FormularioUsuario";

/**
 * Página principal del Dashboard.
 */
import { DashboardPage } from "./dashboard/pages/DashboardPage";

/**
 * Páginas del módulo Productos.
 *
 * ListaProductos:
 * muestra los productos registrados.
 *
 * FormularioProducto:
 * permite crear y editar productos.
 */
import { ListaProductos } from "./products/pages/ListaProductos";
import { FormularioProducto } from "./products/pages/FormularioProducto";

/**
 * COMPONENTE PRINCIPAL DE RUTAS
 * -----------------------------
 *
 * App.tsx define las diferentes rutas de navegación
 * de la aplicación INVENTORY.
 *
 * Las rutas que requieren autenticación están protegidas
 * mediante el componente ProtectedRoute.
 *
 * La seguridad real de las operaciones también se controla
 * en el Backend mediante Spring Security.
 */
function App() {
  return (
    <Routes>

      {/* =====================================================
          AUTENTICACIÓN
          ===================================================== */}

      /**
       * Página de inicio de sesión.
       *
       * Esta ruta es pública porque el usuario todavía
       * no ha iniciado sesión.
       */
      <Route
        path="/login"
        element={<LoginPage />}
      />

      /**
       * Página de registro.
       *
       * Esta ruta mantiene el comportamiento existente
       * del proyecto.
       */
      <Route
        path="/registro"
        element={<RegistroPage />}
      />


      {/* =====================================================
          DASHBOARD
          ===================================================== */}

      /**
       * Ruta principal de INVENTORY.
       *
       * Si el usuario está autenticado podrá acceder
       * al Dashboard.
       */
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        }
      />

      /**
       * Ruta directa al Dashboard.
       */
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        }
      />


      {/* =====================================================
          CATEGORÍAS
          ===================================================== */}

      /**
       * Lista de categorías.
       */
      <Route
        path="/categorias"
        element={
          <ProtectedRoute>
            <ListaCategorias />
          </ProtectedRoute>
        }
      />

      /**
       * Crear una categoría.
       */
      <Route
        path="/categorias/nueva"
        element={
          <ProtectedRoute>
            <FormularioCategoria />
          </ProtectedRoute>
        }
      />

      /**
       * Editar una categoría.
       *
       * El parámetro :id identifica la categoría
       * que se desea modificar.
       */
      <Route
        path="/categorias/editar/:id"
        element={
          <ProtectedRoute>
            <FormularioCategoria />
          </ProtectedRoute>
        }
      />


      {/* =====================================================
          PRODUCTOS
          ===================================================== */}

      /**
       * Lista principal de productos.
       *
       * Permite consultar, buscar, editar, desactivar
       * y reactivar productos según los permisos del usuario.
       */
      <Route
        path="/productos"
        element={
          <ProtectedRoute>
            <ListaProductos />
          </ProtectedRoute>
        }
      />

      /**
       * Formulario para registrar un producto nuevo.
       */
      <Route
        path="/productos/nuevo"
        element={
          <ProtectedRoute>
            <FormularioProducto />
          </ProtectedRoute>
        }
      />

      /**
       * Formulario para editar un producto existente.
       *
       * El parámetro :id corresponde al ID del producto.
       */
      <Route
        path="/productos/editar/:id"
        element={
          <ProtectedRoute>
            <FormularioProducto />
          </ProtectedRoute>
        }
      />


      {/* =====================================================
          PROVEEDORES
          ===================================================== */}

      /**
       * Lista de proveedores.
       */
      <Route
        path="/proveedores"
        element={
          <ProtectedRoute>
            <ListaProveedores />
          </ProtectedRoute>
        }
      />

      /**
       * Crear proveedor.
       */
      <Route
        path="/proveedores/nuevo"
        element={
          <ProtectedRoute>
            <FormularioProveedor />
          </ProtectedRoute>
        }
      />

      /**
       * Editar proveedor.
       */
      <Route
        path="/proveedores/editar/:id"
        element={
          <ProtectedRoute>
            <FormularioProveedor />
          </ProtectedRoute>
        }
      />


      {/* =====================================================
          CLIENTES
          ===================================================== */}

      /**
       * Lista de clientes.
       */
      <Route
        path="/clientes"
        element={
          <ProtectedRoute>
            <ListaClientes />
          </ProtectedRoute>
        }
      />

      /**
       * Crear cliente.
       */
      <Route
        path="/clientes/nuevo"
        element={
          <ProtectedRoute>
            <FormularioCliente />
          </ProtectedRoute>
        }
      />

      /**
       * Editar cliente.
       */
      <Route
        path="/clientes/editar/:id"
        element={
          <ProtectedRoute>
            <FormularioCliente />
          </ProtectedRoute>
        }
      />


      {/* =====================================================
          USUARIOS
          ===================================================== */}

      /**
       * Lista de usuarios.
       */
      <Route
        path="/usuarios"
        element={
          <ProtectedRoute>
            <ListaUsuarios />
          </ProtectedRoute>
        }
      />

      /**
       * Crear usuario.
       */
      <Route
        path="/usuarios/nuevo"
        element={
          <ProtectedRoute>
            <FormularioUsuario />
          </ProtectedRoute>
        }
      />

      /**
       * Editar usuario.
       */
      <Route
        path="/usuarios/editar/:id"
        element={
          <ProtectedRoute>
            <FormularioUsuario />
          </ProtectedRoute>
        }
      />


      {/* =====================================================
          RUTA POR DEFECTO
          ===================================================== */}

      /**
       * Si el usuario escribe una URL que no existe,
       * lo enviamos nuevamente al Dashboard.
       */
      <Route
        path="*"
        element={<Navigate to="/" replace />}
      />

    </Routes>
  );
}

/**
 * Exportamos App para que main.tsx pueda utilizar
 * este componente como punto principal de la aplicación.
 */
export default App;