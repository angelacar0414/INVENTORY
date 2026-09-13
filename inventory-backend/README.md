# INVENTORY - BACKEND (Spring Boot)

**Aprendices:** Darío Bustamante Camargo y Ángela Carolina Rojas  
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

## Base de datos

El Backend utiliza MySQL como sistema gestor de base de datos.

Base de datos utilizada:

```text
inventory_db

La configuración de conexión se encuentra en:

src/main/resources/application.properties
Estructura del proyecto

El Backend está organizado mediante una arquitectura por capas, separando las responsabilidades principales de controladores, servicios y repositorios.

inventory-backend/
└── src/
    └── main/
        ├── java/
        │   └── com.inventory/
        │       ├── auth/
        │       ├── user/
        │       ├── category/
        │       ├── supplier/
        │       └── ...
        │
        └── resources/
            └── application.properties
Patrón de arquitectura

La comunicación entre los componentes principales del Backend sigue el siguiente flujo:

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

Los controladores reciben las solicitudes HTTP, los servicios contienen la lógica de negocio y los repositorios se encargan de la comunicación con la base de datos mediante Spring Data JPA.

Módulo: Autenticación

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
Módulo: Usuarios

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

Módulo: Categorías

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
Módulo: Proveedores

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
Seguridad

El Backend utiliza Spring Security para controlar el acceso a los diferentes recursos de la API.

Actualmente:

Los endpoints de autenticación son de acceso público.
Los endpoints de usuarios requieren el rol ADMINISTRADOR.
Las contraseñas se almacenan utilizando BCrypt.
Se utilizan sesiones HTTP para mantener la autenticación.
La protección CSRF se encuentra deshabilitada para facilitar el funcionamiento de la API REST.

El control de acceso se configura principalmente mediante SecurityConfig.

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

También se verifica la comunicación entre el Backend y el Frontend durante las pruebas de integración.

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

Actualmente se encuentran implementados los módulos de:

Autenticación.
Usuarios.
Categorías.
Proveedores.

Los módulos restantes del sistema serán desarrollados progresivamente de acuerdo con los requisitos y el alcance definido para INVENTORY.