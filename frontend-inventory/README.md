# INVENTORY - FRONTEND (React + TypeScript)

**Aprendices:** Darío Bustamante y Ángela Carvajal Ortiz
**Ficha:** 3186706

Este es el proyecto único de React donde vive la interfaz visual de todos los módulos de INVENTORY. Está hecho con React 18 + TypeScript + Vite + Bootstrap 5 + Axios, y consume la API REST del Backend hecho en Spring Boot.

## Módulos incluidos

Módulo 

Categorías 
Proveedores EN PROCESO
Usuarios EN PROCESO
Clientes PENDIENTE
productos PENDIENTE
movimientos PENDIENTE
dashboard PENDIENTE
reportes PENDIENTE

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
│ └── Sidebar.tsx (menú lateral reutilizable)
├── categories/ (módulo Categorías)
│ ├── pages/
│ ├── services/
│ └── types/
└── suppliers/ (módulo Proveedores)
├── pages/
├── services/
└── types/


## Patrón que seguimos en todos los módulos

1. En `main.tsx` montamos la aplicación de React dentro del `<div id="root">` de `index.html`, y envolvemos todo en `BrowserRouter` para poder navegar entre páginas sin recargar el navegador.
2. Cada módulo define sus propias rutas dentro de `App.tsx`.
3. Cada página de listado usa `useEffect` para pedir los datos al Backend apenas se abre, llamando al `service` correspondiente de ese módulo.
4. Cada módulo tiene su propio archivo de `service`, que es la única pieza que usa Axios para hablar con el Backend — ningún componente visual llama a Axios directamente.
5. Al crear o editar, validamos los datos en el navegador (experiencia de usuario) y también dejamos que el Backend valide de nuevo (la validación real y definitiva).

## Cómo lo ejecutamos

### 1. Instalación de Node.js
Vamos a `https://nodejs.org` y descargamos la versión LTS. Instalación con los valores por defecto (Next, Next, Install).
Verificamos abriendo una terminal (`cmd` o Git Bash) y escribiendo:

node -v
npm -v

Debe mostrar números de versión, sin error.

### 2. Instalación de las dependencias del proyecto
Abrimos una terminal dentro de la carpeta `frontend-inventory` y ejecutamos:

npm install

Esto descarga React, Axios, Bootstrap, etc. (puede tardar 1-3 minutos).

### 3. Nos aseguramos de que el Backend esté corriendo
Este Frontend necesita que el proyecto `inventory-backend` (Spring Boot) esté corriendo en `http://localhost:8080` al mismo tiempo, porque ahí es donde pide los datos de cada módulo.

### 4. Ejecutamos el Frontend

npm run dev

Cuando termina, la terminal muestra una URL, normalmente:
http://localhost:5173


La abrimos en el navegador para ver la aplicación funcionando.

------------------------------------------------------------------------------

## MODULO 1: CATEGORIAS (FRONTEND)

La interfaz visual (Frontend) del módulo Categorías, hecha con React 18 +
TypeScript + Vite + Bootstrap 5 + Axios, siguiendo la arquitectura oficial
definida. Este Frontend consume la API REST del
Backend hecho en Spring Boot (evidencia AA1-EV01).

### Estructura

categories/
├── pages/
│ ├── ListaCategorias.tsx (tabla + buscador)
│ └── FormularioCategoria.tsx (crear/editar)
├── services/
│ └── categoriaService.ts (llamadas Axios al Backend)
└── types/
└── ICategoria.ts (interfaz TypeScript)


### Rutas

- `/categorias` — listado
- `/categorias/nueva` — formulario de creación
- `/categorias/editar/:id` — formulario de edición

### Detalles específicos

- `ListaCategorias.tsx` usa `useEffect` para pedir los datos al Backend apenas se abre la página, llamando a `categoriaService.listar()`.
- `categoriaService.ts` habla con `http://localhost:8080/api/v1/categorias`.

-----------------------------------------------------------------------------------------

## MODULO 2: USUARIOS (FRONTEND)
 

-----------------------------------------------------------------------------------------

## MODULO 3: PROVEDORES (FROTEND)























