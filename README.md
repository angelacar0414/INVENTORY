# INVENTORY - Módulo Categorías (Backend)

**Proyecto:** INVENTORY - Sistema Web de Gestión de Inventarios
**Aprendices:** Darío Bustamante y Ángela Carvajal Ortiz
**Ficha:** 3186706 | **Instructor:** Wilson Alberto Mosquera Caicedo
**Evidencia:** GA8-220501096-AA1-EV01

## ¿Qué trae esta entrega?

El primer módulo del sistema (**Categorías**), siguiendo el orden de
desarrollo definido en el Documento 10 del EKB (Incremento 1),
codificado con la arquitectura oficial del proyecto: Java 17 +
Spring Boot 3 + Spring Data JPA + MySQL 8.

Incluye las 4 operaciones pedidas por la guía (inserción, consulta,
actualización y eliminación -lógica-), correspondientes a:

- RF-9 / CU-9 Registrar categoría
- RF-10 / CU-10 Editar categoría
- RF-11 / CU-11 Desactivar categoría (eliminación lógica)
- RF-12 / CU-12 Consultar categorías

## Estructura de carpetas

```
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
```

## Nota sobre JDBC

La arquitectura oficial de INVENTORY (ver EKB, ADR-004 y ADR-008) usa
**Spring Data JPA**, que por dentro usa el driver **JDBC**
(`mysql-connector-j`) configurado en `application.properties` para
abrir la conexión real con MySQL. Es decir: la conexión JDBC existe,
solo que Spring nos evita escribir el SQL a mano.

Adicionalmente, se incluye la clase `jdbcdemo/CategoryJdbcDemo.java`,
que muestra la conexión JDBC de forma explícita y "manual"
(`DriverManager`, `Connection`, `PreparedStatement`, `ResultSet`),
solo como evidencia académica del componente formativo. Esta clase
es independiente y no forma parte del flujo real de la aplicación.

## Cómo ejecutarlo (paso a paso)

1. Instalar MySQL 8 y crear la base de datos ejecutando el script
   `database/01_create_categoria_table.sql` desde MySQL Workbench.
2. Abrir la carpeta `inventory-backend` en IntelliJ IDEA.
3. Editar `application.properties` y poner tu contraseña real de MySQL.
4. Ejecutar `InventoryApplication.java` (botón ▶ en IntelliJ).
5. Probar los endpoints en Postman:
   - `GET    http://localhost:8080/api/v1/categorias`
   - `GET    http://localhost:8080/api/v1/categorias/1`
   - `POST   http://localhost:8080/api/v1/categorias`  (body JSON: `{"nombre":"Aseo","descripcion":"Productos de limpieza"}`)
   - `PUT    http://localhost:8080/api/v1/categorias/1`
   - `DELETE http://localhost:8080/api/v1/categorias/1`

## Próximos módulos (en orden, según Documento 10 del EKB)

1. ~~Categorías~~ ✅ (esta entrega)
2. Proveedores
3. Clientes
4. Productos
5. Usuarios
6. Movimientos
7. Dashboard
8. Reportes
