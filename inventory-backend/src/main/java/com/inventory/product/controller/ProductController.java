package com.inventory.product.controller;

import com.inventory.product.dto.ProductDTO;
import com.inventory.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST del módulo Productos.
 *
 * Esta clase recibe las solicitudes HTTP realizadas por el frontend
 * y las dirige hacia ProductService.
 *
 * El Controller no contiene las reglas principales del negocio.
 * Su responsabilidad es:
 *
 * - Recibir las solicitudes.
 * - Recibir los datos enviados por el frontend.
 * - Validar el DTO mediante @Valid.
 * - Llamar al Service correspondiente.
 * - Construir la respuesta HTTP.
 *
 * Ruta base del módulo:
 *
 * /api/v1/productos
 */
@RestController
@RequestMapping("/api/v1/productos")
public class ProductController {

    /**
     * Service que contiene la lógica de negocio
     * del módulo Productos.
     */
    private final ProductService productService;

    /**
     * Constructor utilizado por Spring para inyectar
     * ProductService.
     *
     * @param productService servicio de productos
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // =========================================================
    // CONSULTAR PRODUCTOS ACTIVOS
    // =========================================================

    /**
     * Consulta todos los productos activos.
     *
     * Endpoint:
     * GET /api/v1/productos
     *
     * @return lista de productos activos
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> findAllActive() {

        List<ProductDTO> productos =
                productService.findAllActive();

        return ResponseEntity.ok(
                success(
                        "Productos consultados correctamente.",
                        productos
                )
        );
    }

    // =========================================================
    // CONSULTAR TODOS LOS PRODUCTOS
    // =========================================================

    /**
     * Consulta todos los productos registrados,
     * incluyendo los productos inactivos.
     *
     * Endpoint:
     * GET /api/v1/productos/todas
     *
     * @return lista completa de productos
     */
    @GetMapping("/todas")
    public ResponseEntity<Map<String, Object>> findAllIncludingInactive() {

        List<ProductDTO> productos =
                productService.findAllIncludingInactive();

        return ResponseEntity.ok(
                success(
                        "Todos los productos fueron consultados correctamente.",
                        productos
                )
        );
    }

    // =========================================================
    // BUSCAR PRODUCTOS
    // =========================================================

    /**
     * Busca productos utilizando un criterio.
     *
     * El criterio puede coincidir con:
     *
     * - Código.
     * - Nombre.
     * - Categoría.
     *
     * Endpoint:
     * GET /api/v1/productos/buscar?criterio=texto
     *
     * @param criterio texto utilizado para realizar la búsqueda
     * @return productos encontrados
     */
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscar(
            @RequestParam String criterio) {

        List<ProductDTO> productos =
                productService.buscar(criterio);

        return ResponseEntity.ok(
                success(
                        "Búsqueda realizada correctamente.",
                        productos
                )
        );
    }

    // =========================================================
    // CONSULTAR PRODUCTO POR ID
    // =========================================================

    /**
     * Consulta un producto específico utilizando su ID.
     *
     * Endpoint:
     * GET /api/v1/productos/{id}
     *
     * @param id identificador del producto
     * @return información del producto
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(
            @PathVariable Long id) {

        ProductDTO producto =
                productService.findById(id);

        return ResponseEntity.ok(
                success(
                        "Producto consultado correctamente.",
                        producto
                )
        );
    }

    // =========================================================
    // CREAR PRODUCTO
    // =========================================================

    /**
     * Registra un nuevo producto.
     *
     * Endpoint:
     * POST /api/v1/productos
     *
     * @Valid permite ejecutar las validaciones definidas
     * en ProductDTO antes de enviar la información al Service.
     *
     * @param dto información del producto
     * @return producto creado
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @Valid @RequestBody ProductDTO dto) {

        ProductDTO creado =
                productService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        success(
                                "Producto registrado correctamente.",
                                creado
                        )
                );
    }

    // =========================================================
    // ACTUALIZAR PRODUCTO
    // =========================================================

    /**
     * Actualiza la información de un producto existente.
     *
     * Endpoint:
     * PUT /api/v1/productos/{id}
     *
     * El Service controla qué campos pueden modificarse.
     * Por ejemplo, el stock actual no se modifica mediante
     * este endpoint.
     *
     * @param id identificador del producto
     * @param dto nueva información
     * @return producto actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO dto) {

        ProductDTO actualizado =
                productService.update(id, dto);

        return ResponseEntity.ok(
                success(
                        "Producto actualizado correctamente.",
                        actualizado
                )
        );
    }

    // =========================================================
    // DESACTIVAR PRODUCTO
    // =========================================================

    /**
     * Desactiva un producto mediante eliminación lógica.
     *
     * El producto no se elimina físicamente de la base de datos.
     *
     * Endpoint:
     * DELETE /api/v1/productos/{id}
     *
     * @param id identificador del producto
     * @return mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deactivate(
            @PathVariable Long id) {

        productService.deactivate(id);

        return ResponseEntity.ok(
                success(
                        "Producto desactivado correctamente.",
                        null
                )
        );
    }

    // =========================================================
    // REACTIVAR PRODUCTO
    // =========================================================

    /**
     * Reactiva un producto previamente desactivado.
     *
     * Endpoint:
     * PUT /api/v1/productos/{id}/reactivar
     *
     * @param id identificador del producto
     * @return mensaje de confirmación
     */
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Map<String, Object>> reactivate(
            @PathVariable Long id) {

        productService.reactivate(id);

        return ResponseEntity.ok(
                success(
                        "Producto reactivado correctamente.",
                        null
                )
        );
    }

    // =========================================================
    // RESPUESTA ESTÁNDAR
    // =========================================================

    /**
     * Construye la respuesta estándar utilizada por
     * los módulos del proyecto INVENTORY.
     *
     * La respuesta mantiene siempre la misma estructura:
     *
     * {
     *     "success": true,
     *     "message": "...",
     *     "data": ...
     * }
     *
     * Esto facilita que el frontend pueda manejar
     * las respuestas de los diferentes módulos.
     *
     * @param mensaje mensaje de respuesta
     * @param data información que se devolverá
     * @return mapa con la respuesta estándar
     */
    private Map<String, Object> success(
            String mensaje,
            Object data) {

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put("success", true);
        respuesta.put("message", mensaje);
        respuesta.put("data", data);

        return respuesta;
    }
}