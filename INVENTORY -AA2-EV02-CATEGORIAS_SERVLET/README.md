# INVENTORY - Módulo Categorías (Servlets + JSP)

**Aprendices:** Darío Bustamante y Ángela Carvajal Ortiz
**Ficha:** 3186706

## ¿Qué trae esta entrega?

El módulo Categorías, esta vez construido con **Servlets + JSP + JDBC**
(tecnología web clásica de Java), en vez de Spring Boot. Incluye las
4 operaciones (insertar, consultar, actualizar, desactivar), con
formularios HTML reales que usan los métodos GET y POST.

## Estructura del proyecto

```
CATEGORIAS_SERVLET/
├── pom.xml
├── database/01_create_categoria_table.sql
└── src/main/
    ├── java/com/inventory/categoria/
    │   ├── modelo/Categoria.java        (clase POJO)
    │   ├── util/ConexionBD.java         (conexión JDBC pura)
    │   ├── dao/CategoriaDAO.java        (CRUD con JDBC)
    │   └── servlet/CategoriaServlet.java (recibe GET y POST)
    └── webapp/
        ├── index.jsp
        ├── categorias/
        │   ├── listar.jsp               (tabla de categorías)
        │   └── formulario.jsp           (formulario crear/editar)
        ├── includes/sidebar.jsp         (menú lateral reutilizable)
        ├── css/estilos.css
        └── WEB-INF/web.xml
```

## Cómo funciona 

1. El navegador pide `categorias?accion=listar` → esto es un **GET**.
2. `CategoriaServlet.doGet()` lo recibe, llama a `CategoriaDAO.listarActivas()`,
   que usa JDBC (`DriverManager`, `PreparedStatement`) para consultar MySQL.
3. El Servlet guarda la lista con `request.setAttribute("categorias", ...)`
   y reenvía (`forward`) a `listar.jsp`.
4. `listar.jsp` recorre esa lista con un `for` dentro de scriptlets `<% %>`
   y arma la tabla HTML.
5. Cuando el usuario llena el formulario y da clic en "Guardar", el
   `<form method="post">` de `formulario.jsp` envía los datos a
   `CategoriaServlet.doPost()`, que valida, llama al DAO para
   insertar/actualizar, y redirige de nuevo al listado.

## Cómo ejecutarlo

Este tipo de proyecto (Servlets/JSP) necesita un **servidor Tomcat** para
correr, a diferencia del backend con Spring Boot que se ejecutaba solo.

### Opción recomendada: IntelliJ + plugin "Smart Tomcat" (gratis, funciona en Community Edition)

1. Abrimos IntelliJ → `File → Settings → Plugins`.
2. Buscamos **"Smart Tomcat"** e instálamos (damos en Restart si lo pide).
3. Descargamos Apache Tomcat 10 desde `https://tomcat.apache.org/download-10.cgi`
   (elegimos el ZIP de "Core"), descomprímimos en una carpeta fija, ej:
   `C:\apache-tomcat-10.1.x`
4. Abrimos esta carpeta (`CATEGORIAS_SERVLET`) como proyecto en IntelliJ.
5. Esperamos a que Maven cargue las dependencias.
6. Vamos a `Run → Edit Configurations... → + → Smart Tomcat`.
7. En **"Tomcat Server"** seleccionamos la carpeta donde descomprimiste Tomcat.
8. En **"Deployment directory"** dejamos la que aparece por defecto (webapp).
9. Damos **Run** (▶).
10. Abrimos el navegador en: `http://localhost:8080/categorias-servlet/`

### Antes de correrlo: configuramos nuestra contraseña de MySQL

Editamos `src/main/java/com/inventory/categoria/util/ConexionBD.java` y cambiamos
`TU_CLAVE_MYSQL` por nuestra contraseña real.

### Antes de correrlo: creamos la tabla (si no la tenemos de la actividad anterior)

Ejecutamos `database/01_create_categoria_table.sql` en MySQL Workbench.

## Próximos módulos

Con esta misma estructura de Servlet + JSP + DAO se puede replicar para
Proveedores, Clientes, Productos, etc., cambiando solo el nombre de las
clases y la tabla.
