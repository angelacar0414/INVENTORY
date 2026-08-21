package com.inventory.categoria.servlet;

import com.inventory.categoria.dao.CategoriaDAO;
import com.inventory.categoria.modelo.Categoria;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * SERVLET DE CATEGORÍAS
 * ------------------------
 * Recibe las peticiones GET y POST y decide qué hacer con cada una,
 * apoyándose en el CategoriaDAO para hablar con la base de datos.
 *
 * Los System.out.println() que aparecen aquí son solo para PODER VER
 * en la consola del servidor (Tomcat) que cada acción realmente pasa
 * por el Servlet. Sirven como evidencia visual para el video y para
 * comprobar que el procesamiento ocurre del lado del servidor, no
 * solo en el navegador.
 */
@WebServlet("/categorias")
public class CategoriaServlet extends HttpServlet {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        System.out.println("=====================================================");
        System.out.println("[SERVLET] Petición GET recibida -> accion=" + accion);
        System.out.println("[SERVLET] URL completa: " + request.getRequestURL() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : ""));

        try {
            switch (accion) {
                case "nuevo":
                    System.out.println("[SERVLET] Mostrando formulario vacío (nueva categoría).");
                    mostrarFormulario(request, response, null);
                    break;

                case "editar":
                    int id = Integer.parseInt(request.getParameter("id"));
                    System.out.println("[SERVLET] Buscando categoría con id=" + id + " para editar.");
                    Categoria categoria = categoriaDAO.buscarPorId(id);
                    mostrarFormulario(request, response, categoria);
                    break;

                case "desactivar":
                    int idDesactivar = Integer.parseInt(request.getParameter("id"));
                    System.out.println("[SERVLET] Desactivando categoría con id=" + idDesactivar);
                    categoriaDAO.desactivar(idDesactivar);
                    System.out.println("[SERVLET] Categoría id=" + idDesactivar + " desactivada en la base de datos.");
                    response.sendRedirect("categorias?accion=listar");
                    break;

                case "reactivar":
                    int idReactivar = Integer.parseInt(request.getParameter("id"));
                    System.out.println("[SERVLET] Reactivando categoría con id=" + idReactivar);
                    categoriaDAO.reactivar(idReactivar);
                    System.out.println("[SERVLET] Categoría id=" + idReactivar + " reactivada en la base de datos.");
                    response.sendRedirect("categorias?accion=listar&estado=todas");
                    break;

                case "listar":
                default:
                    listarCategorias(request, response);
                    break;
            }
        } catch (SQLException e) {
            System.out.println("[SERVLET] ERROR de base de datos: " + e.getMessage());
            request.setAttribute("mensajeError",
                    "Ocurrió un error al conectar con la base de datos: " + e.getMessage());
            request.getRequestDispatcher("/categorias/listar.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        System.out.println("=====================================================");
        System.out.println("[SERVLET] Petición POST recibida -> accion=" + accion);

        try {
            String idTexto = request.getParameter("id");
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");

            System.out.println("[SERVLET] Datos del formulario -> id=" + idTexto +
                    " | nombre=" + nombre + " | descripcion=" + descripcion);

            // Validación en el servidor: obligatorio y máximo 30 caracteres.
            // Nunca confiamos solo en el "maxlength" del HTML, porque alguien
            // podría saltárselo editando el formulario desde el navegador.
            if (nombre == null || nombre.trim().isEmpty() || nombre.trim().length() > 30) {
                System.out.println("[SERVLET] Validación fallida: nombre vacío o supera 30 caracteres.");
                request.setAttribute("mensajeError",
                        "El nombre de la categoría es obligatorio y no puede superar los 30 caracteres.");
                Categoria categoriaConError = new Categoria();
                categoriaConError.setNombre(nombre);
                categoriaConError.setDescripcion(descripcion);
                if (idTexto != null && !idTexto.isEmpty()) {
                    categoriaConError.setId(Integer.parseInt(idTexto));
                }
                mostrarFormulario(request, response, categoriaConError);
                return;
            }

            if ("guardar".equals(accion)) {
                boolean esNueva = (idTexto == null || idTexto.trim().isEmpty());

                if (esNueva) {
                    System.out.println("[SERVLET] Intentando INSERTAR categoría nueva: " + nombre);

                    if (categoriaDAO.existeNombre(nombre)) {
                        System.out.println("[SERVLET] Validación fallida: el nombre '" + nombre + "' ya existe.");
                        request.setAttribute("mensajeError", "Ya existe una categoría con ese nombre.");
                        Categoria categoriaConError = new Categoria(nombre, descripcion);
                        mostrarFormulario(request, response, categoriaConError);
                        return;
                    }
                    Categoria nueva = new Categoria(nombre, descripcion);
                    categoriaDAO.insertar(nueva);
                    System.out.println("[SERVLET] Categoría '" + nombre + "' insertada correctamente en MySQL.");

                } else {
                    int id = Integer.parseInt(idTexto);
                    System.out.println("[SERVLET] Intentando ACTUALIZAR categoría id=" + id + " -> nuevo nombre: " + nombre);

                    if (categoriaDAO.existeNombreEnOtraCategoria(nombre, id)) {
                        System.out.println("[SERVLET] Validación fallida: el nombre '" + nombre + "' ya lo usa otra categoría.");
                        request.setAttribute("mensajeError", "Ya existe otra categoría con ese nombre.");
                        Categoria categoriaConError = new Categoria();
                        categoriaConError.setId(id);
                        categoriaConError.setNombre(nombre);
                        categoriaConError.setDescripcion(descripcion);
                        mostrarFormulario(request, response, categoriaConError);
                        return;
                    }

                    Categoria actualizar = new Categoria();
                    actualizar.setId(id);
                    actualizar.setNombre(nombre);
                    actualizar.setDescripcion(descripcion);
                    categoriaDAO.actualizar(actualizar);
                    System.out.println("[SERVLET] Categoría id=" + id + " actualizada correctamente en MySQL.");
                }
            }

            response.sendRedirect("categorias?accion=listar");

        } catch (SQLException e) {
            System.out.println("[SERVLET] ERROR de base de datos: " + e.getMessage());
            request.setAttribute("mensajeError",
                    "Ocurrió un error al guardar en la base de datos: " + e.getMessage());
            request.getRequestDispatcher("/categorias/listar.jsp").forward(request, response);
        }
    }

    // ---------------------------------------------------------------
    // Métodos privados de apoyo
    // ---------------------------------------------------------------

    private void listarCategorias(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String estado = request.getParameter("estado");
        boolean mostrarTodas = "todas".equals(estado);

        System.out.println("[SERVLET] Consultando categorías -> mostrarTodas=" + mostrarTodas);

        List<Categoria> categorias = mostrarTodas
                ? categoriaDAO.listarTodas()
                : categoriaDAO.listarActivas();

        System.out.println("[SERVLET] Se encontraron " + categorias.size() + " categoría(s).");

        request.setAttribute("categorias", categorias);
        request.setAttribute("mostrarTodas", mostrarTodas);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/categorias/listar.jsp");
        dispatcher.forward(request, response);
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
                                   Categoria categoria)
            throws ServletException, IOException {

        request.setAttribute("categoria", categoria);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/categorias/formulario.jsp");
        dispatcher.forward(request, response);
    }
}