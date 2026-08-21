<%@ page contentType="text/html;charset=UTF-8" language="java" %><%--



    BARRA LATERAL REUTILIZABLE
    ----------------------------
    Este archivo no es una página completa, es un "pedacito" de HTML
    que se inserta dentro de otras páginas con <jsp:include>. Así no
    hay que copiar y pegar el mismo menú en cada JSP del proyecto.

    La variable "paginaActual" se define en cada página que incluye
    este archivo, y sirve para saber qué opción del menú resaltar
    en azul (la que está "activa" en pantalla).
--%>
<div class="sidebar">
    <div class="logo">INVENTORY</div>

    <a href="#" class="<%= "dashboard".equals(request.getAttribute("paginaActual")) ? "activo" : "" %>">Dashboard</a>
    <a href="#">Usuarios</a>
    <a href="categorias?accion=listar" class="<%= "categorias".equals(request.getAttribute("paginaActual")) ? "activo" : "" %>">Categorías</a>
    <a href="#">Productos</a>
    <a href="#">Proveedores</a>
    <a href="#">Clientes</a>
    <a href="#">Movimientos</a>
    <a href="#">Reportes</a>

    <div class="cerrar-sesion">Cerrar sesión</div>
</div>
