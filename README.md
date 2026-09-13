# INVENTORY - Sistema Web de Gestión de Inventarios (BACKEND)

**Aprendiz:** Dario Bustamante Camargo  
**Aprendiz:** Ángela Carvajal   
**Ficha:** 3186706 
**Instructor:** Diana galviz, Wilson Mosquera, Erika Parra

---

## Descripción del proyecto

INVENTORY es un sistema web de gestión de inventarios desarrollado para micro y pequeñas empresas (MiPymes), con el objetivo de facilitar el control y administración de la información relacionada con el inventario.

El sistema está enfocado exclusivamente en la gestión y control de inventarios. No corresponde a un sistema ERP y no contempla dentro de su alcance módulos de ventas, facturación, contabilidad o compras.

---

## Módulos del sistema

El proyecto contempla los siguientes módulos:

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

Actualmente se encuentran implementados:

- Autenticación
- Usuarios
- Categorías
- Proveedores

Los demás módulos serán desarrollados progresivamente de acuerdo con los requisitos y el alcance definido para el proyecto.

---

## Módulo: Autenticación

El sistema cuenta con un módulo de autenticación para controlar el acceso de los usuarios.

Actualmente se encuentra implementado:

- Inicio de sesión mediante username y contraseña.
- Registro de usuarios.
- Cierre de sesión.
- Manejo de sesiones HTTP.
- Contraseñas protegidas mediante BCrypt.
- Control de acceso mediante roles.

Los roles definidos actualmente son:

- ADMINISTRADOR
- OPERADOR

La gestión de usuarios se encuentra protegida mediante Spring Security y requiere permisos de administrador.

### Endpoints

```text
POST /api/v1/auth/login
POST /api/v1/auth/registrar
POST /api/v1/auth/logout
Módulo: Usuarios

El módulo de usuarios permite administrar los usuarios registrados en el sistema.

Actualmente se encuentran implementadas las siguientes funcionalidades:

Crear usuarios.
Consultar usuarios.
Consultar un usuario por su identificador.
Actualizar usuarios.
Desactivar usuarios.
Reactivar usuarios.
Buscar usuarios.
Visualizar el rol y estado de cada usuario.
Endpoints
POST   /api/v1/usuarios
GET    /api/v1/usuarios
GET    /api/v1/usuarios/{id}
PUT    /api/v1/usuarios/{id}
DELETE /api/v1/usuarios/{id}
PUT    /api/v1/usuarios/{id}/reactivar

El acceso al módulo de usuarios está restringido al rol ADMINISTRADOR.

Módulo: Categorías

El módulo de categorías permite organizar los productos del inventario mediante diferentes categorías.

Actualmente se encuentran implementadas las funcionalidades de:

Crear categorías.
Consultar categorías.
Actualizar categorías.
Desactivar categorías.
Reactivar categorías.
Buscar categorías.
Módulo: Proveedores

El módulo de proveedores permite registrar y administrar la información de los proveedores relacionados con el inventario.

Actualmente se encuentran implementadas las funcionalidades de:

Crear proveedores.
Consultar proveedores.
Actualizar proveedores.
Desactivar proveedores.
Reactivar proveedores.
Tecnologías utilizadas
Backend
Java 17
Spring Boot
Spring Web
Spring Data JPA
Spring Security
Validation
Maven
MySQL
BCrypt
Sesiones HTTP
Frontend
React 18
TypeScript
Vite
Bootstrap 5
Axios
React Router
Herramientas
Visual Studio Code
Git
GitHub
MySQL Workbench
Postman
Arquitectura

El sistema utiliza una arquitectura cliente-servidor:

Frontend (React)
        |
        | HTTP / REST
        v
Backend (Spring Boot)
        |
        | JPA
        v
Base de datos (MySQL)

El backend está organizado mediante una arquitectura por capas, principalmente:

Controller
Service
Repository
Base de datos

El sistema utiliza MySQL como sistema gestor de base de datos.

La base de datos utilizada para el proyecto es:

inventory_db

La configuración de conexión se encuentra en:

src/main/resources/application.properties
Estructura actual del proyecto
INVENTORY/
│
├── database/
│
├── frontend-inventory/
│
├── inventory-backend/
│
├── assets/
│
├── README.md
├── LICENSE
└── .gitignore

La carpeta docs aún no se encuentra incorporada al repositorio. Se agregará posteriormente para organizar la documentación técnica del proyecto.

Pruebas

Las pruebas de los endpoints del backend se realizan principalmente mediante Postman.

Actualmente se han realizado pruebas de:

Inicio de sesión.
Registro de usuarios.
Cierre de sesión.
Creación de usuarios.
Consulta de usuarios.
Consulta de usuario por ID.
Actualización de usuarios.
Desactivación de usuarios.
Reactivación de usuarios.
Control de acceso según el rol.
Gestión de categorías.
Gestión de proveedores.

También se han realizado pruebas de integración entre el frontend y el backend mediante el navegador.

Ejecución del proyecto
Backend

Desde la carpeta inventory-backend ejecutar:

mvn spring-boot:run

El backend se ejecuta normalmente en:

http://localhost:8080
Frontend

Desde la carpeta frontend-inventory instalar las dependencias:

npm install

Ejecutar el servidor de desarrollo:

npm run dev

El frontend se ejecuta normalmente en:

http://localhost:5173
Estado actual del proyecto

El proyecto se encuentra actualmente en etapa de desarrollo.

Los módulos de Autenticación, Usuarios, Categorías y Proveedores cuentan con funcionalidades implementadas y probadas.

Los módulos de Productos, Clientes, Movimientos de inventario, Dashboard, Estadísticas, Reportes y Exportación de reportes serán desarrollados progresivamente de acuerdo con el alcance y los requisitos definidos para INVENTORY.
