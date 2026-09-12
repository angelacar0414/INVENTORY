package com.inventory.supplier.controller;

import com.inventory.supplier.dto.SupplierDTO;
import com.inventory.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Expongo mediante la API REST las operaciones del módulo de
 * Proveedores. Este Controller no contiene lógica de negocio, solo
 * recibe la solicitud, la pasa al Service y devuelve la respuesta en
 * el formato estándar que manejamos en todo INVENTORY: success,
 * message y data.
 *
 * @author Angela Carvajal Ortiz
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/proveedores")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    // GET /api/v1/proveedores : consulto todos los proveedores registrados.
    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll() {
        List<SupplierDTO> proveedores = supplierService.findAll();
        return ResponseEntity.ok(success("Proveedores consultados correctamente.", proveedores));
    }

    // GET /api/v1/proveedores/activos : consulto solo los que están activos.
    @GetMapping("/activos")
    public ResponseEntity<Map<String, Object>> findAllActive() {
        List<SupplierDTO> proveedores = supplierService.findAllActive();
        return ResponseEntity.ok(success("Proveedores activos consultados correctamente.", proveedores));
    }

    // GET /api/v1/proveedores/buscar?nombre=... : busco proveedores por nombre.
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscar(@RequestParam String nombre) {
        List<SupplierDTO> proveedores = supplierService.buscarPorNombre(nombre);
        return ResponseEntity.ok(success("Búsqueda realizada correctamente.", proveedores));
    }

    // GET /api/v1/proveedores/{id} : consulto un proveedor específico.
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable Long id) {
        SupplierDTO proveedor = supplierService.findById(id);
        return ResponseEntity.ok(success("Proveedor consultado correctamente.", proveedor));
    }

    // POST /api/v1/proveedores : registro un nuevo proveedor.
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody SupplierDTO dto) {
        SupplierDTO creado = supplierService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(success("Proveedor registrado correctamente.", creado));
    }

    // PUT /api/v1/proveedores/{id} : edito un proveedor existente.
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
                                                        @Valid @RequestBody SupplierDTO dto) {
        SupplierDTO actualizado = supplierService.update(id, dto);
        return ResponseEntity.ok(success("Proveedor actualizado correctamente.", actualizado));
    }

    // DELETE /api/v1/proveedores/{id} : desactivo el proveedor (eliminación lógica).
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deactivate(@PathVariable Long id) {
        supplierService.deactivate(id);
        return ResponseEntity.ok(success("Proveedor desactivado correctamente.", null));
    }

    // PUT /api/v1/proveedores/{id}/reactivar : vuelvo a activar el proveedor.
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Map<String, Object>> reactivate(@PathVariable Long id) {
        supplierService.reactivate(id);
        return ResponseEntity.ok(success("Proveedor reactivado correctamente.", null));
    }

    // Armo la respuesta en el formato estándar que manejamos en todo el
    // proyecto INVENTORY, para que el Frontend siempre reciba la misma
    // estructura: success, message y data.
    private Map<String, Object> success(String mensaje, Object data) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("success", true);
        respuesta.put("message", mensaje);
        respuesta.put("data", data);
        return respuesta;
    }
}
