// INTERFAZ (TYPE)
// -----------------
// Describe la "forma" que tiene un objeto Proveedor en el Frontend.
// TypeScript usa esto para avisarnos en el editor si nos equivocamos
// escribiendo el nombre de un campo o usando el tipo de dato incorrecto.
//
// Coincide con el SupplierDTO que devuelve el Backend (Spring Boot).

export interface IProveedor {
  id: number;
  nombre: string;
  documento: string;
  telefono: string;
  correo: string;
  direccion: string;
  activo: boolean;
}

// Este tipo se usa para el formulario, donde el id todavía no existe
// (cuando se está creando un proveedor nuevo).
export interface IProveedorFormulario {
  id?: number;
  nombre: string;
  documento: string;
  telefono: string;
  correo: string;
  direccion: string;
}