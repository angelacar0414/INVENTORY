/**
 * INTERFAZ: Producto
 * -------------------
 * Representa la información de un producto que maneja
 * el Frontend de INVENTORY.
 *
 * Esta interfaz debe coincidir con la información que
 * devuelve el ProductDTO del Backend.
 */
export interface IProducto {
  // Identificador único del producto.
  idProducto: number;

  // Código generado automáticamente por el Backend.
  codigo: string;

  // Nombre del producto.
  nombre: string;

  // Descripción opcional del producto.
  descripcion: string;

  // Precio de compra del producto.
  precioCompra: number;

  // Precio de venta del producto.
  precioVenta: number;

  // Cantidad actual disponible en inventario.
  stockActual: number;

  // Cantidad mínima definida para el producto.
  stockMinimo: number;

  // Identificador de la categoría asociada.
  idCategoria: number;

  // Nombre de la categoría.
  nombreCategoria: string;

  // Identificador del proveedor asociado.
  idProveedor: number;

  // Nombre del proveedor.
  nombreProveedor: string;

  // Estado calculado por el Backend.
  estado: "DISPONIBLE" | "AGOTADO" | "INACTIVO";

  // Indica si el producto está activo.
  activo: boolean;
}

/**
 * INTERFAZ: Formulario de Producto
 * ----------------------------------
 * Representa los datos que el usuario puede enviar
 * cuando crea o edita un producto.
 *
 * El código, estado y activo NO se envían desde el
 * formulario porque son controlados por el Backend.
 *
 * El stockActual solamente se utiliza inicialmente
 * al crear el producto. Posteriormente será controlado
 * mediante el módulo de Movimientos.
 */
export interface IProductoFormulario {
  // Nombre del producto.
  nombre: string;

  // Descripción del producto.
  descripcion: string;

  // Precio de compra.
  precioCompra: number;

  // Precio de venta.
  precioVenta: number;

  // Stock inicial.
  //
  // Es opcional porque al editar un producto
  // no debemos modificar directamente su stock.
  stockActual?: number;

  // Stock mínimo permitido.
  stockMinimo: number;

  // Categoría seleccionada.
  idCategoria: number;

  // Proveedor seleccionado.
  idProveedor: number;
}