# INVENTORY - Sistema Web de Gestión de Inventarios

**Aprendiz:** Dario Bustamante Camargo  
**Aprendiz:** Ángela Carvajal   
**Ficha:** 3186706  
**Instructor:** Diana Galviz, Wilson Mosquera, Erika Parra

---

## Descripción del Proyecto

INVENTORY es un sistema web de gestión de inventarios desarrollado para micro y pequeñas empresas (MiPymes), con el objetivo de facilitar el control y administración de la información relacionada con el inventario.

El sistema está enfocado exclusivamente en la gestión y control de inventarios. No corresponde a un sistema ERP y no contempla dentro de su alcance módulos de ventas, facturación, contabilidad o compras.

---

## Módulos del Sistema

### Planificados
- Autenticación
- Usuarios
- Categorías
- Productos
- Proveedores
- Clientes
- Movimientos de inventario
- Dashboard y estadísticas
- Reportes
- Exportación de reportes a PDF y Excel

### Implementados ✅
- **Autenticación:** Inicio de sesión, registro y cierre de sesión con sesiones HTTP y BCrypt.
- **Usuarios:** CRUD de usuarios, desactivación/reactivación, búsqueda y control de roles.
- **Categorías:** Crear, consultar, actualizar y desactivar categorías de productos.
- **Proveedores:** Gestión completa de proveedores con búsqueda y cambio de estado.
- **Productos:** CRUD de productos, búsqueda, filtrado, control de estado automático según stock.
- **Movimientos de Inventario:** Registro de entradas y salidas, historial permanente e inmutable, validaciones de stock.
- **Clientes:** Gestión de clientes para registros de salida.
- **Dashboard:** Pantalla principal con alerta de reposición, totales (productos, categorías, proveedores, clientes y movimientos), actividad del día, estado del inventario, últimos movimientos y accesos rápidos según el rol.
- **Reportes:** Reportes de productos y de movimientos con filtros, visualización en pantalla y exportación a PDF (con gráficas) y Excel. Solo para el rol ADMINISTRADOR.
- **Perfil:** Consulta y actualización de los datos personales y cambio de contraseña del usuario con sesión activa.
- **Interfaz:** Logo de Inventory en el menú lateral y en el login, barra superior con buscador de productos y menú de usuario, iconos SVG propios (sin emojis) y un estilo visual unificado en todos los módulos.

---

## Módulo: Autenticación

Control de acceso mediante sesiones HTTP y protección de contraseñas con BCrypt.

**Roles definidos:**
- ADMINISTRADOR: acceso total al sistema
- OPERADOR: acceso a operaciones de inventario

**Características:**
- Inicio de sesión por username y contraseña
- Registro de usuarios
- Cierre de sesión
- Sesiones HTTP con Spring Security

**Endpoints:**
```
POST   /api/v1/auth/login
POST   /api/v1/auth/registrar
POST   /api/v1/auth/logout
```

---

## Módulo: Usuarios

Administración de usuarios del sistema con asignación de roles y cambio de estado.

**Características:**
- Crear, consultar y actualizar usuarios
- Desactivar y reactivar usuarios
- Búsqueda de usuarios
- Visualización de rol y estado

**Endpoints:**
```
POST   /api/v1/usuarios
GET    /api/v1/usuarios
GET    /api/v1/usuarios/{id}
PUT    /api/v1/usuarios/{id}
DELETE /api/v1/usuarios/{id}
PUT    /api/v1/usuarios/{id}/reactivar
```

**Restricción:** Solo acceso para rol ADMINISTRADOR.

---

## Módulo: Categorías

Organización de productos mediante categorías del inventario.

**Características:**
- Crear, consultar y actualizar categorías
- Desactivar y reactivar categorías
- Búsqueda por nombre

**Endpoints:**
```
POST   /api/v1/categorias
GET    /api/v1/categorias
GET    /api/v1/categorias/{id}
PUT    /api/v1/categorias/{id}
DELETE /api/v1/categorias/{id}
PUT    /api/v1/categorias/{id}/reactivar
```

