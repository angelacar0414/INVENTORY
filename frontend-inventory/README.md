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
Productos | IMPLEMENTADO
Movimientos | IMPLEMENTADO
Dashboard | IMPLEMENTADO
Reportes | IMPLEMENTADO
Perfil | IMPLEMENTADO

## Estructura del proyecto

```
frontend-inventory/
├── index.html
├── package.json
├── vite.config.ts
├── tsconfig.json
└── src/
    ├── main.tsx (punto de entrada)
    ├── App.tsx (rutas de la aplicación)
    ├── index.css (estilos globales y estilo visual compartido)
    ├── assets/
    │   └── logo-inventory.jpeg (logo completo, usado en el login)
    ├── services/
    │   └── api.ts (instancia de Axios con la URL base del Backend)
    ├── components/ (piezas reutilizables)
    │   ├── Sidebar.tsx (menú lateral con el logo y los módulos; también monta la barra superior)
    │   ├── Topbar.tsx (barra superior: saludo, buscador y menú del usuario)
    │   ├── resumenPerfil.ts (guarda el nombre del usuario para la barra superior)
    │   ├── useCerrarSesion.ts (cierre de sesión compartido por el menú lateral y la barra)
    │   ├── LogoInventory.tsx (logo de la caja con check en SVG)
    │   ├── IconoUsuario.tsx (icono de usuario del login en SVG)
    │   ├── Iconos.tsx (conjunto de iconos SVG de línea)
    │   ├── TarjetaKpi.tsx (tarjeta de indicador del Dashboard y Reportes)
    │   └── Paginacion.tsx (paginación reutilizable para las listas)
    ├── auth/ (Autenticación)
    ├── categories/ (Categorías)
    ├── suppliers/ (Proveedores)
    ├── clients/ (Clientes)
    ├── users/ (Usuarios)
    ├── products/ (Productos)
    ├── movements/ (Movimientos)
    ├── dashboard/ (Dashboard)
    ├── reports/ (Reportes)
    └── profile/ (Perfil)
```

Cada carpeta de módulo se organiza igual: `pages/` (pantallas), `services/` (llamadas Axios al Backend) y `types/` (interfaces TypeScript).

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
- `/categorias/nueva` — formulario de creación
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
- Pantalla de inicio de sesión dividida en dos partes: el logo de Inventory a la izquierda y el formulario sobre fondo azul a la derecha.
- Icono de usuario dibujado en SVG (`components/IconoUsuario.tsx`) en lugar de un emoji.
- Botón para mostrar u ocultar la contraseña mientras se escribe.

### Estructura

```
auth/
├── auth.css (estilos del login y del registro)
├── pages/
│   ├── LoginPage.tsx
│   ├── RegistroPage.tsx
│   └── ProtectedRoute.tsx
├── services/
│   └── authService.ts
└── types/
```

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

## MODULO 6: PRODUCTOS (FRONTEND)

Módulo terminado. Sigue el mismo patrón utilizado en los demás módulos:

- `products/types/IProducto.ts` — define la estructura de un Producto y del formulario de creación/edición.
- `products/services/productoService.ts` — contiene las peticiones mediante Axios al Backend.
- `products/pages/ListaProductos.tsx` — muestra la tabla de productos y permite realizar búsquedas y gestionar su estado.
- `products/pages/FormularioProducto.tsx` — permite crear y editar productos utilizando el mismo formulario.

El módulo se encuentra integrado con el Backend mediante la API REST correspondiente. Incluye validaciones de código único, stock mínimo y muestra el estado automático del producto (DISPONIBLE, AGOTADO, INACTIVO).

-----------------------------------------------------------------------------------------

## MODULO 7: MOVIMIENTOS (FRONTEND)

El módulo de Movimientos permite registrar entradas y salidas del inventario, manteniendo un historial permanente e inmutable.

Actualmente se encuentran implementadas las siguientes funcionalidades:

- Registrar entradas de inventario.
- Registrar salidas de inventario con asignación de cliente.
- Consultar el historial de entradas y salidas.
- Búsqueda por código o nombre de producto.
- Filtrado por tipo de movimiento (entrada o salida).
- Paginación del historial.
- Validación en tiempo real de stock disponible.

### Estructura

