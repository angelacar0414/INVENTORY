# INVENTORY - BACKEND (Spring Boot)

**Aprendices:** Darío Bustamante Camargo y Ángela Carvajal  
**Ficha:** 3186706

Este es el proyecto Backend de INVENTORY, desarrollado con Java 17 y Spring Boot. El Backend proporciona la API REST utilizada por el Frontend para gestionar la información relacionada con el inventario.

## Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Validation
- Maven
- MySQL
- BCrypt
- Sesiones HTTP
- JdbcTemplate (Dashboard y Reportes)
- Apache POI (exportación a Excel)
- OpenPDF (exportación a PDF)
- SLF4J (registro de eventos)

## Base de datos

El Backend utiliza MySQL como sistema gestor de base de datos.

Base de datos utilizada:

```text
inventory_db
```

La configuración de conexión se encuentra en:

```text
src/main/resources/application.properties
```

## Estructura del proyecto

El Backend está organizado mediante una arquitectura por capas, separando las responsabilidades principales de controladores, servicios y repositorios. Cada módulo vive en su propio paquete (organización por funcionalidad).

```text
inventory-backend/
└── src/
    └── main/
        ├── java/
        │   └── com.inventory/
        │       ├── auth/        (inicio de sesión, registro y cierre de sesión)
        │       ├── user/        (usuarios)
        │       ├── category/    (categorías)
        │       ├── supplier/    (proveedores)
        │       ├── client/      (clientes)
        │       ├── product/     (productos)
        │       ├── movement/    (movimientos de inventario)
        │       ├── dashboard/   (indicadores generales)
        │       ├── report/      (reportes, con exportación a PDF y Excel)
        │       ├── profile/     (perfil del usuario con sesión)
        │       ├── config/      (SecurityConfig y CorsConfig)
        │       └── exception/   (excepciones y manejo global de errores)
        │
        └── resources/
            └── application.properties
```

## Patrón de arquitectura

La comunicación entre los componentes principales del Backend sigue el siguiente flujo:

```text
Controller
     |
     v
  Service
     |
     v
 Repository
     |
     v
   MySQL
```

Los controladores reciben las solicitudes HTTP, los servicios contienen la lógica de negocio y los repositorios se encargan de la comunicación con la base de datos mediante Spring Data JPA. Los módulos de solo lectura (Dashboard y Reportes) consultan con `JdbcTemplate` y consultas parametrizadas.
-------------------------------------------------------------------
## Módulo: Autenticación

El módulo de autenticación permite controlar el acceso de los usuarios al sistema.

Actualmente se encuentra implementado:

Inicio de sesión mediante username y contraseña.
Registro de usuarios.
Cierre de sesión.
Manejo de sesiones HTTP.
Protección mediante Spring Security.
Contraseñas almacenadas mediante BCrypt.
Control de acceso mediante roles.

Los roles definidos actualmente son:

ADMINISTRADOR
OPERADOR
Endpoints
POST /api/v1/auth/login
POST /api/v1/auth/registrar
POST /api/v1/auth/logout
-----------------------------------------------------------------------------
## Módulo: Usuarios

El módulo de usuarios permite administrar los usuarios registrados en el sistema.

Actualmente se encuentran implementadas las siguientes funcionalidades:

Crear usuarios.
Consultar todos los usuarios.
Consultar un usuario por ID.
Actualizar usuarios.
Desactivar usuarios.
Reactivar usuarios.
Validar usuarios duplicados por username y correo.
Asignar rol al usuario.
Controlar el acceso mediante roles.
Endpoints
POST   /api/v1/usuarios
GET    /api/v1/usuarios
GET    /api/v1/usuarios/{id}
PUT    /api/v1/usuarios/{id}
DELETE /api/v1/usuarios/{id}
PUT    /api/v1/usuarios/{id}/reactivar

El acceso a los endpoints de gestión de usuarios está restringido al rol ADMINISTRADOR.
----------------------------------------------------------------
## Módulo: Categorías

El módulo de categorías permite gestionar las categorías utilizadas para organizar los productos del inventario.

Actualmente se encuentran implementadas funcionalidades para:

Crear categorías.
Consultar categorías.
Actualizar categorías.
Desactivar categorías.
Reactivar categorías.
Buscar categorías.

El módulo utiliza una estructura separada de:

