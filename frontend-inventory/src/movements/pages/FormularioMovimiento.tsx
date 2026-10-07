import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { Sidebar } from "../../components/Sidebar";
import { movimientoService } from "../services/movimientoService";
import type {
  IMovimientoFormulario,
  TipoMovimiento,
} from "../types/IMovimiento";

import { productoService } from "../../products/services/productoService";
import type { IProducto } from "../../products/types/IProducto";

import { clienteService } from "../../clients/services/clienteService";
import type { ICliente } from "../../clients/types/ICliente";

/**
 * Propiedades del formulario.
 *
 * El mismo formulario sirve para registrar entradas y salidas.
 * El tipo lo define la ruta desde App.tsx:
 *
 * /movimientos/entrada → tipo = "ENTRADA"
 * /movimientos/salida  → tipo = "SALIDA"
 */
interface Props {
  tipo: TipoMovimiento;
}

/**
 * PÁGINA: Formulario de Movimiento
 * --------------------------------
 * Esta página permite registrar:
 *
 * - Una ENTRADA: aumenta el stock del producto.
 * - Una SALIDA: disminuye el stock y exige un cliente.
 *
 * El frontend solo hace validaciones básicas para ayudar al usuario.
 * Las reglas definitivas (stock suficiente, cliente obligatorio,
 * producto activo, etc.) las aplica el Backend.
 *
 * El usuario que registra el movimiento no se envía desde aquí:
 * el Backend lo toma de la sesión iniciada.
 */
