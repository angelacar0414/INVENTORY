package com.inventory.supplier.repository;

import com.inventory.supplier.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Me encargo de hablar directamente con la base de datos para todo lo
 * relacionado con proveedores, usando Spring Data JPA. Aquí no valido
 * ninguna regla de negocio, solo ejecuto las consultas que el Service
 * me solicita, igual que hicimos en el Repository de Categorías.
 *
 * @author Angela Carvajal Ortiz
 * @version 1.0
 */
public interface SupplierRepository extends JpaRepository<SupplierEntity, Long> {

    // Reviso si ya existe un proveedor registrado con ese nombre exacto
    // (sin importar mayúsculas o minúsculas), para evitar duplicados.
    boolean existsByNombreIgnoreCase(String nombre);

    // Reviso si ya existe un proveedor registrado con ese correo,
    // también para evitar duplicados.
    boolean existsByCorreoIgnoreCase(String correo);

    // Traigo únicamente los proveedores que están activos.
    List<SupplierEntity> findByActivoTrue();

    // Busco proveedores cuyo nombre contenga el texto que me envíen,
    // sin importar mayúsculas o minúsculas. La uso para el buscador.
    List<SupplierEntity> findByNombreContainingIgnoreCase(String nombre);
}
