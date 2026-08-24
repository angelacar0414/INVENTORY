// INTERFAZ (TYPE)
// -----------------
// Describe la "forma" que tiene un objeto Categoría en el Frontend.
// TypeScript usa esto para avisarnos en el editor si nos equivocamos
// escribiendo el nombre de un campo o usando el tipo de dato incorrecto.
//
// Coincide con el CategoryDTO que devuelve el Backend (Spring Boot).

export interface ICategoria {
  id: number;
  nombre: string;
  descripcion: string;
  activo: boolean;
}

// Este tipo se usa para el formulario, donde el id todavía no existe
// (cuando se está creando una categoría nueva).
export interface ICategoriaFormulario {
  id?: number;
  nombre: string;
  descripcion: string;
}