movements/
├── pages/
│   ├── ListaMovimientos.tsx (tabla + historial)
│   └── FormularioMovimiento.tsx (crear entrada o salida)
├── services/
│   └── movimientoService.ts (llamadas Axios al Backend)
└── types/
    └── IMovimiento.ts (interfaces TypeScript)

### Rutas

- `/movimientos` — historial de movimientos
- `/movimientos/entrada` — registrar entrada
- `/movimientos/salida` — registrar salida

### Detalles específicos

- `ListaMovimientos.tsx` utiliza `useEffect` para solicitar el historial al Backend y permite búsqueda y filtrado.
- `FormularioMovimiento.tsx` se utiliza tanto para registrar entradas como salidas, con un parámetro `tipo` que determina su comportamiento.
- Para salidas, se requiere seleccionar un cliente y se valida que el stock sea suficiente.
- El historial es permanente: no se puede editar ni eliminar movimientos registrados.
- Se muestra el stock antes y después de cada movimiento para verificar el impacto en el inventario.

-----------------------------------------------------------------------------------------

## MODULO 8: DASHBOARD (FRONTEND)

El módulo de Dashboard es la pantalla principal del sistema, la que se muestra apenas iniciamos sesión. A diferencia de los demás módulos, no tiene formulario de creación ni edición: solo muestra un resumen del estado del inventario.

Actualmente se encuentra implementado:

- Franja de alerta cuando hay productos con stock bajo o agotados.
- Cinco tarjetas de totales: productos, categorías, proveedores, clientes y movimientos.
- Actividad de hoy (entradas y salidas del día).
- Estado del inventario en una barra con productos disponibles, con stock bajo y agotados.
- Tabla de los últimos movimientos registrados, con etiquetas de ENTRADA y SALIDA y cantidades con signo.
- Accesos rápidos a registrar entrada, registrar salida, Productos y Reportes.
- Acceso disponible tanto para el rol ADMINISTRADOR como para el rol OPERADOR.

### Estructura

```
dashboard/
├── pages/
│   └── DashboardPage.tsx (pantalla principal)
├── services/
│   └── dashboardService.ts (llamadas Axios al Backend)
└── types/
    └── IDashboard.ts (interfaces TypeScript)
```

### Rutas

- `/dashboard` — pantalla principal, a la que se redirige automáticamente después de iniciar sesión.

### Detalles específicos

- `DashboardPage.tsx` utiliza `useEffect` para solicitar el resumen al Backend cuando se abre la página.
- `dashboardService.ts` realiza la petición mediante Axios a `http://localhost:8080/api/v1/dashboard`.
- Las tarjetas de totales usan el componente compartido `TarjetaKpi` y los iconos de `Iconos.tsx`. Cada tarjeta es un enlace que lleva al módulo correspondiente.
- La franja de alerta cambia de destino según el rol: el ADMINISTRADOR va a Reportes y el OPERADOR va a Productos.
- En los accesos rápidos, el botón de Reportes solo se muestra al ADMINISTRADOR.
- La barra de estado calcula los disponibles como el total de productos menos los de stock bajo y los agotados.
- Los totales se calculan en cada consulta, así que al volver al Dashboard después de registrar cambios en el inventario ya aparecen actualizados.

  -----------------------------------------------------

## MODULO 9: REPORTES (FRONTEND)

El módulo de Reportes permite consultar el inventario y sus movimientos con filtros, y descargarlos en PDF o Excel. Solo está disponible para el rol ADMINISTRADOR.

Actualmente se encuentra implementado:

- Reporte de productos con filtros por estado, proveedor, categoría y stock bajo.
- Reporte de movimientos con filtros por rango de fechas, tipo, producto y cliente.
- Indicadores resumen, tabla de detalle, búsqueda y paginación.
- Descarga en PDF (con gráficas) y en Excel con los filtros del último reporte generado.
- Indicadores con la misma tarjeta y los mismos iconos del Dashboard, para mantener una apariencia uniforme.

### Estructura

```
reports/
├── pages/
│   ├── ReportesPage.tsx (pestañas Productos y Movimientos)
│   ├── ReporteProductosTab.tsx
│   ├── ReporteMovimientosTab.tsx
│   └── ComponentesReporte.tsx (piezas compartidas: indicadores, formato de dinero y paginación)
├── services/
│   └── reporteService.ts (llamadas Axios y descarga de archivos)
└── types/
    └── IReporte.ts (interfaces TypeScript)
```

### Rutas

- `/reportes` — pantalla de reportes

