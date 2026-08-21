<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.inventory.categoria.modelo.Categoria" %>
<%
    request.setAttribute("paginaActual", "categorias");

    List<Categoria> categorias = (List<Categoria>) request.getAttribute("categorias");
    String mensajeError = (String) request.getAttribute("mensajeError");
    Boolean mostrarTodasObj = (Boolean) request.getAttribute("mostrarTodas");
    boolean mostrarTodas = mostrarTodasObj != null && mostrarTodasObj;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Categorías - INVENTORY</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>

<div class="contenedor">

    <jsp:include page="/includes/sidebar.jsp" />

    <div class="contenido">

        <div class="encabezado-pagina">
            <div>
                <h1>Categorías</h1>
                <p>Gestión de categorías</p>
            </div>
            <a href="categorias?accion=nuevo" class="boton boton-primario">+ Nueva categoría</a>
        </div>

        <% if (mensajeError != null) { %>
            <div class="mensaje-error"><%= mensajeError %></div>
        <% } %>

        <div class="buscador" style="display: flex; justify-content: space-between; align-items: center;">
            <input type="text" id="campoBuscar" placeholder="Buscar categoría..." onkeyup="filtrarTabla()">

            <% if (mostrarTodas) { %>
                <a href="categorias?accion=listar" class="boton boton-secundario">Ver solo activas</a>
            <% } else { %>
                <a href="categorias?accion=listar&estado=todas" class="boton boton-secundario">Ver todas (incluye inactivas)</a>
            <% } %>
        </div>

        <div class="tarjeta">
            <table id="tablaCategorias">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Descripción</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <%-- Fila que se muestra solo cuando el buscador no encuentra coincidencias --%>
                    <tr id="filaSinResultados" style="display: none;">
                        <td colspan="5" class="sin-datos">No se encontraron categorías con ese criterio de búsqueda.</td>
                    </tr>
                    <%
                        if (categorias != null && !categorias.isEmpty()) {
                            for (Categoria c : categorias) {
                    %>
                    <tr data-fila-categoria="true">
                        <td><%= c.getId() %></td>
                        <td><%= c.getNombre() %></td>
                        <td><%= c.getDescripcion() != null ? c.getDescripcion() : "" %></td>
                        <td>
                            <% if (c.isActivo()) { %>
                                <span class="estado estado-activo">Activo</span>
                            <% } else { %>
                                <span class="estado estado-inactivo">Inactivo</span>
                            <% } %>
                        </td>
                        <td class="acciones">
                            <% if (c.isActivo()) { %>
                                <a class="editar" href="categorias?accion=editar&id=<%= c.getId() %>">Editar</a>
                                <a class="desactivar" href="categorias?accion=desactivar&id=<%= c.getId() %>"
                                   onclick="return confirm('¿Desactivar esta categoría?');">Desactivar</a>
                            <% } else { %>
                                <a class="editar" href="categorias?accion=reactivar&id=<%= c.getId() %>"
                                   onclick="return confirm('¿Reactivar esta categoría?');">Reactivar</a>
                            <% } %>
                        </td>
                    </tr>
                    <%
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="5" class="sin-datos">No hay categorías registradas todavía.</td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>

    </div>
</div>

<script>
    function filtrarTabla() {
        const texto = document.getElementById("campoBuscar").value.toLowerCase();
        const filas = document.querySelectorAll("#tablaCategorias tbody tr[data-fila-categoria]");
        let coincidencias = 0;

        filas.forEach(function (fila) {
            const contenido = fila.textContent.toLowerCase();
            const coincide = contenido.includes(texto);
            fila.style.display = coincide ? "" : "none";
            if (coincide) coincidencias++;
        });

        const filaVacia = document.getElementById("filaSinResultados");
        if (filaVacia) {
            filaVacia.style.display = (coincidencias === 0) ? "" : "none";
        }
    }
</script>

</body>
</html>