<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.inventory.categoria.modelo.Categoria" %>
<%
    request.setAttribute("paginaActual", "categorias");

    Categoria categoria = (Categoria) request.getAttribute("categoria");
    boolean esEdicion = (categoria != null && categoria.getId() != 0);
    String mensajeError = (String) request.getAttribute("mensajeError");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><%= esEdicion ? "Editar" : "Nueva" %> categoría - INVENTORY</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>

<div class="contenedor">

    <jsp:include page="/includes/sidebar.jsp" />

    <div class="contenido">

        <div class="encabezado-pagina">
            <div>
                <h1><%= esEdicion ? "Editar categoría" : "Nueva categoría" %></h1>
                <p>Complete los datos y guarde los cambios</p>
            </div>
        </div>

        <% if (mensajeError != null) { %>
            <div class="mensaje-error"><%= mensajeError %></div>
        <% } %>

        <form class="formulario" action="categorias" method="post">

            <input type="hidden" name="accion" value="guardar">

            <% if (esEdicion) { %>
                <input type="hidden" name="id" value="<%= categoria.getId() %>">
            <% } %>

            <label for="nombre">Nombre de la categoría (máx. 30 caracteres)</label>
            <input type="text"
                   id="nombre"
                   name="nombre"
                   required
                   maxlength="30"
                   value="<%= categoria != null && categoria.getNombre() != null ? categoria.getNombre() : "" %>">

            <label for="descripcion">Descripción</label>
            <textarea id="descripcion"
                      name="descripcion"
                      rows="3"
                      maxlength="255"><%= categoria != null && categoria.getDescripcion() != null ? categoria.getDescripcion() : "" %></textarea>

            <button type="submit" class="boton boton-primario">Guardar</button>
            <a href="categorias?accion=listar" class="boton boton-secundario">Cancelar</a>

        </form>

    </div>
</div>

</body>
</html>