-----------------------------------------------------------------------------------------

## MODULO 10: PERFIL (FRONTEND)

El módulo de Perfil permite que cada usuario consulte y actualice su información personal y cambie su contraseña. Está disponible para los roles ADMINISTRADOR y OPERADOR.

### Estructura

```
profile/
├── pages/
│   └── PerfilPage.tsx (datos personales y cambio de contraseña)
├── services/
│   └── perfilService.ts (llamadas Axios al Backend)
└── types/
    └── IPerfil.ts (interfaces TypeScript)
```

### Rutas

- `/perfil` — mi perfil (se abre desde el menú del usuario en la barra superior)

### Detalles específicos

- El username y el rol se muestran, pero no se pueden modificar.
- El cambio de contraseña pide la contraseña actual y una confirmación de la nueva (mínimo 6 caracteres).
- El Backend identifica al usuario por la sesión, por lo que nadie puede modificar el perfil de otra persona.

  -----------------------------------------------------


## COMPONENTES COMPARTIDOS E IDENTIDAD VISUAL

Para que todos los módulos se vean y se comporten igual, las piezas que se repiten viven en `src/components/` y los estilos comunes en `src/index.css`.

### Componentes

Componente | Para qué sirve
--- | ---
`Sidebar.tsx` | Menú lateral de todas las pantallas internas. Muestra el logo, los módulos y el botón para cerrar sesión. También monta la barra superior, para que aparezca en todos los módulos sin editar cada página.
`Topbar.tsx` | Barra superior fija con saludo, buscador de productos y menú del usuario (avatar con iniciales, nombre y rol; opciones "Mi perfil" y "Cerrar sesión").
`LogoInventory.tsx` | Logo de la caja con el check, dibujado en SVG. Se puede mostrar con o sin la palabra "Inventory" y en cualquier tamaño.
`IconoUsuario.tsx` | Icono de usuario (cabeza y hombros) que se muestra en el login.
`Iconos.tsx` | Conjunto de iconos SVG de línea (paquete, capas, camión, personas, intercambio, alerta, entrada, salida, gráfica, entre otros). Reemplazan a los emojis.
`TarjetaKpi.tsx` | Tarjeta de indicador con icono, título y valor. Se usa en el Dashboard y en Reportes y puede funcionar como enlace.
`Paginacion.tsx` | Hook `usePaginacion` y componente `Paginacion`, con 10 registros por página en las listas.

### Barra superior

La barra azul de arriba (`Topbar.tsx`) se muestra en todas las pantallas internas y tiene tres partes:

- **Saludo:** "Hola" con el nombre de pila del usuario.
- **Buscador de productos:** al escribir un código o nombre y presionar Enter, navega a `/productos?buscar=texto`. `ListaProductos.tsx` lee ese parámetro con `useSearchParams` y muestra directamente los resultados. Si el buscador se envía vacío, se muestra la lista completa.
- **Menú del usuario:** avatar con las iniciales, nombre completo y rol. Al hacer clic se abre un menú con "Mi perfil" y "Cerrar sesión". Se cierra al hacer clic afuera o al presionar Escape.

El nombre completo se consulta una sola vez a `GET /api/v1/perfil` y se guarda en `sessionStorage` (`resumenPerfil.ts`) para no repetir la petición en cada pantalla. Cuando el usuario edita sus datos en "Mi perfil", la página lanza un evento y la barra se actualiza al instante.

### Colores

Cada color tiene un significado y se usa igual en todo el sistema:

- Azul: acciones principales y entradas.
- Naranja: salidas y avisos.
- Verde: productos disponibles.
- Rojo: agotados y errores.
- Gris: información neutra.

### Estilos globales

`index.css` define el aspecto común de tarjetas, tablas, etiquetas de estado (tipo píldora), botones, campos de formulario y menú lateral. Si necesitamos cambiar el aspecto de todo el sistema, lo hacemos en ese archivo.

---

## Estado actual del Frontend

Actualmente se encuentran implementados y funcionales los módulos de:

- Autenticación
- Usuarios
- Categorías
- Proveedores
- Clientes
- Productos
- Movimientos
- Dashboard
- Reportes
- Perfil

Todas las listas de los módulos cuentan con paginación mediante el componente compartido `components/Paginacion.tsx`.

Todas las pantallas internas comparten el logo, los iconos SVG y el estilo visual descritos en la sección de componentes compartidos.
