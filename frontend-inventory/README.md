# INVENTORY - FRONTEND (React + TypeScript)

**Aprendices:** Darío Bustamante Camargo y Ángela Carvajal
**Ficha:** 3186706

Este es el proyecto único de React donde vive la interfaz visual de los módulos de INVENTORY. Está desarrollado con React 18 + TypeScript + Vite + Bootstrap 5 + Axios, y consume la API REST del Backend desarrollado en Spring Boot.

## Módulos incluidos

Módulo | Estado
--- | ---
Categorías | IMPLEMENTADO
Proveedores | IMPLEMENTADO
Usuarios | IMPLEMENTADO
Clientes | IMPLEMENTADO
Productos | PENDIENTE
Movimientos | PENDIENTE
Dashboard | IMPLEMENTADO
Reportes | PENDIENTE

## Estructura del proyecto

frontend-inventory/
├── index.html
├── package.json
├── vite.config.ts
├── tsconfig.json
└── src/
    ├── main.tsx (punto de entrada)
    ├── App.tsx (rutas de la aplicación)
    ├── index.css (estilos globales)
    ├── components/
    │   └── Sidebar.tsx (menú lateral reutilizable)
    ├── auth/ (módulo de Autenticación)
    │   ├── pages/
    │   ├── services/
    │   └── types/
    ├── categories/ (módulo Categorías)
    │   ├── pages/
    │   ├── services/
    │   └── types/
    ├── suppliers/ (módulo Proveedores)
    │   ├── pages/
    │   ├── services/
    │   └── types/
    └── users/ (módulo Usuarios)
        ├── pages/
        ├── services/
        └── types/

## Patrón que seguimos en todos los módulos

1. En `main.tsx` montamos la aplicación de React dentro del `<div id="root">` de `index.html`, y envolvemos todo en `BrowserRouter` para poder navegar entre páginas sin recargar el navegador.
2. Cada módulo define sus propias rutas dentro de `App.tsx`.
3. Cada página de listado utiliza `useEffect` para solicitar los datos al Backend cuando se abre, llamando al `service` correspondiente del módulo.
4. Cada módulo tiene su propio archivo de `service`, que es la pieza encargada de utilizar Axios para comunicarse con el Backend.
5. Al crear o editar, se realiza una validación básica de los datos en el navegador para mejorar la experiencia del usuario y el Backend realiza nuevamente la validación correspondiente.
6. Las rutas que requieren autenticación están protegidas mediante `ProtectedRoute`.

## Cómo lo ejecutamos

### 1. Instalación de Node.js

Vamos a `https://nodejs.org` y descargamos la versión LTS.

Verificamos la instalación abriendo una terminal (`cmd`, PowerShell o Git Bash) y escribiendo:

node -v
npm -v

Debe mostrar los números de versión instalados, sin errores.

### 2. Instalación de las dependencias del proyecto

Abrimos una terminal dentro de la carpeta `frontend-inventory` y ejecutamos:

npm install

Esto descarga las dependencias necesarias para ejecutar el proyecto.

### 3. Nos aseguramos de que el Backend esté corriendo

Este Frontend necesita que el proyecto `inventory-backend` (Spring Boot) esté corriendo en:

http://localhost:8080

El Backend proporciona la API REST que utiliza el Frontend para consultar y administrar la información.

### 4. Ejecutamos el Frontend

Ejecutamos:

npm run dev

Cuando termina, la terminal muestra una URL, normalmente:

http://localhost:5173

Abrimos esta dirección en el navegador para utilizar la aplicación.

------------------------------------------------------------------------------

## MODULO 1: CATEGORIAS (FRONTEND)

La interfaz visual (Frontend) del módulo Categorías está desarrollada con React 18 + TypeScript + Vite + Bootstrap 5 + Axios, siguiendo la arquitectura definida para el proyecto.

Este módulo consume la API REST del Backend desarrollado en Spring Boot.

### Estructura

categories/
├── pages/
│   ├── ListaCategorias.tsx (tabla + buscador)
│   └── FormularioCategoria.tsx (crear/editar)
├── services/
│   └── categoriaService.ts (llamadas Axios al Backend)
└── types/
    └── ICategoria.ts (interfaz TypeScript)

### Rutas

- `/categorias` — listado
- `/categorias/nuevo` — formulario de creación
- `/categorias/editar/:id` — formulario de edición

### Detalles específicos

- `ListaCategorias.tsx` utiliza `useEffect` para solicitar los datos al Backend cuando se abre la página.
- `categoriaService.ts` realiza las peticiones mediante Axios a `http://localhost:8080/api/v1/categorias`.
- El módulo permite crear, editar, desactivar y reactivar categorías.

-----------------------------------------------------------------------------------------

## MODULO 2: USUARIOS (FRONTEND)

El módulo de Usuarios permite administrar los usuarios registrados en el sistema desde la interfaz web.

Actualmente se encuentran implementadas las siguientes funcionalidades:

