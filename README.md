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

**Base de datos:** `inventory_db`

**Configuración:** `inventory-backend/src/main/resources/application.properties`

---

## Estructura del Proyecto

```
INVENTORY/
├── database/
│   ├── 01_create_database.sql
│   ├── 02_create_tables.sql
│   └── 03_create_movimiento_table.sql
├── frontend-inventory/
│   └── src/
│       ├── auth/
│       ├── categories/
│       ├── clients/
│       ├── components/
│       ├── dashboard/
│       ├── movements/
│       ├── products/
│       ├── suppliers/
│       ├── users/
│       └── ...
├── inventory-backend/
│   └── src/main/java/com/inventory/
│       ├── auth/
│       ├── category/
│       ├── client/
│       ├── config/
│       ├── exception/
│       ├── movement/
│       ├── product/
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

### Frontend
- Navegación entre módulos
- Formularios de entrada
- Búsqueda y filtrado
- Paginación
- Validaciones de negocio
- Mensajes de error y éxito
- Integración con el Backend

---

## Estado Actual

**En desarrollo activo:**
- Dashboard y estadísticas
- Reportes
- Exportación a PDF y Excel

**Completados:**
- Autenticación
- Usuarios
- Categorías
- Proveedores
- Clientes
- Productos
- Movimientos de Inventario

---

## Notas para el Desarrollo

- Los archivos de configuración de la base de datos están en la carpeta `database/`
- La documentación adicional se encuentra en `docs/` (próximamente)
- El proyecto sigue las convenciones de arquitectura por capas definidas en la EKB
- Todos los módulos implementados incluyen validaciones de negocio en Backend y Frontend
