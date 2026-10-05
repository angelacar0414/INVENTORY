package com.inventory.product.service;

import com.inventory.category.entity.CategoryEntity;
import com.inventory.category.repository.CategoryRepository;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.product.dto.ProductDTO;
import com.inventory.product.entity.ProductEntity;
import com.inventory.product.entity.ProductStatus;
import com.inventory.product.mapper.ProductMapper;
import com.inventory.product.repository.ProductRepository;
import com.inventory.supplier.entity.SupplierEntity;
import com.inventory.supplier.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase Service del módulo Productos.
 *
 * Aquí se concentra la lógica de negocio relacionada con
 * la creación, consulta, actualización y desactivación
 * de los productos del sistema INVENTORY.
 *
 * El Controller recibe las solicitudes HTTP y delega
 * las operaciones a esta clase.
 *
 * El Service se comunica con:
 *
 * - ProductRepository
 * - CategoryRepository
 * - SupplierRepository
 * - ProductMapper
 *
 * De esta manera se mantiene separada la lógica de negocio
 * de la comunicación con la base de datos y de la API REST.
 */
@Service
public class ProductService {

    /**
     * Repositorio encargado de consultar y guardar productos.
     */
    private final ProductRepository productRepository;

    /**
     * Repositorio utilizado para verificar las categorías
     * asociadas a los productos.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Repositorio utilizado para verificar los proveedores
     * asociados a los productos.
     */
    private final SupplierRepository supplierRepository;

    /**
     * Mapper encargado de convertir entre ProductEntity y ProductDTO.
     */
    private final ProductMapper productMapper;

