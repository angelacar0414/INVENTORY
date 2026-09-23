package com.inventory.client.controller;

import com.inventory.client.dto.ClientDTO;
import com.inventory.client.service.ClientService;
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
 * Clientes. Este Controller no contiene lógica de negocio, solo
 * recibe la solicitud, la pasa al Service y devuelve la respuesta en
 * el formato estándar que manejamos en todo INVENTORY: success,
 * message y data.
 *
 * @author Angela Carvajal Ortiz
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/clientes")
public class ClientController {

    @Autowired
    private ClientService clientService;

    // GET /api/v1/clientes : consulto todos los clientes registrados.
    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll() {
        List<ClientDTO> clientes = clientService.findAll();
        return ResponseEntity.ok(success("Clientes consultados correctamente.", clientes));
    }

    // GET /api/v1/clientes/activos : consulto solo los que están activos.
    @GetMapping("/activos")
    public ResponseEntity<Map<String, Object>> findAllActive() {
        List<ClientDTO> clientes = clientService.findAllActive();
        return ResponseEntity.ok(success("Clientes activos consultados correctamente.", clientes));
    }

    // GET /api/v1/clientes/buscar?nombre=... : busco clientes por nombre.
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscar(@RequestParam String nombre) {
        List<ClientDTO> clientes = clientService.buscarPorNombre(nombre);
        return ResponseEntity.ok(success("Búsqueda realizada correctamente.", clientes));
    }

    // GET /api/v1/clientes/{id} : consulto un cliente específico.
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable Long id) {
        ClientDTO cliente = clientService.findById(id);
        return ResponseEntity.ok(success("Cliente consultado correctamente.", cliente));
    }

    // POST /api/v1/clientes : registro un nuevo cliente.
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody ClientDTO dto) {
        ClientDTO creado = clientService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(success("Cliente registrado correctamente.", creado));
    }

    // PUT /api/v1/clientes/{id} : edito un cliente existente.
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
                                                        @Valid @RequestBody ClientDTO dto) {
        ClientDTO actualizado = clientService.update(id, dto);
        return ResponseEntity.ok(success("Cliente actualizado correctamente.", actualizado));
    }

    // DELETE /api/v1/clientes/{id} : desactivo el cliente (eliminación lógica).
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deactivate(@PathVariable Long id) {
        clientService.deactivate(id);
        return ResponseEntity.ok(success("Cliente desactivado correctamente.", null));
    }

    // PUT /api/v1/clientes/{id}/reactivar : vuelvo a activar el cliente.
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Map<String, Object>> reactivate(@PathVariable Long id) {
        clientService.reactivate(id);
        return ResponseEntity.ok(success("Cliente reactivado correctamente.", null));
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
