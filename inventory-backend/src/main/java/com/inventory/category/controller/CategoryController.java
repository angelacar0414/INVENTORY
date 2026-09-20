package com.inventory.category.controller;

import com.inventory.category.dto.CategoryDTO;
import com.inventory.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CLASE CONTROLLER
 * -----------------
 * Es la "puerta de entrada" del módulo. Recibe las peticiones HTTP
 * que manda React (Frontend) y las pasa al Service. No decide nada
 * de lógica de negocio aquí, solo recibe y responde.
 *
 * Las rutas de la API o enpoints que permiten al sistema trabajar con las categorias:
 *   GET    /api/v1/categorias
 *   GET    /api/v1/categorias/{id}
 *   POST   /api/v1/categorias
 *   PUT    /api/v1/categorias/{id}
 *   DELETE /api/v1/categorias/{id}   (elimina lógicamente, no física)
 */
@RestController
@RequestMapping("/api/v1/categorias")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET /api/v1/categorias
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<CategoryDTO> categorias = categoryService.findAllActive();
        return ResponseEntity.ok(success("Categorías obtenidas correctamente.", categorias));
    }
    // GET /api/v1/categorias/todas
    // Trae TODAS las categorías, incluyendo las inactivas.
    // Se usa cuando el usuario activa el botón "Ver todas (incluye inactivas)".
    @GetMapping("/todas")
    public ResponseEntity<Map<String, Object>> getAllIncludingInactive() {
        List<CategoryDTO> categorias = categoryService.findAllIncludingInactive();
        return ResponseEntity.ok(success("Categorías obtenidas correctamente.", categorias));
    }
    // GET /api/v1/categorias/buscar?nombre=...
    // Busco categorías por nombre (búsqueda parcial), para el
    // campo "Buscar categoría" del Frontend.
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscar(@RequestParam String nombre) {
        List<CategoryDTO> categorias = categoryService.buscarPorNombre(nombre);
        return ResponseEntity.ok(success("Búsqueda realizada correctamente.", categorias));
    }

    // GET /api/v1/categorias/5
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        CategoryDTO categoria = categoryService.findById(id);
        return ResponseEntity.ok(success("Categoría encontrada.", categoria));
    }

    // POST /api/v1/categorias
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CategoryDTO dto) {
        CategoryDTO creada = categoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(success("Categoría registrada correctamente.", creada));
    }

    // PUT /api/v1/categorias/5
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
                                                        @Valid @RequestBody CategoryDTO dto) {
        CategoryDTO actualizada = categoryService.update(id, dto);
        return ResponseEntity.ok(success("Categoría actualizada correctamente.", actualizada));
    }

    // DELETE /api/v1/categorias/5  (eliminación lógica: pone activo = false)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deactivate(@PathVariable Long id) {
        categoryService.deactivate(id);
        return ResponseEntity.ok(success("Categoría desactivada correctamente.", null));
    }
    // PUT /api/v1/categorias/5/reactivar  (vuelve a poner activo = true)
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Map<String, Object>> reactivate(@PathVariable Long id) {
        categoryService.reactivate(id);
        return ResponseEntity.ok(success("Categoria reactivada correctamente.", null));
    }

    // Método pequeño para no repetir el formato de respuesta en cada endpoint
    private Map<String, Object> success(String mensaje, Object data) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", mensaje);
        body.put("data", data);
        return body;
    }
}
