#INVENTORY - Sistema Web de Gestión de Inventarios (BACKEND)
Proyecto: INVENTORY - Sistema Web de Gestión de Inventarios Aprendices: Darío Bustamante y Ángela Carvajal Ortiz Ficha: 3186706 Instructor: Diana Galviz/Wilson Mosquera/Erika Parra

Módulos:

categorías 
usuarios 
proveedores 
clientes 
productos 
movimientos 
dashboard 
reportes
________________________________________________________________________________
#Módulo 1 Categorías (Backend)
Evidencia: Codificación de módulos del software (categoría) GA7-220501096-AA2-EV01.

¿Qué trae esta entrega?
El primer módulo del sistema (Categorías), codificado con la arquitectura oficial del proyecto: Java 17 + Spring Boot 3 + Spring Data JPA + MySQL 8.

Incluye las 4 operaciones (inserción, consulta, actualización y eliminación -lógica-), correspondientes a:

Registrar categoría
Editar categoría
Desactivar categoría (eliminación lógica)
Consultar categorías
Estructura de carpetas
inventory-backend/
├── pom.xml
└── src/main/
    ├── java/com/inventory/
    │   ├── InventoryApplication.java     (clase principal)
    │   ├── category/
    │   │   ├── controller/CategoryController.java
    │   │   ├── service/CategoryService.java
    │   │   ├── repository/CategoryRepository.java
    │   │   ├── entity/CategoryEntity.java
    │   │   ├── dto/CategoryDTO.java
    │   │   └── mapper/CategoryMapper.java
    │   ├── exception/                    (compartido por todos los módulos)
    │   │   ├── GlobalExceptionHandler.java
    │   │   ├── ResourceNotFoundException.java
    │   │   └── DuplicateResourceException.java
    │   └── jdbcdemo/
    │       └── CategoryJdbcDemo.java     (JDBC "puro", ver nota abajo)
    └── resources/application.properties
database/
└── 01_create_categoria_table.sql
Nota sobre JDBC
La arquitectura oficial de INVENTORY usa Spring Data JPA, que por dentro usa el driver JDBC (mysql-connector-j) configurado en application.properties para abrir la conexión real con MySQL. Es decir: la conexión JDBC existe, solo que Spring nos evita escribir el SQL a mano.

Adicionalmente, se incluye la clase jdbcdemo/CategoryJdbcDemo.java, que muestra la conexión JDBC de forma explícita y "manual" (DriverManager, Connection, PreparedStatement, ResultSet), solo como evidencia académica del componente formativo. Esta clase es independiente y no forma parte del flujo real de la aplicación.

Cómo ejecutamos
Instalamos MySQL 8 y creamos la base de datos ejecutando el script database/01_create_categoria_table.sql desde MySQL Workbench.
Abrimos la carpeta inventory-backend en IntelliJ IDEA.
Editamos application.properties y ponemos nuestra contraseña real de MySQL.
Ejecutamos InventoryApplication.java (botón ▶ en IntelliJ).
Probamos los endpoints en Postman:
GET    http://localhost:8080/api/v1/categorias
GET    http://localhost:8080/api/v1/categorias/1
POST   http://localhost:8080/api/v1/categorias (body JSON: {"nombre":"Repuestos","descripcion":"Respuestos y accesorios para motos"})
PUT    http://localhost:8080/api/v1/categorias/1
DELETE http://localhost:8080/api/v1/categorias/1
_________________________________________________________________________________
Módulo: Usuarios / Autenticación (Backend)

_________________________________________________________________________________

Módulo 3: Proveedores (Backend)
Lo codifiqué siguiendo el mismo patrón que ya había usado en Categorías, para que todo el backend se vea consistente. Tiene el CRUD completo, más dos operaciones extra que agregué: búsqueda por nombre y reactivación de un proveedor que había quedado desactivado.

Estructura
supplier/ ├── controller/SupplierController.java ├── service/SupplierService.java ├── repository/SupplierRepository.java ├── entity/SupplierEntity.java ├── dto/SupplierDTO.java └── mapper/SupplierMapper.java

Reglas que validé en el Service
No dejo registrar un proveedor con un nombre que ya exista.
Tampoco dejo repetir el correo electrónico entre dos proveedores.
La eliminación es lógica: al "borrar" un proveedor, lo que cambia es su campo activo a false, el registro nunca se borra de la base de datos.
Endpoints
GET http://localhost:8080/api/v1/proveedores — todos los proveedores
GET http://localhost:8080/api/v1/proveedores/activos — solo los activos
GET http://localhost:8080/api/v1/proveedores/buscar?nombre=texto — búsqueda por nombre
GET http://localhost:8080/api/v1/proveedores/1 — consulta por id
POST http://localhost:8080/api/v1/proveedores (body: {"nombre":"Repuestos Moto S.A.S","documento":"900123456-1","telefono":"3011234567","correo":"contacto@repuestosmoto.com","direccion":"Calle 10 # 20-30"})
PUT http://localhost:8080/api/v1/proveedores/1 — edición
DELETE http://localhost:8080/api/v1/proveedores/1 — desactivación (eliminación lógica)
PUT http://localhost:8080/api/v1/proveedores/1/reactivar — reactivación
Probé estas 9 combinaciones en Postman (registro, duplicado de nombre, listado completo, listado de activos, desactivación, verificación de que el proveedor sigue existiendo pero inactivo, búsqueda por nombre, consulta por id, edición y reactivación), y todas respondieron tal como esperaba.

