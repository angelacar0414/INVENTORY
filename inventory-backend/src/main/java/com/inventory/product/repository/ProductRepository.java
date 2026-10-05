package com.inventory.product.repository;

import com.inventory.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio encargado de realizar las operaciones de acceso
 * a la base de datos relacionadas con los productos.
 *
 * Al extender JpaRepository, Spring Data JPA proporciona
 * automáticamente las operaciones básicas de:
 *
 * - Guardar
 * - Consultar
 * - Actualizar
 * - Eliminar
 * - Buscar por identificador
 *
 * No es necesario escribir manualmente las consultas SQL
 * para estas operaciones básicas.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * Obtiene únicamente los productos que se encuentran activos.
     *
     * Se utiliza para las consultas normales del módulo de productos.
     */
    List<ProductEntity> findByActivoTrue();

    /**
     * Obtiene todos los productos ordenados desde el más reciente
     * hasta el más antiguo según su identificador.
     */
    List<ProductEntity> findAllByOrderByIdProductoDesc();

    /**
     * Verifica si ya existe un producto con el código indicado.
     *
     * IgnoreCase permite realizar la comparación sin distinguir
     * entre letras mayúsculas y minúsculas.
     */
    boolean existsByCodigoIgnoreCase(String codigo);

    /**
     * Busca un producto utilizando su código.
     */
    Optional<ProductEntity> findByCodigoIgnoreCase(String codigo);

    /**
     * Busca productos activos cuyo nombre contenga el texto indicado.
     */
    List<ProductEntity> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    /**
     * Busca productos activos cuyo nombre de categoría
     * contenga el texto indicado.
     *
     * Spring Data JPA utiliza la relación categoria definida
     * en ProductEntity para realizar esta búsqueda.
     */
    List<ProductEntity> findByCategoria_NombreContainingIgnoreCaseAndActivoTrue(
            String nombre
    );

    /**
     * Busca productos activos cuyo código contenga
     * el texto indicado.
     */
    List<ProductEntity> findByCodigoContainingIgnoreCaseAndActivoTrue(
            String codigo
    );
}