# INVENTORY - Frontend del módulo Categorías (React + TypeScript)

**Aprendices:** Darío Bustamante y Ángela Carvajal Ortiz
**Ficha:** 3186706

## ¿Qué trae esta entrega?

La interfaz visual (Frontend) del módulo Categorías, hecha con React 18 +
TypeScript + Vite + Bootstrap 5 + Axios, siguiendo la arquitectura oficial
definida en el EKB del proyecto. Este Frontend consume la API REST del
Backend hecho en Spring Boot (evidencia AA1-EV01).

## Estructura del proyecto

```
FRONTEND_CATEGORIAS/
├── index.html
├── package.json
├── vite.config.ts
├── tsconfig.json
└── src/
    ├── main.tsx                 (punto de entrada)
    ├── App.tsx                  (rutas de la aplicación)
    ├── index.css                (estilos globales)
    ├── components/
    │   └── Sidebar.tsx           (menú lateral reutilizable)
    └── categories/
        ├── pages/
        │   ├── ListaCategorias.tsx      (tabla + buscador)
        │   └── FormularioCategoria.tsx  (crear/editar)
        ├── services/
        │   └── categoriaService.ts      (llamadas Axios al Backend)
        └── types/
            └── ICategoria.ts             (interfaz TypeScript)
```

## Cómo funciona 

1. `main.tsx` montamos la aplicación de React dentro del `<div id="root">`
   de `index.html`, y envuelve todo en `BrowserRouter` para poder navegar
   entre páginas sin recargar el navegador.
2. `App.tsx` define las rutas: `/categorias` (listado), `/categorias/nueva`
   y `/categorias/editar/:id` (formulario).
3. `ListaCategorias.tsx` usa `useEffect` para pedir los datos al Backend
   apenas se abre la página, llamando a `categoriaService.listar()`.
4. `categoriaService.ts` es la única pieza que usa Axios para hablar con
   `http://localhost:8080/api/v1/categorias` — ningún componente visual
   llama a Axios directamente.
5. Al crear o editar, `FormularioCategoria.tsx` validamos los datos en el
   navegador (experiencia de usuario) y también dejamos que el Backend
   valide de nuevo (la validación real y definitiva).

## Cómo lo ejecutamos

### 1. Instalación de Node.js 

Ve a `https://nodejs.org` y descarga la versión **LTS**. Instálacion con
los valores por defecto (Next, Next, Install).

Verificación de instalación abriendo una terminal (`cmd` o Git Bash) y
escribiendo:
```
node -v
npm -v
```
Debe mostrar números de versión, sin error.

### 2. Instalación las dependencias del proyecto

Abrimos una terminal **dentro de la carpeta** `FRONTEND_CATEGORIAS` y ejecutamos:
```
npm install
```
Esto descarga React, Axios, Bootstrap, etc. (puede tardar 1-3 minutos).

### 3. Nos aseguramos de que el Backend esté corriendo

Este Frontend necesita que el proyecto `inventory-backend` (Spring Boot)
esté corriendo en `http://localhost:8080` al mismo tiempo, porque ahí es
donde pide los datos de las categorías.

### 4. Ejecutamos el Frontend

```
npm run dev
```

Cuando terminamos, la terminal muestra una URL, normalmente:
```
http://localhost:5173
```

La abrimos en el navegador para ver la aplicación funcionando.

## Próximos módulos

Con esta misma estructura (`pages`, `components`, `services`, `types`) se
puede replicar para Proveedores, Clientes, Productos, etc.
