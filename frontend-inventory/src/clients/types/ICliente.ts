// TYPES (interfaces del módulo Clientes)
// -----------------------------
// Igual que con Proveedores, separamos la entidad completa (ICliente,
// tal como la devuelve el Backend, con id y con el campo activo) del
// dato que realmente llenamos en el formulario (IClienteFormulario,
// que es solo lo que el usuario escribe).
//
// "direccion" la dejamos en ICliente porque el campo sigue existiendo
// en la base de datos, pero la quitamos de IClienteFormulario porque
// decidimos que no es un dato necesario para pedirle al cliente.

export interface ICliente {
  idCliente: number;
  nombre: string;
  documento?: string;
  telefono?: string;
  correo?: string;
  direccion?: string;
  activo: boolean;
}

export interface IClienteFormulario {
  nombre: string;
  documento?: string;
  telefono?: string;
  correo?: string;
}