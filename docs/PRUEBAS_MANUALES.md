# INVENTORY - Pruebas manuales (Reportes, Perfil, Dashboard y Paginación)

Pruebas realizadas según la estrategia definida en el EKB (pruebas manuales con Postman y navegador).
Marcar cada caso con ✔ cuando se verifique.

**Preparación:** Backend en `http://localhost:8080`, Frontend en `http://localhost:5173`, un usuario ADMINISTRADOR y uno OPERADOR.

## 1. Dashboard (RF-027)

| # | Caso | Resultado esperado | OK |
|---|------|--------------------|----|
| 1.1 | Entrar al Dashboard con cualquier rol | Se muestran productos, categorías, proveedores, clientes, stock bajo, agotados, entradas y salidas de hoy | |
| 1.2 | Crear una categoría y volver al Dashboard | El total de categorías aumenta en 1 | |
| 1.3 | Desactivar un proveedor y volver al Dashboard | El total de proveedores disminuye en 1 | |

## 2. Paginación de listas

| # | Caso | Resultado esperado | OK |
|---|------|--------------------|----|
| 2.1 | Lista con más de 10 registros (Categorías, Proveedores, Clientes, Productos, Usuarios) | Muestra 10 filas y "Página 1 de N" | |
| 2.2 | Pulsar "Siguiente" hasta la última página | Muestra las filas restantes y "Siguiente" queda deshabilitado | |
| 2.3 | Escribir en el buscador | Vuelve a la página 1 con los resultados filtrados | |
| 2.4 | Lista con 10 registros o menos | No aparecen los botones de paginación | |

## 3. Perfil (RF-032, RF-033, RF-034)

| # | Caso | Resultado esperado | OK |
|---|------|--------------------|----|
| 3.1 | Abrir "Mi perfil" con ADMINISTRADOR y con OPERADOR | Muestra los datos del usuario con sesión; usuario y rol deshabilitados | |
| 3.2 | Cambiar nombre y apellido y guardar | Mensaje de éxito; los datos quedan guardados al recargar | |
| 3.3 | Dejar un campo vacío y guardar | Mensaje de campo obligatorio | |
| 3.4 | Escribir un correo sin formato válido | Mensaje de formato inválido | |
| 3.5 | Usar el correo de otro usuario | Mensaje "ya está registrado por otro usuario" (409) | |
| 3.6 | Cambiar contraseña con la actual incorrecta | Mensaje "La contraseña actual no es correcta." | |
| 3.7 | Nueva contraseña de menos de 6 caracteres | Mensaje de longitud mínima | |
| 3.8 | Confirmación distinta a la nueva | Mensaje "La confirmación no coincide..." | |
| 3.9 | Nueva igual a la actual | Mensaje "debe ser diferente a la actual" | |
| 3.10 | Cambio correcto, cerrar sesión e ingresar con la nueva | Ingresa; la contraseña anterior ya no funciona | |
| 3.11 | Verificar en MySQL la columna de contraseña | Aparece cifrada (BCrypt), nunca en texto plano | |

**Postman:** `GET /api/v1/perfil`, `PUT /api/v1/perfil`, `PUT /api/v1/perfil/contrasena`. Sin sesión deben responder 401.

## 4. Reportes (RF-028 a RF-031)

| # | Caso | Resultado esperado | OK |
|---|------|--------------------|----|
| 4.1 | Entrar a Reportes con OPERADOR | Mensaje "Solo el administrador puede ver los reportes." (403) | |
| 4.2 | Generar reporte de productos sin filtros | KPIs, barra de estados y tabla con todos los productos | |
| 4.3 | Filtrar por estado, proveedor, categoría y "Solo stock bajo" | La tabla solo muestra los productos que cumplen | |
| 4.4 | Descargar PDF de productos | Archivo `reporte-productos-AAAAMMDD-HHMM.pdf` con gráficas y tabla | |
| 4.5 | Descargar Excel de productos | Archivo `.xlsx` con hojas "Resumen" y "Datos" | |
| 4.6 | Generar reporte de movimientos (últimos 30 días por defecto) | KPIs y tabla de movimientos | |
| 4.7 | Probar los atajos Hoy, 7 días, 30 días, Este mes y Todo | El rango de fechas cambia | |
| 4.8 | Fecha inicial posterior a la final | Mensaje de validación | |
| 4.9 | Tipo "Entradas" | El filtro de cliente se deshabilita | |
| 4.10 | Descargar PDF y Excel de movimientos | Los archivos coinciden con lo que se ve en pantalla | |
| 4.11 | Cambiar un filtro sin volver a generar | Aviso de filtros pendientes y descargas deshabilitadas | |

## 5. Registro de eventos y errores (SSD 6.4 y 8.6)

| # | Caso | Resultado esperado | OK |
|---|------|--------------------|----|
| 5.1 | Iniciar sesión correctamente | La consola del Backend registra el inicio de sesión | |
| 5.2 | Iniciar sesión con contraseña incorrecta | La consola registra el error de autenticación | |
| 5.3 | Cerrar sesión | La consola registra el cierre de sesión | |
| 5.4 | Provocar un error (por ejemplo, con Postman enviar datos inválidos) | La respuesta incluye `success`, `message` y `errors` | |
