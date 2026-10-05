package com.inventory.product.mapper;

import com.inventory.category.entity.CategoryEntity;
import com.inventory.product.dto.ProductDTO;
import com.inventory.product.entity.ProductEntity;
import com.inventory.product.entity.ProductStatus;
import com.inventory.supplier.entity.SupplierEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper encargado de convertir la información entre
 * ProductEntity y ProductDTO.
 *
 * ProductEntity representa el producto dentro de la capa
 * de persistencia y está relacionado con la base de datos.
 *
 * ProductDTO representa la información que se recibe o
 * devuelve mediante la API REST.
 *
 * De esta manera mantenemos separadas las responsabilidades
 * de cada capa del backend.
 */
@Component
public class ProductMapper {

    /**
     * Convierte una entidad ProductEntity en un ProductDTO.
     *
     * Se utiliza principalmente cuando el backend necesita
     * enviar la información de un producto al frontend.
     *
     * @param entity entidad del producto
     * @return DTO con la información del producto
     */
    public ProductDTO toDTO(ProductEntity entity) {

        ProductDTO dto = new ProductDTO();

        // ---------------------------------------------------------
        // INFORMACIÓN BÁSICA DEL PRODUCTO
        // ---------------------------------------------------------

        dto.setIdProducto(entity.getIdProducto());
        dto.setCodigo(entity.getCodigo());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());

        // ---------------------------------------------------------
        // PRECIOS Y EXISTENCIAS
        // ---------------------------------------------------------

        dto.setPrecioCompra(entity.getPrecioCompra());
        dto.setPrecioVenta(entity.getPrecioVenta());
        dto.setStockActual(entity.getStockActual());
        dto.setStockMinimo(entity.getStockMinimo());

        // ---------------------------------------------------------
        // CATEGORÍA
        // ---------------------------------------------------------

        /*
         * ProductEntity mantiene una relación ManyToOne con
         * CategoryEntity.
         *
         * Además del identificador, enviamos el nombre de la
         * categoría para facilitar su visualización en el frontend.
         */
        if (entity.getCategoria() != null) {
            dto.setIdCategoria(entity.getCategoria().getId());
            dto.setNombreCategoria(entity.getCategoria().getNombre());
        }

        // ---------------------------------------------------------
        // PROVEEDOR
        // ---------------------------------------------------------

        /*
         * ProductEntity mantiene una relación ManyToOne con
         * SupplierEntity.
         *
         * Se envían tanto el identificador como el nombre del
         * proveedor para que el frontend pueda mostrarlo.
         */
        if (entity.getProveedor() != null) {
            dto.setIdProveedor(entity.getProveedor().getId());
            dto.setNombreProveedor(entity.getProveedor().getNombre());
        }

        // ---------------------------------------------------------
        // ESTADO Y ACTIVACIÓN
        // ---------------------------------------------------------

        /*
         * El estado se convierte de Enum a String para facilitar
         * su transporte mediante JSON.
         */
        if (entity.getEstado() != null) {
            dto.setEstado(entity.getEstado().name());
        }

        dto.setActivo(entity.getActivo());

        return dto;
    }

    /**
     * Convierte un ProductDTO en una nueva ProductEntity.
     *
     * Este método se utiliza principalmente durante el registro
     * de un producto.
     *
     * El código, estado y activo no se reciben como datos de
     * confianza desde el frontend. Esos valores serán controlados
     * posteriormente por ProductService.
     *
     * @param dto información recibida desde la API
     * @return nueva entidad ProductEntity
     */
    public ProductEntity toEntity(ProductDTO dto) {

        ProductEntity entity = new ProductEntity();

        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPrecioCompra(dto.getPrecioCompra());
        entity.setPrecioVenta(dto.getPrecioVenta());
        entity.setStockActual(
                dto.getStockActual() != null ? dto.getStockActual() : 0
        );
        entity.setStockMinimo(dto.getStockMinimo());

        /*
         * En esta etapa únicamente creamos las referencias
         * de categoría y proveedor utilizando sus identificadores.
         *
         * El Service será responsable de verificar que realmente
         * existan y que estén activos antes de guardar el producto.
         */
        if (dto.getIdCategoria() != null) {
            CategoryEntity categoria = new CategoryEntity();
            categoria.setId(dto.getIdCategoria());
            entity.setCategoria(categoria);
        }

        if (dto.getIdProveedor() != null) {
            SupplierEntity proveedor = new SupplierEntity();
            proveedor.setId(dto.getIdProveedor());
            entity.setProveedor(proveedor);
        }

        return entity;
    }

    /**
     * Actualiza los campos modificables de una entidad existente.
     *
     * No modifica:
     *
     * - Código
     * - Stock actual
     * - Estado
     * - Activo
     *
     * Estos valores son controlados por las reglas de negocio
     * del módulo Productos.
     *
     * @param entity producto existente
     * @param dto nueva información del producto
     */
    public void actualizarEntity(ProductEntity entity, ProductDTO dto) {

        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPrecioCompra(dto.getPrecioCompra());
        entity.setPrecioVenta(dto.getPrecioVenta());
        entity.setStockMinimo(dto.getStockMinimo());

        /*
         * Actualizamos únicamente la referencia de categoría.
         * La existencia y estado de la categoría serán validados
         * posteriormente por ProductService.
         */
        if (dto.getIdCategoria() != null) {
            CategoryEntity categoria = new CategoryEntity();
            categoria.setId(dto.getIdCategoria());
            entity.setCategoria(categoria);
        }

        /*
         * Actualizamos únicamente la referencia del proveedor.
         * La existencia y estado del proveedor serán validados
         * posteriormente por ProductService.
         */
        if (dto.getIdProveedor() != null) {
            SupplierEntity proveedor = new SupplierEntity();
            proveedor.setId(dto.getIdProveedor());
            entity.setProveedor(proveedor);
        }
    }
}