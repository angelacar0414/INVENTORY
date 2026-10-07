import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { Sidebar } from "../../components/Sidebar";
import { productoService } from "../services/productoService";
import type { IProductoFormulario } from "../types/IProducto";

import { categoriaService } from "../../categories/services/categoriaService";
import type { ICategoria } from "../../categories/types/ICategoria";

import { proveedorService } from "../../suppliers/services/proveedorService";
import type { IProveedor } from "../../suppliers/types/IProveedor";

/**
 * PÁGINA: Formulario de Producto
 * ------------------------------
 * Esta página permite:
 *
 * - Registrar un producto nuevo.
 * - Editar un producto existente.
 * - Seleccionar una categoría.
 * - Seleccionar un proveedor.
 * - Definir precios.
 * - Definir stock inicial.
 * - Definir stock mínimo.
 *
 * Algunas propiedades NO son controladas directamente
 * desde este formulario porque pertenecen a la lógica
 * del Backend:
 *
 * - Código.
 * - Estado.
 * - Activo.
 * - Stock actual después de la creación.
 *
 * El stock posteriormente será administrado mediante
 * el módulo de Movimientos.
 */
export function FormularioProducto() {

  // Permite navegar entre las diferentes páginas.
  const navegar = useNavigate();

  // Obtiene el parámetro "id" cuando estamos editando.
  const { id } = useParams();

  // Determina si estamos creando o editando.
  const modoEdicion = Boolean(id);

  /**
   * Estado principal del formulario.
   *
   * Los valores iniciales son vacíos o cero para que
   * el formulario pueda utilizarse para crear un producto.
   */
  const [formulario, setFormulario] =
    useState<IProductoFormulario>({
      nombre: "",
      descripcion: "",
      precioCompra: 0,
      precioVenta: 0,
      stockActual: 0,
      stockMinimo: 0,
      idCategoria: 0,
      idProveedor: 0,
    });

  // Lista de categorías activas.
  const [categorias, setCategorias] =
    useState<ICategoria[]>([]);

  // Lista de proveedores activos.
  const [proveedores, setProveedores] =
    useState<IProveedor[]>([]);

  // Indica si se están cargando los datos iniciales.
  const [cargando, setCargando] = useState(true);

  // Controla el envío del formulario.
  const [guardando, setGuardando] = useState(false);

  // Mensaje de error general.
  const [error, setError] = useState("");

  // Mensaje de éxito.
  const [mensaje, setMensaje] = useState("");

  /**
   * CARGAR CATEGORÍAS Y PROVEEDORES
   * -------------------------------
   * Para registrar un producto necesitamos mostrar
   * únicamente categorías y proveedores activos.
   *
   * Se reutilizan los servicios que ya existen
   * en el proyecto.
   */
  const cargarDatosFormulario = async () => {

    try {

      setCargando(true);
      setError("");

      const [categoriasResultado, proveedoresResultado] =
        await Promise.all([
          categoriaService.listar(),
          proveedorService.listar(),
        ]);

      setCategorias(categoriasResultado);
      setProveedores(proveedoresResultado);

    } catch (error: any) {

      const mensajeError =
        error?.response?.data?.message ||
        "No fue posible cargar las categorías y proveedores.";

      setError(mensajeError);

    } finally {

      setCargando(false);
    }
  };

  /**
   * CARGAR PRODUCTO PARA EDITAR
   * ---------------------------
   * Si existe un ID en la URL, consultamos el producto
   * correspondiente y cargamos sus datos en el formulario.
   */
  const cargarProducto = async () => {

    if (!id) {
      return;
    }

    try {

      setCargando(true);
      setError("");

      const producto =
        await productoService.buscarPorId(Number(id));

      setFormulario({
        nombre: producto.nombre,
        descripcion: producto.descripcion || "",
        precioCompra: producto.precioCompra,
        precioVenta: producto.precioVenta,
        stockMinimo: producto.stockMinimo,
        idCategoria: producto.idCategoria,
        idProveedor: producto.idProveedor,

        /**
         * El stock actual se muestra solamente como
         * información.
         *
         * No se modifica mediante el formulario de edición.
         */
        stockActual: producto.stockActual,
      });

    } catch (error: any) {

      const mensajeError =
        error?.response?.data?.message ||
        "No fue posible cargar el producto.";

      setError(mensajeError);

    } finally {

      setCargando(false);
    }
  };

  /**
   * CARGA INICIAL
   * -------------
   * Primero cargamos las categorías y proveedores.
   *
   * Si estamos editando, posteriormente cargamos
   * también la información del producto.
   */
  useEffect(() => {

    const inicializar = async () => {

      await cargarDatosFormulario();

      if (id) {
        await cargarProducto();
      } else {
        setCargando(false);
      }
    };

    inicializar();

  }, [id]);

  /**
   * MANEJAR CAMBIOS DEL FORMULARIO
   * ------------------------------
   * Actualiza únicamente el campo que el usuario
   * está modificando.
   */
  const manejarCambio = (
    campo: keyof IProductoFormulario,
    valor: string | number
  ) => {

    setFormulario((actual) => ({
      ...actual,
      [campo]: valor,
    }));

    // Limpiamos mensajes anteriores cuando el usuario
    // vuelve a modificar el formulario.
    setError("");
    setMensaje("");
  };

  /**
   * VALIDACIÓN DEL FORMULARIO
   * --------------------------
   * Realizamos validaciones básicas antes de enviar
   * la información al Backend.
   */
  const validarFormulario = (): boolean => {

    if (!formulario.nombre.trim()) {
      setError("El nombre del producto es obligatorio.");
      return false;
    }

    if (formulario.nombre.trim().length > 150) {
      setError(
        "El nombre del producto no puede superar los 150 caracteres."
      );
      return false;
    }

    if (formulario.precioCompra < 0) {
      setError("El precio de compra no puede ser negativo.");
      return false;
    }

    if (formulario.precioVenta < 0) {
      setError("El precio de venta no puede ser negativo.");
      return false;
    }

    if (formulario.stockMinimo < 0) {
      setError("El stock mínimo no puede ser negativo.");
      return false;
    }

    if (
      !modoEdicion &&
      (formulario.stockActual ?? 0) < 0
    ) {
      setError("El stock inicial no puede ser negativo.");
      return false;
    }

    if (!formulario.idCategoria) {
      setError("Debe seleccionar una categoría.");
      return false;
    }

    if (!formulario.idProveedor) {
      setError("Debe seleccionar un proveedor.");
      return false;
    }

    return true;
  };

  /**
   * GUARDAR PRODUCTO
   * ----------------
   * Decide automáticamente si debemos crear o actualizar
   * dependiendo del modo del formulario.
   */
  const guardarProducto = async (
    evento: React.FormEvent
  ) => {

    evento.preventDefault();

    setError("");
    setMensaje("");

    // Primero realizamos las validaciones.
    if (!validarFormulario()) {
      return;
    }

    try {

      setGuardando(true);

      /**
       * Cuando estamos editando:
       *
       * NO enviamos stockActual porque el stock no debe
       * modificarse desde este formulario.
       */
      const datosParaEnviar: IProductoFormulario = {
        nombre: formulario.nombre.trim(),
        descripcion: formulario.descripcion.trim(),
        precioCompra: formulario.precioCompra,
        precioVenta: formulario.precioVenta,
        stockMinimo: formulario.stockMinimo,
        idCategoria: formulario.idCategoria,
        idProveedor: formulario.idProveedor,
      };

      /**
       * Cuando estamos creando sí enviamos el stock inicial.
       */
      if (!modoEdicion) {
        datosParaEnviar.stockActual =
          formulario.stockActual ?? 0;
      }

      if (modoEdicion) {

        await productoService.actualizar(
          Number(id),
          datosParaEnviar
        );

        setMensaje(
          "Producto actualizado correctamente."
        );

      } else {

        await productoService.crear(
          datosParaEnviar
        );

        setMensaje(
          "Producto creado correctamente."
        );
      }

      /**
       * Después de guardar esperamos un momento para que
       * el usuario pueda leer el mensaje y posteriormente
       * volvemos a la lista de productos.
       */
      setTimeout(() => {
        navegar("/productos");
      }, 800);

    } catch (error: any) {

      /**
       * Spring Boot devuelve el mensaje del error
       * dentro de response.data.message.
       */
      const mensajeError =
        error?.response?.data?.message ||
        "No fue posible guardar el producto.";

      setError(mensajeError);

    } finally {

      setGuardando(false);
    }
  };

  /**
   * ADVERTENCIA SOBRE EL PRECIO DE VENTA
   * -------------------------------------
   * El Backend permite que el precio de venta sea menor
   * que el precio de compra.
   *
   * Por eso NO bloqueamos el formulario.
   *
   * Simplemente mostramos una advertencia para que
   * el usuario pueda revisar el dato.
   */
  const precioVentaMenor =
    formulario.precioVenta > 0 &&
    formulario.precioCompra > formulario.precioVenta;

  /**
   * Mientras cargamos los datos mostramos un indicador.
   */
  if (cargando) {

    return (
      <div className="d-flex">

        <Sidebar />

        <main className="contenido flex-grow-1">

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
              Cargando información...
            </p>

          </div>

        </main>

      </div>
    );
  }

  return (
    <div className="d-flex">

      {/* Menú lateral principal */}
      <Sidebar />

      {/* Contenido principal */}
      <main className="contenido flex-grow-1">

        {/* Encabezado */}
        <div className="encabezado-pagina">

          <div>

            <h1>
              {modoEdicion
                ? "Editar producto"
                : "Nuevo producto"}
            </h1>

            <p className="text-muted mb-0">
              {modoEdicion
                ? "Actualiza la información del producto."
                : "Registra un nuevo producto en el inventario."}
            </p>

          </div>

        </div>

        {/* Formulario */}
        <div className="tarjeta">

          {/* Error */}
          {error && (
            <div className="alert alert-danger">
              {error}
            </div>
          )}

          {/* Éxito */}
          {mensaje && (
            <div className="alert alert-success">
              {mensaje}
            </div>
          )}

          {/* Advertencia de precios */}
          {precioVentaMenor && (
            <div className="alert alert-warning">

              <strong>Advertencia:</strong>{" "}

              El precio de venta es menor que el precio
              de compra. Revise que esta información sea
              correcta antes de guardar.

            </div>
          )}

          <form onSubmit={guardarProducto}>

            {/* Código del producto */}
            {modoEdicion && (
              <div className="mb-3">

                <label className="form-label">
                  Código
                </label>

                <input
                  type="text"
                  className="form-control"
                  value="Código generado por el sistema"
                  disabled
                />

                <div className="form-text">
                  El código del producto es administrado
                  automáticamente por el Backend.
                </div>

              </div>
            )}

            {/* Nombre */}
            <div className="mb-3">

              <label
                htmlFor="nombre"
                className="form-label"
              >
                Nombre del producto *
              </label>

              <input
                id="nombre"
                type="text"
                className="form-control"
                maxLength={150}
                value={formulario.nombre}
                onChange={(e) =>
                  manejarCambio(
                    "nombre",
                    e.target.value
                  )
                }
                placeholder="Ejemplo: Arroz Diana 500g"
                required
              />

            </div>

            {/* Descripción */}
            <div className="mb-3">

              <label
                htmlFor="descripcion"
                className="form-label"
              >
                Descripción
              </label>

              <textarea
                id="descripcion"
                className="form-control"
                rows={3}
                maxLength={255}
                value={formulario.descripcion}
                onChange={(e) =>
                  manejarCambio(
                    "descripcion",
                    e.target.value
                  )
                }
                placeholder="Descripción del producto"
              />

            </div>

            {/* Categoría y proveedor */}
            <div className="row">

              {/* Categoría */}
              <div className="col-md-6 mb-3">

                <label
                  htmlFor="categoria"
                  className="form-label"
                >
                  Categoría *
                </label>

                <select
                  id="categoria"
                  className="form-select"
                  value={formulario.idCategoria}
                  onChange={(e) =>
                    manejarCambio(
                      "idCategoria",
                      Number(e.target.value)
                    )
                  }
                  required
                >

                  <option value={0}>
                    Seleccione una categoría
                  </option>

                  {categorias.map((categoria) => (

                    <option
                      key={categoria.id}
                      value={categoria.id}
                    >
                      {categoria.nombre}
                    </option>

                  ))}

                </select>

              </div>

              {/* Proveedor */}
              <div className="col-md-6 mb-3">

                <label
                  htmlFor="proveedor"
                  className="form-label"
                >
                  Proveedor *
                </label>

                <select
                  id="proveedor"
                  className="form-select"
                  value={formulario.idProveedor}
                  onChange={(e) =>
                    manejarCambio(
                      "idProveedor",
                      Number(e.target.value)
                    )
                  }
                  required
                >

                  <option value={0}>
                    Seleccione un proveedor
                  </option>

                  {proveedores.map((proveedor) => (

                    <option
                      key={proveedor.id}
                      value={proveedor.id}
                    >
                      {proveedor.nombre}
                    </option>

                  ))}

                </select>

              </div>

            </div>

            {/* Precios */}
            <div className="row">

              {/* Precio de compra */}
              <div className="col-md-6 mb-3">

                <label
                  htmlFor="precioCompra"
                  className="form-label"
                >
                  Precio de compra *
                </label>

                <input
                  id="precioCompra"
                  type="number"
                  className="form-control"
                  min="0"
                  step="0.01"
                  value={formulario.precioCompra}
                  onChange={(e) =>
                    manejarCambio(
                      "precioCompra",
                      Number(e.target.value)
                    )
                  }
                  required
                />

              </div>

              {/* Precio de venta */}
              <div className="col-md-6 mb-3">

                <label
                  htmlFor="precioVenta"
                  className="form-label"
                >
                  Precio de venta *
                </label>

                <input
                  id="precioVenta"
                  type="number"
                  className="form-control"
                  min="0"
                  step="0.01"
                  value={formulario.precioVenta}
                  onChange={(e) =>
                    manejarCambio(
                      "precioVenta",
                      Number(e.target.value)
                    )
                  }
                  required
                />

              </div>

            </div>

            {/* Stock */}
            <div className="row">

              {/* Stock inicial */}
              <div className="col-md-6 mb-3">

                <label
                  htmlFor="stockActual"
                  className="form-label"
                >
                  Stock inicial
                </label>

                <input
                  id="stockActual"
                  type="number"
                  className="form-control"
                  min="0"
                  step="1"
                  value={formulario.stockActual ?? 0}
                  disabled={modoEdicion}
                  onChange={(e) =>
                    manejarCambio(
                      "stockActual",
                      Number(e.target.value)
                    )
                  }
                />

                <div className="form-text">

                  {modoEdicion
                    ? "El stock no se modifica desde Productos. Utilice el módulo Movimientos."
                    : "Cantidad inicial con la que se registrará el producto."}

                </div>

              </div>

              {/* Stock mínimo */}
              <div className="col-md-6 mb-3">

                <label
                  htmlFor="stockMinimo"
                  className="form-label"
                >
                  Stock mínimo *
                </label>

                <input
                  id="stockMinimo"
                  type="number"
                  className="form-control"
                  min="0"
                  step="1"
                  value={formulario.stockMinimo}
                  onChange={(e) =>
                    manejarCambio(
                      "stockMinimo",
                      Number(e.target.value)
                    )
                  }
                  required
                />

                <div className="form-text">
                  Cantidad mínima utilizada como referencia
                  para controlar el inventario.
                </div>

              </div>

            </div>

            {/* Información del estado en edición */}
            {modoEdicion && (
              <div className="alert alert-info">

                <strong>Importante:</strong>{" "}

                El estado del producto es calculado
                automáticamente por el Backend según
                su stock y si está activo.

              </div>
            )}

            {/* Botones */}
            <div className="d-flex gap-2 mt-4">

              {/* Guardar */}
              <button
                type="submit"
                className="btn btn-primary"
                disabled={guardando}
              >

                {guardando
                  ? "Guardando..."
                  : "Guardar producto"}

              </button>

              {/* Cancelar */}
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => navegar("/productos")}
                disabled={guardando}
              >
                Cancelar
              </button>

            </div>

          </form>

        </div>

      </main>

    </div>
  );
}