    /**
     * Constructor utilizado por Spring para inyectar las dependencias
     * necesarias para el funcionamiento del Service.
     */
    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            SupplierRepository supplierRepository,
            ProductMapper productMapper) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.productMapper = productMapper;
    }

    // =========================================================
    // CONSULTAR PRODUCTOS ACTIVOS
    // =========================================================

    /**
     * Consulta únicamente los productos que se encuentran activos.
     *
     * Esta será la consulta utilizada normalmente por el frontend
     * para mostrar el listado principal de productos.
     *
     * @return lista de productos activos
     */
    public List<ProductDTO> findAllActive() {

        return productRepository.findByActivoTrue()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    // =========================================================
    // CONSULTAR TODOS LOS PRODUCTOS
    // =========================================================

    /**
     * Consulta todos los productos registrados, incluyendo
     * aquellos que fueron desactivados.
     *
     * Esta opción será útil para la administración del inventario
     * y para permitir la reactivación de productos.
     *
     * @return lista completa de productos
     */
    public List<ProductDTO> findAllIncludingInactive() {

        return productRepository.findAllByOrderByIdProductoDesc()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    // =========================================================
    // CONSULTAR PRODUCTO POR ID
    // =========================================================

    /**
     * Busca un producto específico mediante su identificador.
     *
     * Si el producto no existe, se lanza ResourceNotFoundException
     * para que el sistema pueda devolver un mensaje claro al cliente.
     *
     * @param id identificador del producto
     * @return información del producto encontrado
     */
    public ProductDTO findById(Long id) {

        ProductEntity producto = buscarProductoOLanzarError(id);

        return productMapper.toDTO(producto);
    }

    // =========================================================
    // BUSCAR PRODUCTOS
    // =========================================================

    /**
     * Busca productos activos utilizando un criterio de búsqueda.
     *
     * El criterio puede coincidir con:
     *
     * - Código del producto
     * - Nombre del producto
     * - Nombre de la categoría
     *
     * Si el criterio está vacío, se devuelven todos los productos
     * activos.
     *
     * Se utiliza LinkedHashMap para evitar que un mismo producto
     * aparezca varias veces cuando coincide en más de un criterio.
     *
     * @param criterio texto ingresado en el buscador
     * @return productos que coinciden con la búsqueda
     */
    public List<ProductDTO> buscar(String criterio) {

        if (criterio == null || criterio.isBlank()) {
            return findAllActive();
        }

        /*
         * El Map utiliza el ID del producto como llave.
         *
         * De esta forma, si un producto aparece tanto por nombre
         * como por código, solamente se mostrará una vez.
         */
        Map<Long, ProductEntity> resultados = new LinkedHashMap<>();

        productRepository
                .findByNombreContainingIgnoreCaseAndActivoTrue(criterio)
                .forEach(producto ->
                        resultados.put(producto.getIdProducto(), producto)
                );

        productRepository
                .findByCodigoContainingIgnoreCaseAndActivoTrue(criterio)
                .forEach(producto ->
                        resultados.put(producto.getIdProducto(), producto)
                );

        productRepository
                .findByCategoria_NombreContainingIgnoreCaseAndActivoTrue(criterio)
                .forEach(producto ->
                        resultados.put(producto.getIdProducto(), producto)
                );

        return resultados.values()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    // =========================================================
    // REGISTRAR PRODUCTO
    // =========================================================

    /**
     * Registra un nuevo producto en el sistema.
     *
     * Antes de guardar se realizan las siguientes validaciones:
     *
     * 1. La categoría debe existir.
     * 2. La categoría debe estar activa.
     * 3. El proveedor debe existir.
     * 4. El proveedor debe estar activo.
     * 5. Se genera automáticamente el código del producto.
     * 6. El producto se crea como activo.
     * 7. Se calcula automáticamente su estado.
     *
     * @param dto información enviada desde el frontend
     * @return producto registrado
     */
    public ProductDTO create(ProductDTO dto) {

        // ---------------------------------------------------------
        // VALIDAR CATEGORÍA
        // ---------------------------------------------------------

        CategoryEntity categoria = buscarCategoriaActiva(
                dto.getIdCategoria()
        );

        // ---------------------------------------------------------
        // VALIDAR PROVEEDOR
        // ---------------------------------------------------------

        SupplierEntity proveedor = buscarProveedorActivo(
                dto.getIdProveedor()
        );

        // ---------------------------------------------------------
        // CREAR ENTIDAD
        // ---------------------------------------------------------

        ProductEntity nuevoProducto = productMapper.toEntity(dto);

        /*
         * Aunque el Mapper crea una referencia de categoría y
         * proveedor, aquí reemplazamos esas referencias por las
         * entidades reales que fueron consultadas y verificadas
         * en la base de datos.
         */
        nuevoProducto.setCategoria(categoria);
        nuevoProducto.setProveedor(proveedor);

        // ---------------------------------------------------------
        // GENERAR CÓDIGO AUTOMÁTICAMENTE
        // ---------------------------------------------------------

        nuevoProducto.setCodigo(generarCodigo());

        /*
         * Todo producto nuevo comienza activo.
         */
        nuevoProducto.setActivo(true);

        /*
         * El estado no lo decide el frontend.
         *
         * El backend lo calcula utilizando el stock actual.
         */
        actualizarEstado(nuevoProducto);

        // ---------------------------------------------------------
        // GUARDAR PRODUCTO
        // ---------------------------------------------------------

        ProductEntity guardado = productRepository.save(nuevoProducto);

        return productMapper.toDTO(guardado);
    }

    // =========================================================
    // ACTUALIZAR PRODUCTO
    // =========================================================

    /**
     * Actualiza la información modificable de un producto existente.
     *
     * No se modifica:
     *
     * - Código
     * - Stock actual
     * - Estado manualmente
     * - Estado activo
     *
     * El stock será controlado posteriormente mediante
     * el módulo Movimientos.
     *
     * @param id identificador del producto
     * @param dto nueva información del producto
     * @return producto actualizado
     */
    public ProductDTO update(Long id, ProductDTO dto) {

        // ---------------------------------------------------------
        // BUSCAR PRODUCTO
        // ---------------------------------------------------------

        ProductEntity productoExistente =
                buscarProductoOLanzarError(id);

        // ---------------------------------------------------------
        // VALIDAR CATEGORÍA
        // ---------------------------------------------------------

        CategoryEntity categoria = buscarCategoriaActiva(
                dto.getIdCategoria()
        );

        // ---------------------------------------------------------
        // VALIDAR PROVEEDOR
        // ---------------------------------------------------------

        SupplierEntity proveedor = buscarProveedorActivo(
                dto.getIdProveedor()
        );

        // ---------------------------------------------------------
        // ACTUALIZAR CAMPOS PERMITIDOS
        // ---------------------------------------------------------

        productMapper.actualizarEntity(
                productoExistente,
                dto
        );

        /*
         * Después de actualizar los datos básicos reemplazamos
         * las referencias por las entidades verificadas.
         */
        productoExistente.setCategoria(categoria);
        productoExistente.setProveedor(proveedor);

        /*
         * El stock actual no se modifica aquí.
         *
         * El estado se vuelve a calcular utilizando el stock
         * que actualmente tiene el producto.
         */
        actualizarEstado(productoExistente);

        // ---------------------------------------------------------
        // GUARDAR CAMBIOS
        // ---------------------------------------------------------

        ProductEntity actualizado =
                productRepository.save(productoExistente);

        return productMapper.toDTO(actualizado);
    }

    // =========================================================
    // DESACTIVAR PRODUCTO
    // =========================================================

    /**
     * Desactiva un producto mediante eliminación lógica.
     *
     * El registro NO se elimina físicamente de la base de datos.
     * Simplemente se cambia el campo activo a false.
     *
     * Al estar inactivo, su estado será INACTIVO.
     *
     * @param id identificador del producto
     */
    public void deactivate(Long id) {

        ProductEntity producto =
                buscarProductoOLanzarError(id);

        producto.setActivo(false);

        actualizarEstado(producto);

        productRepository.save(producto);
    }

    // =========================================================
    // REACTIVAR PRODUCTO
    // =========================================================

    /**
     * Reactiva un producto que anteriormente había sido
     * desactivado.
     *
     * Después de activarlo, el estado se calcula nuevamente
     * teniendo en cuenta el stock actual.
     *
     * @param id identificador del producto
     */
    public void reactivate(Long id) {

        ProductEntity producto =
                buscarProductoOLanzarError(id);

        producto.setActivo(true);

        actualizarEstado(producto);

        productRepository.save(producto);
    }

    // =========================================================
    // GENERAR CÓDIGO
    // =========================================================

    /**
     * Genera automáticamente un código único para el producto.
     *
     * El formato utilizado será:
     *
     * PROD-0001
     * PROD-0002
     * PROD-0003
     *
     * El código es generado por el backend para evitar que
     * el usuario tenga que ingresarlo manualmente.
     *
     * @return código único generado
     */
    private String generarCodigo() {

        /*
         * Tomamos la cantidad actual de productos y sumamos uno
         * para obtener el siguiente número inicial.
         */
        long siguienteNumero = productRepository.count() + 1;

        String codigo;

        /*
         * Se utiliza un ciclo para asegurarnos de que el código
         * generado no exista previamente.
         */
        do {

            codigo = String.format(
                    "PROD-%04d",
                    siguienteNumero
            );

            siguienteNumero++;

        } while (productRepository.existsByCodigoIgnoreCase(codigo));

        return codigo;
    }

    // =========================================================
    // CALCULAR ESTADO
    // =========================================================

    /**
     * Calcula automáticamente el estado del producto.
     *
     * Reglas definidas para INVENTORY:
     *
     * - Producto inactivo → INACTIVO
     * - Stock igual a cero → AGOTADO
     * - Stock mayor a cero → DISPONIBLE
     *
     * El frontend no controla directamente este estado.
     *
     * @param producto producto cuyo estado será actualizado
     */
    private void actualizarEstado(ProductEntity producto) {

        if (!Boolean.TRUE.equals(producto.getActivo())) {

            producto.setEstado(ProductStatus.INACTIVO);

        } else if (producto.getStockActual() == null
                || producto.getStockActual() <= 0) {

            producto.setEstado(ProductStatus.AGOTADO);

        } else {

            producto.setEstado(ProductStatus.DISPONIBLE);
        }
    }

    // =========================================================
    // BUSCAR CATEGORÍA ACTIVA
    // =========================================================

    /**
     * Busca una categoría por su identificador y verifica
     * que se encuentre activa.
     *
     * @param id identificador de la categoría
     * @return categoría activa
     */
    private CategoryEntity buscarCategoriaActiva(Long id) {

        if (id == null) {
            throw new ResourceNotFoundException(
                    "La categoría es obligatoria."
            );
        }

        CategoryEntity categoria = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró la categoría con id " + id
                        )
                );

        if (!Boolean.TRUE.equals(categoria.getActivo())) {
            throw new ResourceNotFoundException(
                    "La categoría seleccionada se encuentra inactiva."
            );
        }

        return categoria;
    }

    // =========================================================
    // BUSCAR PROVEEDOR ACTIVO
    // =========================================================

    /**
     * Busca un proveedor por su identificador y verifica
     * que se encuentre activo.
     *
     * @param id identificador del proveedor
     * @return proveedor activo
     */
    private SupplierEntity buscarProveedorActivo(Long id) {

        if (id == null) {
            throw new ResourceNotFoundException(
                    "El proveedor es obligatorio."
            );
        }

        SupplierEntity proveedor = supplierRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el proveedor con id " + id
                        )
                );

        if (!Boolean.TRUE.equals(proveedor.getActivo())) {
            throw new ResourceNotFoundException(
                    "El proveedor seleccionado se encuentra inactivo."
            );
        }

        return proveedor;
    }

    // =========================================================
    // BUSCAR PRODUCTO O LANZAR ERROR
    // =========================================================

    /**
     * Método auxiliar utilizado para evitar repetir la misma
     * consulta y el mismo mensaje de error en diferentes métodos.
     *
     * @param id identificador del producto
     * @return producto encontrado
     */
    private ProductEntity buscarProductoOLanzarError(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el producto con id " + id
                        )
                );
    }
}