export function FormularioMovimiento({ tipo }: Props) {

  // Permite navegar entre las diferentes páginas.
  const navegar = useNavigate();

  // Determina si estamos registrando una salida o una entrada.
  const esSalida = tipo === "SALIDA";

  // Productos activos disponibles para seleccionar.
  const [productos, setProductos] = useState<IProducto[]>([]);

  // Clientes activos (solo se cargan para las salidas).
  const [clientes, setClientes] = useState<ICliente[]>([]);

  // Campos del formulario.
  //
  // La cantidad se guarda como texto para que el usuario pueda
  // borrar el campo y escribir con normalidad; la convertimos a
  // número únicamente al validar y al enviar.
  const [idProducto, setIdProducto] = useState(0);
  const [cantidad, setCantidad] = useState("");
  const [idCliente, setIdCliente] = useState(0);
  const [observacion, setObservacion] = useState("");

  // Indica si se están cargando los datos iniciales.
  const [cargando, setCargando] = useState(true);

  // Controla el envío del formulario.
  const [guardando, setGuardando] = useState(false);

  // Se vuelve true cuando el movimiento ya fue registrado.
  //
  // Un movimiento no se puede deshacer, por eso bloqueamos el
  // botón mientras esperamos para volver a la lista y así evitar
  // registrarlo dos veces por un doble clic.
  const [registrado, setRegistrado] = useState(false);

  // Mensaje de error general.
  const [error, setError] = useState("");

  // Mensaje de éxito.
  const [mensaje, setMensaje] = useState("");

  /**
   * CARGAR DATOS DEL FORMULARIO
   * ---------------------------
   * Para registrar un movimiento necesitamos mostrar únicamente
   * productos activos y, en las salidas, clientes activos.
   *
   * Se reutilizan los servicios que ya existen en el proyecto.
   */
  useEffect(() => {

    const cargarDatosFormulario = async () => {

      try {

        setCargando(true);
        setError("");

        const [productosResultado, clientesResultado] =
          await Promise.all([
            productoService.listar(),
            esSalida
              ? clienteService.listar()
              : Promise.resolve([] as ICliente[]),
          ]);

        setProductos(productosResultado);
        setClientes(clientesResultado);

      } catch (error: any) {

        const mensajeError =
          error?.response?.data?.message ||
          "No fue posible cargar los productos y clientes.";

        setError(mensajeError);

      } finally {

        setCargando(false);
      }
    };

    cargarDatosFormulario();

  }, [esSalida]);

  /**
   * DATOS CALCULADOS PARA AYUDAR AL USUARIO
   * ---------------------------------------
   * Con el producto y la cantidad seleccionados mostramos cuál
   * será el stock después del movimiento. Es solo una ayuda
   * visual: el Backend calcula el valor real.
   */
  const productoSeleccionado = productos.find(
    (producto) => producto.idProducto === idProducto
  );

  const cantidadNumero = Number(cantidad);

  const cantidadValida =
    cantidad.trim() !== "" &&
    Number.isInteger(cantidadNumero) &&
    cantidadNumero > 0;

  // Stock que quedaría después del movimiento (o null si todavía
  // falta elegir el producto o escribir una cantidad válida).
  const stockResultante =
    productoSeleccionado && cantidadValida
      ? esSalida
        ? productoSeleccionado.stockActual - cantidadNumero
        : productoSeleccionado.stockActual + cantidadNumero
      : null;

  // Una salida no puede superar el stock disponible.
  const superaStock =
    esSalida && stockResultante !== null && stockResultante < 0;

  // Aviso informativo: el stock quedaría en el mínimo o por debajo.
  const stockQuedaBajo =
    productoSeleccionado !== undefined &&
    stockResultante !== null &&
    stockResultante >= 0 &&
    stockResultante <= productoSeleccionado.stockMinimo;

  /**
   * LIMPIAR MENSAJES
   * ----------------
   * Cuando el usuario vuelve a modificar el formulario, quitamos
   * los mensajes anteriores.
   */
  const limpiarMensajes = () => {
    setError("");
    setMensaje("");
  };

  /**
   * VALIDACIÓN DEL FORMULARIO
   * --------------------------
   * Validaciones básicas antes de enviar la información al Backend.
   */
  const validarFormulario = (): boolean => {

    if (!idProducto) {
      setError("Debe seleccionar un producto.");
      return false;
    }

    if (!cantidadValida) {
      setError(
        "La cantidad debe ser un número entero mayor que cero."
      );
      return false;
    }

    if (esSalida && !idCliente) {
      setError("Debe seleccionar un cliente para registrar una salida.");
      return false;
    }

    if (superaStock && productoSeleccionado) {
      setError(
        `La cantidad supera el stock disponible (${productoSeleccionado.stockActual}).`
      );
      return false;
    }

    return true;
  };

  /**
   * REGISTRAR MOVIMIENTO
   * --------------------
   * Envía la entrada o la salida al Backend según el tipo de
   * formulario.
   */
  const guardarMovimiento = async (
    evento: React.FormEvent
  ) => {

    evento.preventDefault();

    limpiarMensajes();

    // Primero realizamos las validaciones.
    if (!validarFormulario()) {
      return;
    }

    try {

      setGuardando(true);

      const datosParaEnviar: IMovimientoFormulario = {
        idProducto,
        cantidad: cantidadNumero,
        observacion: observacion.trim() || undefined,
      };

      // El cliente solo se envía en las salidas.
      if (esSalida) {
        datosParaEnviar.idCliente = idCliente;
      }

      const registro = esSalida
        ? await movimientoService.registrarSalida(datosParaEnviar)
        : await movimientoService.registrarEntrada(datosParaEnviar);

      setRegistrado(true);

      setMensaje(
        `${esSalida ? "Salida" : "Entrada"} registrada correctamente. ` +
        `Stock de ${registro.nombreProducto}: ` +
        `${registro.stockAnterior} → ${registro.stockPosterior}.`
      );

      /**
       * Después de guardar esperamos un momento para que el usuario
       * pueda leer el mensaje y volvemos al historial.
       */
      setTimeout(() => {
        navegar("/movimientos");
      }, 1200);

    } catch (error: any) {

      /**
       * Spring Boot devuelve el mensaje del error dentro de
       * response.data.message (por ejemplo: "Stock insuficiente...").
       */
      const mensajeError =
        error?.response?.data?.message ||
        "No fue posible registrar el movimiento.";

      setError(mensajeError);

    } finally {

      setGuardando(false);
    }
  };

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
              {esSalida
                ? "Registrar salida"
                : "Registrar entrada"}
            </h1>

            <p className="text-muted mb-0">
              {esSalida
                ? "Registra los productos que se entregan a un cliente. El stock disminuirá."
                : "Registra los productos que ingresan al inventario. El stock aumentará."}
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

          {/* No hay productos para mover */}
          {productos.length === 0 && !error && (
            <div className="alert alert-warning">
              No hay productos activos. Registre un producto
              antes de crear movimientos.
            </div>
          )}

          {/* Una salida necesita al menos un cliente */}
          {esSalida && clientes.length === 0 && !error && (
            <div className="alert alert-warning">
              No hay clientes activos. Para registrar una salida
              primero{" "}
              <Link to="/clientes/nuevo">
                registre un cliente
              </Link>
              .
            </div>
          )}

          <form onSubmit={guardarMovimiento}>

            {/* Producto */}
            <div className="mb-3">

              <label
                htmlFor="producto"
                className="form-label"
              >
                Producto *
              </label>

              <select
                id="producto"
                className="form-select"
                value={idProducto}
                onChange={(e) => {
                  setIdProducto(Number(e.target.value));
                  limpiarMensajes();
                }}
                required
              >

                <option value={0}>
                  Seleccione un producto
                </option>

                {productos.map((producto) => (

                  <option
                    key={producto.idProducto}
                    value={producto.idProducto}
                    disabled={
                      esSalida && producto.stockActual === 0
                    }
                  >
                    {producto.codigo} - {producto.nombre}
                    {" "}(Stock: {producto.stockActual})
                    {esSalida && producto.stockActual === 0
                      ? " - Agotado"
                      : ""}
                  </option>

                ))}

              </select>

            </div>

            {/* Cantidad y cliente */}
            <div className="row">

              {/* Cantidad */}
              <div className="col-md-6 mb-3">

                <label
                  htmlFor="cantidad"
                  className="form-label"
                >
                  Cantidad *
                </label>

                <input
                  id="cantidad"
                  type="number"
                  className="form-control"
                  min="1"
                  step="1"
                  value={cantidad}
                  onChange={(e) => {
                    setCantidad(e.target.value);
                    limpiarMensajes();
                  }}
                  placeholder="Unidades"
                  required
                />

              </div>

              {/* Cliente (solo en salidas) */}
              {esSalida && (

                <div className="col-md-6 mb-3">

                  <label
                    htmlFor="cliente"
                    className="form-label"
                  >
                    Cliente *
                  </label>

                  <select
                    id="cliente"
                    className="form-select"
                    value={idCliente}
                    onChange={(e) => {
                      setIdCliente(Number(e.target.value));
                      limpiarMensajes();
                    }}
                    required
                  >

                    <option value={0}>
                      Seleccione un cliente
                    </option>

                    {clientes.map((cliente) => (

                      <option
                        key={cliente.idCliente}
                        value={cliente.idCliente}
                      >
                        {cliente.nombre}
                        {cliente.documento
                          ? ` (${cliente.documento})`
                          : ""}
                      </option>

                    ))}

                  </select>

                </div>

              )}

            </div>

            {/* Resumen del stock */}
            {productoSeleccionado && (

              <div
                className={
                  superaStock
                    ? "alert alert-danger"
                    : "alert alert-info"
                }
              >

                <div>
                  Stock actual:{" "}
                  <strong>
                    {productoSeleccionado.stockActual}
                  </strong>
                  {" · "}
                  Stock mínimo:{" "}
                  <strong>
                    {productoSeleccionado.stockMinimo}
                  </strong>
                </div>

                {stockResultante !== null && (
                  <div>
                    {superaStock ? (
                      <>
                        La cantidad supera el stock disponible.
                      </>
                    ) : (
                      <>
                        Stock después del movimiento:{" "}
                        <strong>{stockResultante}</strong>
                      </>
                    )}
                  </div>
                )}

                {stockQuedaBajo && (
                  <div className="mt-1">
                    <strong>Atención:</strong> el stock quedará en
                    el mínimo o por debajo.
                  </div>
                )}

              </div>

            )}

            {/* Observación */}
            <div className="mb-3">

              <label
                htmlFor="observacion"
                className="form-label"
              >
                Observación
              </label>

              <textarea
                id="observacion"
                className="form-control"
                rows={3}
                maxLength={255}
                value={observacion}
                onChange={(e) => {
                  setObservacion(e.target.value);
                  limpiarMensajes();
                }}
                placeholder={
                  esSalida
                    ? "Ejemplo: Entrega pedido #123"
                    : "Ejemplo: Compra al proveedor"
                }
              />

            </div>

            <div className="alert alert-secondary">

              <strong>Importante:</strong>{" "}

              Un movimiento registrado no se puede modificar ni
              eliminar. Revise los datos antes de guardar.

            </div>

            {/* Botones */}
            <div className="d-flex gap-2 mt-4">

              {/* Guardar */}
              <button
                type="submit"
                className="btn btn-primary"
                disabled={guardando || registrado}
              >

                {guardando
                  ? "Guardando..."
                  : esSalida
                    ? "Registrar salida"
                    : "Registrar entrada"}

              </button>

              {/* Cancelar */}
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => navegar("/movimientos")}
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
