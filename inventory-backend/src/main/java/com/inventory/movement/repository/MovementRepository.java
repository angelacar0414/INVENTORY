package com.inventory.movement.repository;

import com.inventory.movement.entity.MovementEntity;
import com.inventory.movement.entity.MovementType;
import com.inventory.product.entity.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio encargado de las operaciones de acceso a la base de datos
 * relacionadas con los movimientos de inventario.
 *
 * Al extender JpaRepository, Spring Data JPA proporciona automáticamente
 * las operaciones básicas (guardar, consultar por id, etc.).
 *
 * Nota: el historial de movimientos nunca se elimina. Aunque JpaRepository
 * incluye métodos de borrado, el Service del módulo no los utiliza y la
 * API REST no expone ningún endpoint para eliminar movimientos.
 *
 * Todas las consultas de lectura usan @EntityGraph para traer en una sola
 * consulta el producto, el usuario y el cliente de cada movimiento. Así se
 * evita hacer una consulta adicional por cada fila del historial.
 */
@Repository
public interface MovementRepository extends JpaRepository<MovementEntity, Long> {

    // =========================================================
    // CONSULTAR HISTORIAL
    // =========================================================

    /**
     * Obtiene todo el historial, del movimiento más reciente al más antiguo.
     */
    @EntityGraph(attributePaths = {"producto", "usuario", "cliente"})
    List<MovementEntity> findAllByOrderByFechaMovimientoDescIdMovimientoDesc();

    /**
     * Obtiene el historial de un solo tipo (ENTRADA o SALIDA),
     * del más reciente al más antiguo.
     */
    @EntityGraph(attributePaths = {"producto", "usuario", "cliente"})
    List<MovementEntity> findByTipoMovimientoOrderByFechaMovimientoDescIdMovimientoDesc(
            MovementType tipoMovimiento
    );

    /**
     * Busca movimientos cuyo producto coincida (por nombre o por código)
     * con el texto indicado.
     */
    @EntityGraph(attributePaths = {"producto", "usuario", "cliente"})
    @Query("""
            SELECT m FROM MovementEntity m
            WHERE LOWER(m.producto.nombre) LIKE LOWER(CONCAT('%', :criterio, '%'))
               OR LOWER(m.producto.codigo) LIKE LOWER(CONCAT('%', :criterio, '%'))
            ORDER BY m.fechaMovimiento DESC, m.idMovimiento DESC
            """)
    List<MovementEntity> buscarPorProducto(@Param("criterio") String criterio);

    /**
     * Igual que buscarPorProducto, pero filtrando además por tipo
     * de movimiento.
     */
    @EntityGraph(attributePaths = {"producto", "usuario", "cliente"})
    @Query("""
            SELECT m FROM MovementEntity m
            WHERE m.tipoMovimiento = :tipo
              AND (LOWER(m.producto.nombre) LIKE LOWER(CONCAT('%', :criterio, '%'))
                OR LOWER(m.producto.codigo) LIKE LOWER(CONCAT('%', :criterio, '%')))
            ORDER BY m.fechaMovimiento DESC, m.idMovimiento DESC
            """)
    List<MovementEntity> buscarPorProductoYTipo(
            @Param("criterio") String criterio,
            @Param("tipo") MovementType tipo
    );

    /**
     * Consulta un movimiento por su identificador, trayendo también
     * su producto, usuario y cliente.
     */
    @EntityGraph(attributePaths = {"producto", "usuario", "cliente"})
    Optional<MovementEntity> findByIdMovimiento(Long idMovimiento);

    // =========================================================
    // BLOQUEO DEL PRODUCTO
    // =========================================================

    /**
     * Consulta un producto y lo BLOQUEA en la base de datos hasta que
     * termine la transacción actual (SELECT ... FOR UPDATE).
     *
     * ¿Por qué es necesario?
     * Si dos usuarios registran un movimiento sobre el mismo producto
     * al mismo tiempo, ambos podrían leer el mismo stock y el segundo
     * movimiento pisaría el resultado del primero. Con el bloqueo, el
     * segundo movimiento espera a que el primero termine y recién ahí
     * lee el stock ya actualizado.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p WHERE p.idProducto = :idProducto")
    Optional<ProductEntity> buscarProductoConBloqueo(
            @Param("idProducto") Long idProducto
    );
}