---

## Módulo: Productos

Gestión de productos con control automático de estado según stock disponible.

**Características:**
- CRUD de productos con código único
- Control de stock mínimo y actual
- Estado automático: DISPONIBLE, AGOTADO, INACTIVO
- Búsqueda por código o nombre
- Desactivación y reactivación de productos

**Validaciones:**
- Código de producto único
- Stock no negativo
- Stock mínimo definido por usuario

**Endpoints:**
```
POST   /api/v1/productos
GET    /api/v1/productos
GET    /api/v1/productos/{id}
PUT    /api/v1/productos/{id}
DELETE /api/v1/productos/{id}
PUT    /api/v1/productos/{id}/reactivar
```

---

## Módulo: Proveedores

Registro y administración de proveedores del inventario.

**Características:**
- Crear, consultar y actualizar proveedores
- Desactivar y reactivar proveedores
- Búsqueda por nombre o NIT
- Contacto e información comercial

**Endpoints:**
```
POST   /api/v1/proveedores
GET    /api/v1/proveedores
GET    /api/v1/proveedores/{id}
PUT    /api/v1/proveedores/{id}
DELETE /api/v1/proveedores/{id}
PUT    /api/v1/proveedores/{id}/reactivar
```

---

## Módulo: Clientes

Gestión de clientes para registrar salidas de inventario.

**Características:**
- CRUD de clientes
- Búsqueda por nombre o documento
- Desactivación y reactivación
- Información de contacto

**Endpoints:**
```
POST   /api/v1/clientes
GET    /api/v1/clientes
GET    /api/v1/clientes/{id}
PUT    /api/v1/clientes/{id}
DELETE /api/v1/clientes/{id}
PUT    /api/v1/clientes/{id}/reactivar
```

---

## Módulo: Movimientos de Inventario

Registro permanente de entradas y salidas del inventario con trazabilidad completa.

**Características:**
- Registrar entradas (ingreso de unidades)
- Registrar salidas (entrega a cliente)
- Historial permanente e inmutable (no se puede editar ni eliminar)
- Registro automático de usuario y fecha
- Cálculo automático de stock antes y después
- Búsqueda y filtrado del historial
- Paginación de registros

**Validaciones:**
- Cantidad debe ser positiva
- Stock suficiente para salidas
- Cliente obligatorio en salidas
- Producto debe estar activo

**Endpoints:**
```
GET    /api/v1/movimientos          (historial con filtros)
GET    /api/v1/movimientos/{id}     (detalle de movimiento)
POST   /api/v1/movimientos/entrada  (registrar entrada)
POST   /api/v1/movimientos/salida   (registrar salida)
```

**Restricción:** Solo acceso para roles ADMINISTRADOR y OPERADOR.

---

## Módulo: Dashboard

Pantalla principal del sistema con un resumen del inventario. Es de solo lectura.

**Indicadores:**
- Productos, categorías, proveedores y clientes activos
- Total histórico de movimientos registrados
- Productos con stock bajo y agotados
- Entradas y salidas del día (cantidad de movimientos y unidades movidas)
- Últimos movimientos registrados

**Cómo se presenta la información:**
- Una franja de alerta que aparece cuando hay productos con stock bajo o agotados. Su botón lleva a Reportes si el usuario es ADMINISTRADOR y a Productos si es OPERADOR.
- Cinco tarjetas con los totales (productos, categorías, proveedores, clientes y movimientos). Cada tarjeta abre el módulo correspondiente.
- "Actividad de hoy" con las unidades que entraron y salieron en el día y cuántos movimientos fueron, y "Estado del inventario" con una barra de productos disponibles, con stock bajo y agotados.
- Tabla de últimos movimientos con etiquetas ENTRADA y SALIDA y la cantidad con signo.
- Accesos rápidos para registrar una entrada o una salida, ir a Productos y, solo para el ADMINISTRADOR, ir a Reportes.

**Endpoint:**
```
GET    /api/v1/dashboard
```

**Restricción:** Acceso para roles ADMINISTRADOR y OPERADOR.

