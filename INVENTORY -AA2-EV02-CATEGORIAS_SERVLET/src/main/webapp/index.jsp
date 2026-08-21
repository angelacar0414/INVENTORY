<%--
    Página de entrada del sitio. Como por ahora solo está construido
    el módulo de Categorías, apenas alguien entra a la raíz del
    proyecto, lo mandamos directo al listado de categorías.
--%>
<%
    response.sendRedirect("categorias?accion=listar");
%>