- Listado de usuarios.
- Búsqueda de usuarios.
- Creación de usuarios.
- Edición de usuarios.
- Desactivación de usuarios.
- Reactivación de usuarios.
- Visualización del rol.
- Visualización del estado del usuario.

### Estructura

users/
├── pages/
│   ├── ListaUsuarios.tsx (tabla + buscador)
│   └── FormularioUsuario.tsx (crear/editar)
├── services/
│   └── usuarioService.ts (llamadas Axios al Backend)
└── types/
    └── IUsuario.ts (interfaces TypeScript)

### Rutas

- `/usuarios` — listado
- `/usuarios/nuevo` — formulario de creación
- `/usuarios/editar/:id` — formulario de edición

### Detalles específicos

- `ListaUsuarios.tsx` muestra los usuarios registrados y permite realizar búsquedas.
- `FormularioUsuario.tsx` se utiliza tanto para crear como para editar usuarios.
- `usuarioService.ts` contiene las peticiones HTTP relacionadas con el módulo.
- El acceso al módulo está protegido por autenticación y el Backend restringe la gestión de usuarios al rol `ADMINISTRADOR`.

-----------------------------------------------------------------------------------------

## MODULO 3: PROVEEDORES (FRONTEND)

Módulo terminado. Sigue el mismo patrón utilizado en los demás módulos:

- `suppliers/types/IProveedor.ts` — define la estructura de un Proveedor y del formulario de creación/edición.
- `suppliers/services/proveedorService.ts` — contiene las peticiones mediante Axios al Backend.
- `suppliers/pages/ListaProveedores.tsx` — muestra la tabla de proveedores y permite realizar búsquedas y gestionar su estado.
- `suppliers/pages/FormularioProveedor.tsx` — permite crear y editar proveedores utilizando el mismo formulario.

El módulo se encuentra integrado con el Backend mediante la API REST correspondiente.

-----------------------------------------------------------------------------------------

## MODULO 4: AUTENTICACION (FRONTEND)

El módulo de Autenticación permite iniciar y cerrar sesión en la aplicación y controlar el acceso a las rutas protegidas.

Actualmente se encuentra implementado:

- Inicio de sesión mediante username y contraseña.
- Registro de usuarios.
- Cierre de sesión.
- Manejo de sesión HTTP.
- Protección de rutas mediante `ProtectedRoute`.

### Estructura

auth/
├── pages/
│   ├── LoginPage.tsx
│   ├── RegistroPage.tsx
│   └── ProtectedRoute.tsx
├── services/
│   └── authService.ts
└── types/

### Rutas

- `/login` — inicio de sesión
- `/registro` — registro de usuario

Las rutas que requieren autenticación utilizan `ProtectedRoute`. Cuando no existe una sesión activa, el usuario es redirigido a `/login`.

-----------------------------------------------------------------------------------------
## MODULO 5: CLIENTES (FRONTEND)
Módulo terminado. Sigue el mismo patrón utilizado en los demás módulos:

clients/types/ICliente.ts — define la estructura de un Cliente y del formulario de creación/edición.
clients/services/clienteService.ts — contiene las peticiones mediante Axios al Backend.
clients/pages/ListaClientes.tsx — muestra la tabla de clientes y permite realizar búsquedas y gestionar su estado.
clients/pages/FormularioCliente.tsx — permite crear y editar clientes utilizando el mismo formulario.

-----------------------------------------------------------------------------------------

## MODULO 6: DASHBOARD (FRONTEND)

El módulo de Dashboard es la pantalla principal del sistema, la que se muestra apenas iniciamos sesión. A diferencia de los demás módulos, no tiene formulario de creación ni edición: solo muestra un resumen del estado del inventario.

Actualmente se encuentra implementado:

- Consulta de indicadores generales (productos totales, stock bajo, agotados, entradas del día, salidas del día).
- Listado de los últimos movimientos registrados.
- Acceso disponible tanto para el rol ADMINISTRADOR como para el rol OPERADOR.

### Estructura

dashboard/
├── pages/
│   └── DashboardPage.tsx (pantalla principal)
├── services/
│   └── dashboardService.ts (llamadas Axios al Backend)
└── types/
    └── IDashboard.ts (interfaces TypeScript)

### Rutas

- `/dashboard` — pantalla principal, a la que se redirige automáticamente después de iniciar sesión.

### Detalles específicos

- `DashboardPage.tsx` utiliza `useEffect` para solicitar el resumen al Backend cuando se abre la página.
- `dashboardService.ts` realiza la petición mediante Axios a `http://localhost:8080/api/v1/dashboard`.
- Mientras los módulos de Productos y Movimientos no estén desarrollados, los indicadores relacionados con estos se muestran en cero, ya que todavía no existe información que consultar.

  -----------------------------------------------------


## Estado actual del Frontend

Actualmente se encuentran implementados y funcionales los módulos de:

- Autenticación
- Usuarios
- Categorías
- Proveedores
- Clientes
- Dashboard

Los módulos de Productos, Movimientos y Reportes serán desarrollados posteriormente de acuerdo con los requisitos y el alcance definido para INVENTORY.