---

## Módulo: Reportes

Consultas del inventario con exportación a PDF y Excel.

**Características:**
- Reporte de productos: filtros por estado, proveedor, categoría y stock bajo
- Reporte de movimientos: filtros por rango de fechas, tipo, producto y cliente
- Indicadores resumen y tabla de detalle en pantalla
- Exportación a PDF con gráficas (estado de los productos, entradas y salidas en el tiempo, productos más movidos)
- Exportación a Excel con hoja de resumen y hoja de datos con filtros
- El archivo descargado usa los mismos filtros del reporte que se ve en pantalla

**Endpoints:**
```
GET    /api/v1/reportes/productos
GET    /api/v1/reportes/productos/pdf
GET    /api/v1/reportes/productos/excel
GET    /api/v1/reportes/movimientos
GET    /api/v1/reportes/movimientos/pdf
GET    /api/v1/reportes/movimientos/excel
```

**Restricción:** Solo acceso para el rol ADMINISTRADOR.

---

## Módulo: Perfil

Cada usuario administra su propia información. El usuario se toma de la sesión, nunca de la URL.

**Características:**
- Consultar el perfil
- Actualizar nombre, apellido y correo (el username y el rol no se modifican)
- Cambiar la contraseña confirmando la actual (mínimo 6 caracteres, almacenada con BCrypt)

**Endpoints:**
```
GET    /api/v1/perfil
PUT    /api/v1/perfil
PUT    /api/v1/perfil/contrasena
```

Se abre desde el menú del usuario en la barra superior. Si el usuario cambia su nombre, la barra se actualiza sin recargar la página.

**Restricción:** Acceso para cualquier usuario autenticado (ADMINISTRADOR y OPERADOR).

---

## Interfaz y Diseño Visual

Todas las pantallas internas comparten la misma identidad visual para que el sistema se vea como una sola aplicación.

- **Logo:** la caja con el check de Inventory aparece en el menú lateral de todos los módulos (incluido el Dashboard) y en la pantalla de inicio de sesión.
- **Barra superior:** aparece en todas las pantallas internas. Incluye un saludo, un buscador de productos (al enviar, abre Productos con el texto ya filtrado) y el menú del usuario, con avatar de iniciales, nombre y rol. Desde ese menú se accede a "Mi perfil" y se cierra la sesión.
- **Inicio de sesión:** el logo completo a la izquierda y, a la derecha, el formulario con un icono de usuario dibujado en SVG.
- **Iconos:** se dibujaron como SVG de línea (`components/Iconos.tsx`), por lo que no dependen de emojis ni de librerías externas.
- **Colores con significado:** azul para entradas y acciones principales, naranja para salidas y avisos, verde para disponible, rojo para agotado o error y gris para información neutra.
- **Estilo unificado:** las tarjetas, tablas, etiquetas de estado, botones y campos de todos los módulos se definen en `src/index.css`, de modo que los cambios visuales se hacen en un solo lugar.
- **Componentes compartidos:** `Sidebar`, `LogoInventory`, `IconoUsuario`, `Iconos`, `TarjetaKpi` (usada en el Dashboard y en Reportes) y `Paginacion`, para no repetir código.

---

## Tecnologías Utilizadas

### Backend
- Java 17
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- Spring Security (sesiones HTTP)
- Validation
- Maven
- MySQL 8.0
- BCrypt
- JdbcTemplate (consultas del Dashboard y de Reportes)
- Apache POI (exportación a Excel)
- OpenPDF (exportación a PDF)
- SLF4J (registro de eventos)

### Frontend
- React 18
- TypeScript
- Vite
- Bootstrap 5
- Axios
- React Router

### Herramientas
- Visual Studio Code
- Git / GitHub
- MySQL Workbench
- Postman

---

## Arquitectura

**Cliente-Servidor:**
```
Frontend (React)
    ↓ HTTP/REST
Backend (Spring Boot)
    ↓ JPA
Base de datos (MySQL)
```

**Capas del Backend:**
```
Controller → Service → Repository → Base de datos
```