controller
service
repository
entity
dto
---------------------------------------------------------------
## Módulo: Proveedores

El módulo de proveedores permite registrar y administrar la información de los proveedores relacionados con el inventario.

Actualmente se encuentran implementadas funcionalidades para:

Crear proveedores.
Consultar proveedores.
Actualizar proveedores.
Desactivar proveedores.
Reactivar proveedores.

El módulo utiliza una estructura separada de:

controller
service
repository
entity
dto
-----------------------------------------------------------------------
## Módulo: Clientes

El módulo de clientes permite registrar y administrar la información de los clientes asociados a las salidas de productos del inventario.

Actualmente se encuentran implementadas funcionalidades para:

Crear clientes.
Consultar clientes.
Actualizar clientes.
Desactivar clientes.
Reactivar clientes.
Buscar clientes.

El módulo utiliza una estructura separada de:

controller
service
repository
entity
dto
----------------------------------------------------------------------
## Módulo: Productos

El módulo de productos permite administrar el catálogo de productos del inventario con control automático de estado según el stock disponible.

Actualmente se encuentran implementadas funcionalidades para:

Crear productos con código único.
Consultar productos.
Actualizar productos.
Desactivar productos.
Reactivar productos.
Buscar productos por código o nombre.
Control automático de estado: DISPONIBLE, AGOTADO, INACTIVO.
Validación de stock mínimo.

El módulo utiliza una estructura separada de:

controller
service
repository
entity
dto

Endpoints
POST   /api/v1/productos
GET    /api/v1/productos
GET    /api/v1/productos/{id}
PUT    /api/v1/productos/{id}
DELETE /api/v1/productos/{id}
PUT    /api/v1/productos/{id}/reactivar

----------------------------------------------------------------------
## Módulo: Movimientos

El módulo de movimientos permite registrar y mantener un historial permanente de todas las entradas y salidas del inventario.

Actualmente se encuentran implementadas funcionalidades para:

Registrar entradas de inventario.
Registrar salidas de inventario.
Consultar el historial de movimientos.
Buscar movimientos por producto.
Filtrar movimientos por tipo (entrada o salida).
Validación de cantidad disponible.
Cálculo automático de stock anterior y posterior.
Registro automático de usuario y fecha.

El módulo utiliza una estructura separada de:

controller
service
repository
entity
dto
validator
exception handler

Validaciones específicas del módulo:

Cantidad debe ser un número positivo.
Stock suficiente para salidas.
Cliente obligatorio en salidas.
Producto y usuario activos.

Endpoints
GET    /api/v1/movimientos (con filtros opcionales de criterio y tipo)
GET    /api/v1/movimientos/{id}
POST   /api/v1/movimientos/entrada
POST   /api/v1/movimientos/salida

El acceso a los endpoints de movimientos está restringido a los roles ADMINISTRADOR y OPERADOR. El historial no se puede editar ni eliminar (operaciones PUT y DELETE están deshabilitadas por seguridad).

----------------------------------------------------------------------
## Módulo: Dashboard

El módulo de Dashboard entrega los indicadores generales del inventario. Es de solo lectura y consulta las tablas mediante JdbcTemplate, sin Entity ni Repository propios.

Indicadores:

Total de productos, categorías, proveedores y clientes activos.
Total histórico de movimientos registrados.
Productos con stock bajo y agotados.
Entradas y salidas del día.
Últimos movimientos registrados.

Endpoint
GET    /api/v1/dashboard

El acceso está permitido a los roles ADMINISTRADOR y OPERADOR.

----------------------------------------------------------------------
## Módulo: Reportes

El módulo de reportes permite consultar el inventario con filtros y exportar la información en PDF y Excel.

Actualmente se encuentran implementadas funcionalidades para:

Reporte de productos (filtros por estado, proveedor, categoría y stock bajo).
Reporte de movimientos (filtros por fechas, tipo, producto y cliente).
Exportación a PDF con gráficas.
Exportación a Excel con hoja de resumen y hoja de datos.

El módulo utiliza una estructura separada de:

controller
service
repository (consultas con JdbcTemplate y parámetros)
dto
validator
exception handler
export (generación de PDF, Excel y gráficas)

Librerías utilizadas:

Apache POI (Excel).
OpenPDF (PDF).
Java2D para dibujar las gráficas del PDF.

