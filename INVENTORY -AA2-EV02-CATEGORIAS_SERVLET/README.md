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

## Cómo funciona (para explicarlo si te preguntan)

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

1. Abre IntelliJ → `File → Settings → Plugins`.
2. Busca **"Smart Tomcat"** e instálalo (dale Restart si lo pide).
3. Descarga Apache Tomcat 10 desde `https://tomcat.apache.org/download-10.cgi`
   (elige el ZIP de "Core"), descomprímelo en una carpeta fija, ej:
   `C:\apache-tomcat-10.1.x`
4. Abre esta carpeta (`CATEGORIAS_SERVLET`) como proyecto en IntelliJ.
5. Espera a que Maven cargue las dependencias.
6. Ve a `Run → Edit Configurations... → + → Smart Tomcat`.
7. En **"Tomcat Server"** selecciona la carpeta donde descomprimiste Tomcat.
8. En **"Deployment directory"** deja la que aparece por defecto (webapp).
9. Dale **Run** (▶).
10. Abre el navegador en: `http://localhost:8080/categorias-servlet/`

### Antes de correrlo: configura tu contraseña de MySQL

Edita `src/main/java/com/inventory/categoria/util/ConexionBD.java` y cambia
`TU_CLAVE_MYSQL` por tu contraseña real.

### Antes de correrlo: crea la tabla (si no la tienes de la actividad anterior)

Ejecuta `database/01_create_categoria_table.sql` en MySQL Workbench.

## Próximos módulos

Con esta misma estructura de Servlet + JSP + DAO se puede replicar para
Proveedores, Clientes, Productos, etc., cambiando solo el nombre de las
clases y la tabla.