Los módulos de gestión (usuarios, categorías, productos, etc.) acceden a la base de datos con Spring Data JPA. El Dashboard y los Reportes son de solo lectura, por lo que consultan con `JdbcTemplate` y consultas parametrizadas, sin entidades propias.

**Base de datos:** `inventory_db`

**Configuración:** `inventory-backend/src/main/resources/application.properties`

---

## Estructura del Proyecto

```
INVENTORY/
├── database/
│   ├── 01_create_categoria_table.sql
│   ├── 02_create_producto_table.sql
│   └── 03_create_movimiento_table.sql
├── docs/
│   └── PRUEBAS_MANUALES.md
├── frontend-inventory/
│   └── src/
│       ├── assets/ (logo de Inventory)
│       ├── auth/
│       ├── categories/
│       ├── clients/
│       ├── components/ (Sidebar, Topbar, LogoInventory, IconoUsuario, Iconos, TarjetaKpi, Paginacion)
│       ├── dashboard/
│       ├── movements/
│       ├── products/
│       ├── profile/
│       ├── reports/
│       ├── services/
│       ├── suppliers/
│       ├── users/
│       └── ...
├── inventory-backend/
│   └── src/main/java/com/inventory/
│       ├── auth/
│       ├── category/
│       ├── client/
│       ├── config/
│       ├── dashboard/
│       ├── exception/
│       ├── movement/
│       ├── product/
│       ├── profile/
│       ├── report/
│       ├── supplier/
│       ├── user/
│       └── ...
├── README.md
├── LICENSE
└── .gitignore
```

---

## Ejecución del Proyecto

### Backend
```bash
cd inventory-backend
mvn spring-boot:run
```
Se ejecuta en: `http://localhost:8080`

### Frontend
```bash
cd frontend-inventory
npm install
npm run dev
```
Se ejecuta en: `http://localhost:5173`

---

## Pruebas

### Backend
Pruebas de endpoints mediante Postman:
- Autenticación y sesiones
- Usuarios (CRUD y cambio de estado)
- Categorías, Proveedores y Clientes
- Productos (CRUD, búsqueda, cambio de estado)
- Movimientos (entrada, salida, búsqueda, filtrado)
- Dashboard (totales y últimos movimientos)
- Reportes (filtros y descarga de PDF y Excel, solo ADMINISTRADOR)
- Perfil (consulta, actualización y cambio de contraseña)

### Frontend
- Navegación entre módulos
- Formularios de entrada
- Búsqueda y filtrado
- Paginación
- Validaciones de negocio
- Mensajes de error y éxito
- Integración con el Backend
- Diferencias de acceso entre ADMINISTRADOR y OPERADOR (Dashboard, Reportes y Usuarios)

Los casos de prueba manuales están documentados en [`docs/PRUEBAS_MANUALES.md`](docs/PRUEBAS_MANUALES.md).

---

## Estado Actual

**En desarrollo activo:**
- Pruebas manuales finales y documentación

**Completados:**
- Autenticación
- Usuarios
- Categorías
- Proveedores
- Clientes
- Productos
- Movimientos de Inventario
- Dashboard
- Reportes (con exportación a PDF y Excel)
- Perfil

---

## Notas para el Desarrollo

- Los archivos de configuración de la base de datos están en la carpeta `database/`
- La documentación adicional se encuentra en `docs/` (por ahora, los casos de pruebas manuales)
- El proyecto sigue las convenciones de arquitectura por capas definidas en la EKB
- Todos los módulos implementados incluyen validaciones de negocio en Backend y Frontend
- Las listas de los módulos usan la paginación compartida `components/Paginacion.tsx` (10 registros por página)
- El Backend registra eventos relevantes (inicio y cierre de sesión, errores de autenticación, excepciones no controladas y errores de base de datos) mediante SLF4J
- Las respuestas de error siguen el formato `{ success, message, errors }` definido en el SSD
- La apariencia (logo, iconos, colores y componentes compartidos) se centraliza en `frontend-inventory/src/components/` y `src/index.css`
