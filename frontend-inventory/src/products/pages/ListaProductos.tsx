import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { Sidebar } from "../../components/Sidebar";
import { productoService } from "../services/productoService";
import type { IProducto } from "../types/IProducto";

/**
 * PÁGINA: Lista de Productos
 * --------------------------
 * Esta página permite consultar y administrar los productos
 * registrados en el sistema INVENTORY.
 *
 * Funcionalidades principales:
 *
 * - Consultar productos activos.
 * - Consultar todos los productos.
 * - Buscar productos.
 * - Ver información de stock.
 * - Ver estado del producto.
 * - Editar productos.
 * - Desactivar productos.
 * - Reactivar productos.
 *
 * Los permisos importantes también son controlados por
 * el Backend mediante Spring Security.
 */
export function ListaProductos() {

  // Lista de productos que se muestran actualmente.
  const [productos, setProductos] = useState<IProducto[]>([]);

  // Texto utilizado para realizar búsquedas.
  const [criterio, setCriterio] = useState("");

  // Indica si se están mostrando productos activos
  // o todos los productos.
  const [mostrarTodos, setMostrarTodos] = useState(false);

  // Controla el estado de carga de la información.
  const [cargando, setCargando] = useState(true);

  // Permite mostrar mensajes de error en pantalla.
  const [error, setError] = useState("");

  // Obtiene el rol del usuario que inició sesión.
  //
  // Spring Security también controla estos permisos
  // en el Backend. Esta validación solamente permite
  // adaptar la interfaz visual.
  const rol = sessionStorage.getItem("rol") || "";

  // Determina si el usuario actual es administrador.
  const esAdministrador = rol === "ADMINISTRADOR";

  /**
   * CARGAR PRODUCTOS
   * ----------------
   * Consulta los productos dependiendo de la opción
   * seleccionada:
   *
   * - Activos.
   * - Todos.
   */
  const cargarProductos = async () => {
    try {
      setCargando(true);
      setError("");

      let resultado: IProducto[];

      if (mostrarTodos) {
        resultado = await productoService.listarTodas();
      } else {
        resultado = await productoService.listar();
      }

      setProductos(resultado);

    } catch (error: any) {

      // Intentamos mostrar el mensaje enviado por el Backend.
      const mensaje =
        error?.response?.data?.message ||
        "No fue posible cargar los productos.";

      setError(mensaje);

    } finally {
      setCargando(false);
    }
  };

  /**
   * EFECTO INICIAL
   * --------------
   * Carga los productos cuando se abre la página.
   *
   * También vuelve a cargarlos cuando cambia la opción
   * "Mostrar todos".
   */
  useEffect(() => {
    cargarProductos();
  }, [mostrarTodos]);

  /**
   * BUSCAR PRODUCTOS
   * ----------------
   * La búsqueda se realiza directamente contra el Backend.
   *
   * Si el campo está vacío, se vuelve a cargar la lista
   * normal de productos.
   */
  const buscarProductos = async (valor: string) => {

    setCriterio(valor);

    // Si no existe texto de búsqueda, volvemos a cargar
    // la lista correspondiente.
    if (!valor.trim()) {
      cargarProductos();
      return;
    }

    try {
      setCargando(true);
      setError("");

      const resultado = await productoService.buscar(valor);

      setProductos(resultado);

    } catch (error: any) {

      const mensaje =
        error?.response?.data?.message ||
        "No fue posible realizar la búsqueda.";

      setError(mensaje);

    } finally {
      setCargando(false);
    }
  };

  /**
   * DESACTIVAR PRODUCTO
   * -------------------
   * Antes de realizar la operación se solicita
   * confirmación al usuario.
   */
  const desactivarProducto = async (id: number) => {

    const confirmar = window.confirm(
      "¿Está seguro de que desea desactivar este producto?"
    );

    if (!confirmar) {
      return;
    }

    try {

      await productoService.desactivar(id);

      alert("Producto desactivado correctamente.");

      // Volvemos a cargar la información.
      cargarProductos();

    } catch (error: any) {

      const mensaje =
        error?.response?.data?.message ||
        "No fue posible desactivar el producto.";

      alert(mensaje);
    }
  };

  /**
   * REACTIVAR PRODUCTO
   * ------------------
   * Solicita confirmación antes de volver a activar
   * un producto.
   */
  const reactivarProducto = async (id: number) => {

    const confirmar = window.confirm(
      "¿Está seguro de que desea reactivar este producto?"
    );

    if (!confirmar) {
      return;
    }

    try {

      await productoService.reactivar(id);

      alert("Producto reactivado correctamente.");

      cargarProductos();

    } catch (error: any) {

      const mensaje =
        error?.response?.data?.message ||
        "No fue posible reactivar el producto.";

      alert(mensaje);
    }
  };

  /**
   * FORMATEAR VALORES MONETARIOS
   * ----------------------------
   * Convierte los valores numéricos de precios a formato
   * de pesos colombianos para mostrarlos en pantalla.
   */
  const formatoMoneda = (valor: number) => {
    return valor.toLocaleString("es-CO", {
      style: "currency",
      currency: "COP",
      minimumFractionDigits: 0,
    });
  };

  /**
   * OBTENER CLASE VISUAL DEL ESTADO
   * -------------------------------
   * Utilizamos clases de Bootstrap para diferenciar
   * visualmente los estados de los productos.
   */
  const claseEstado = (estado: IProducto["estado"]) => {

    switch (estado) {

      case "DISPONIBLE":
        return "badge bg-success";

      case "AGOTADO":
        return "badge bg-warning text-dark";

      case "INACTIVO":
        return "badge bg-secondary";

      default:
        return "badge bg-secondary";
    }
  };

  return (
    <div className="d-flex">

      {/* Menú lateral principal de INVENTORY */}
      <Sidebar />

      {/* Contenido principal */}
      <main className="contenido flex-grow-1">

        {/* Encabezado de la página */}
        <div className="encabezado-pagina">

          <div>
            <h1>Productos</h1>

            <p className="text-muted mb-0">
              Gestión y consulta de productos del inventario.
            </p>
          </div>

          {/* Solamente el administrador puede registrar productos */}
          {esAdministrador && (
            <Link
              to="/productos/nuevo"
              className="btn btn-primary"
            >
              + Nuevo producto
            </Link>
          )}

        </div>

        {/* Tarjeta principal */}
        <div className="tarjeta">

          {/* Barra superior de búsqueda y filtros */}
          <div className="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">

            {/* Buscador */}
            <div className="buscador flex-grow-1">

              <input
                type="text"
                className="form-control"
                placeholder="Buscar por código, nombre o categoría..."
                value={criterio}
                onChange={(e) => buscarProductos(e.target.value)}
              />

            </div>

            {/* Botón para cambiar entre activos y todos */}
            <div>

              <button
                type="button"
                className={`btn ${
                  mostrarTodos
                    ? "btn-outline-secondary"
                    : "btn-outline-primary"
                }`}
                onClick={() => setMostrarTodos(!mostrarTodos)}
              >
                {mostrarTodos
                  ? "Ver solo activos"
                  : "Ver todos"}
              </button>

            </div>

          </div>

          {/* Mensaje de error */}
          {error && (
            <div className="alert alert-danger">
              {error}
            </div>
          )}

          {/* Indicador de carga */}
          {cargando ? (

            <div className="text-center py-5">
              <div
                className="spinner-border"
                role="status"
              >
                <span className="visually-hidden">
                  Cargando...
                </span>
              </div>

              <p className="mt-2 text-muted">
                Cargando productos...
              </p>
            </div>

          ) : (

            /* Tabla de productos */
            <div className="table-responsive">

              <table className="table table-hover align-middle">

                <thead>

                  <tr>
                    <th>Código</th>
                    <th>Producto</th>
                    <th>Categoría</th>
                    <th>Proveedor</th>
                    <th>Precio compra</th>
                    <th>Precio venta</th>
                    <th>Stock</th>
                    <th>Estado</th>

                    {/* Las acciones solamente tienen sentido
                        para administradores. */}
                    {esAdministrador && (
                      <th className="text-center">
                        Acciones
                      </th>
                    )}
                  </tr>

                </thead>

                <tbody>

                  {productos.length === 0 ? (

                    <tr>

                      <td
                        colSpan={esAdministrador ? 9 : 8}
                        className="text-center py-4 text-muted"
                      >
                        No se encontraron productos.
                      </td>

                    </tr>

                  ) : (

                    productos.map((producto) => (

                      <tr key={producto.idProducto}>

                        {/* Código generado por el Backend */}
                        <td>
                          <strong>
                            {producto.codigo}
                          </strong>
                        </td>

                        {/* Nombre y descripción */}
                        <td>

                          <div>
                            {producto.nombre}
                          </div>

                          {producto.descripcion && (
                            <small className="text-muted">
                              {producto.descripcion}
                            </small>
                          )}

                        </td>

                        {/* Categoría */}
                        <td>
                          {producto.nombreCategoria}
                        </td>

                        {/* Proveedor */}
                        <td>
                          {producto.nombreProveedor}
                        </td>

                        {/* Precio de compra */}
                        <td>
                          {formatoMoneda(producto.precioCompra)}
                        </td>

                        {/* Precio de venta */}
                        <td>
                          {formatoMoneda(producto.precioVenta)}
                        </td>

                        {/* Información del stock */}
                        <td>

                          <strong>
                            {producto.stockActual}
                          </strong>

                          <small className="d-block text-muted">
                            Mínimo: {producto.stockMinimo}
                          </small>

                        </td>

                        {/* Estado calculado por el Backend */}
                        <td>

                          <span
                            className={claseEstado(
                              producto.estado
                            )}
                          >
                            {producto.estado}
                          </span>

                        </td>

                        {/* Acciones administrativas */}
                        {esAdministrador && (

                          <td>

                            <div className="d-flex gap-2 justify-content-center">

                              {/* Editar */}
                              {producto.activo && (
                                <Link
                                  to={`/productos/editar/${producto.idProducto}`}
                                  className="btn btn-sm btn-outline-primary"
                                >
                                  Editar
                                </Link>
                              )}

                              {/* Desactivar */}
                              {producto.activo && (
                                <button
                                  type="button"
                                  className="btn btn-sm btn-outline-danger"
                                  onClick={() =>
                                    desactivarProducto(
                                      producto.idProducto
                                    )
                                  }
                                >
                                  Desactivar
                                </button>
                              )}

                              {/* Reactivar */}
                              {!producto.activo && (
                                <button
                                  type="button"
                                  className="btn btn-sm btn-outline-success"
                                  onClick={() =>
                                    reactivarProducto(
                                      producto.idProducto
                                    )
                                  }
                                >
                                  Reactivar
                                </button>
                              )}

                            </div>

                          </td>

                        )}

                      </tr>

                    ))

                  )}

                </tbody>

              </table>

            </div>

          )}

        </div>

      </main>

    </div>
  );
}