Endpoints
GET    /api/v1/reportes/productos
GET    /api/v1/reportes/productos/pdf
GET    /api/v1/reportes/productos/excel
GET    /api/v1/reportes/movimientos
GET    /api/v1/reportes/movimientos/pdf
GET    /api/v1/reportes/movimientos/excel

El acceso a los endpoints de reportes está restringido al rol ADMINISTRADOR.

----------------------------------------------------------------------
## Módulo: Perfil

El módulo de perfil permite que cada usuario administre su propia información. El usuario se identifica por la sesión activa, nunca por un identificador enviado en la URL.

Actualmente se encuentran implementadas funcionalidades para:

Consultar el perfil.
Actualizar nombre, apellido y correo electrónico.
Cambiar la contraseña confirmando la contraseña actual.

El módulo utiliza una estructura separada de:

controller
service
dto
validator
exception handler

Validaciones específicas del módulo:

Nombre, apellido y correo obligatorios.
Formato de correo válido y no utilizado por otro usuario.
La contraseña actual debe ser correcta.
La nueva contraseña debe tener al menos 6 caracteres, ser diferente a la actual y coincidir con su confirmación.
La contraseña se almacena cifrada con BCrypt.

Endpoints
GET    /api/v1/perfil
PUT    /api/v1/perfil
PUT    /api/v1/perfil/contrasena

El acceso está permitido a cualquier usuario autenticado.

----------------------------------------------------------------------
Seguridad

El Backend utiliza Spring Security para controlar el acceso a los diferentes recursos de la API.

Actualmente:

Los endpoints de autenticación son de acceso público.
Los endpoints de usuarios requieren el rol ADMINISTRADOR.
Los endpoints de reportes requieren el rol ADMINISTRADOR.
Los endpoints de dashboard y movimientos requieren el rol ADMINISTRADOR u OPERADOR.
Los endpoints de perfil requieren un usuario autenticado, y la persona se identifica por su sesión.
Las contraseñas se almacenan utilizando BCrypt.
Se utilizan sesiones HTTP para mantener la autenticación.
La protección CSRF se encuentra deshabilitada para facilitar el funcionamiento de la API REST.

El control de acceso se configura principalmente mediante SecurityConfig.

Registro de eventos y manejo de errores

El Backend registra eventos relevantes mediante SLF4J (inicio y cierre de sesión, errores de autenticación, excepciones no controladas y errores de acceso a la base de datos). Los mensajes técnicos se escriben en la consola del servidor y al cliente solo se le entrega un mensaje claro.

Las respuestas de error siguen el formato:

{ "success": false, "message": "Descripción del error.", "errors": [ ... ] }

Pruebas

Las pruebas de los endpoints se realizan principalmente utilizando Postman.

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
Validación de acceso según el rol.
Gestión de categorías.
Gestión de proveedores.
Gestión de clientes.
Gestión de productos.
Registro de entradas y salidas, y consulta del historial de movimientos.
Indicadores del dashboard.
Reportes de productos y movimientos con filtros, y descarga de PDF y Excel (solo ADMINISTRADOR).
Consulta y actualización del perfil, y cambio de contraseña.

También se verifica la comunicación entre el Backend y el Frontend durante las pruebas de integración. Los casos de prueba manuales se encuentran en `docs/PRUEBAS_MANUALES.md`, en la raíz del repositorio.

Ejecución del proyecto

Para ejecutar el Backend se debe contar previamente con:

Java 17 instalado.
Maven instalado.
MySQL Server activo.
Base de datos inventory_db creada.
Configuración de conexión correctamente establecida.

Desde la carpeta inventory-backend ejecutar:

mvn spring-boot:run

El Backend se ejecuta normalmente en:

http://localhost:8080
Configuración

La configuración principal del proyecto se encuentra en:

src/main/resources/application.properties

En este archivo se configura principalmente:

Conexión con MySQL.
Nombre de la base de datos.
Usuario de la base de datos.
Contraseña.
Configuración de JPA y Hibernate.
Configuración relacionada con el servidor.
Estado actual del Backend
-----------------------------------------------------------------------

Actualmente se encuentran implementados los módulos de:

Autenticación.
Usuarios.
Categorías.
Proveedores.
Clientes.
Productos.
Movimientos.
Dashboard.
Reportes.
Perfil.

Con estos módulos se completan los definidos en el alcance de INVENTORY 1.0.
