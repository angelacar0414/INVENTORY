package com.inventory.category.repository;

import com.inventory.category.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * CLASE REPOSITORY
 * -----------------
 * Es la única capa que "habla" con la base de datos.
 *
 * IMPORTANTE PARA LA EVIDENCIA (conexión JDBC):
 * JpaRepository, por dentro, usa Hibernate, y Hibernate usa el DRIVER
 * JDBC (mysql-connector-j) que configuramos en application.properties
 * para abrir la conexión real con MySQL. Es decir: SÍ estamos
 * conectando por JDBC, solo que Spring nos ahorra escribir el SQL
 * y el manejo manual de Connection/Statement/ResultSet.
 *
 * Solo con "extends JpaRepository" ya tenemos gratis los métodos:
 *   save(), findById(), findAll(), deleteById(), etc.
 *
 * Los métodos de abajo son "consultas personalizadas": Spring lee
 * el NOMBRE del método y genera el SQL automáticamente.
 */
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    // Genera algo como:
    // SELECT * FROM categoria WHERE activo = true
    List<CategoryEntity> findByActivoTrue();

    // Genera algo como:
    // SELECT * FROM categoria WHERE nombre = ? AND activo = true
    Optional<CategoryEntity> findByNombreIgnoreCaseAndActivoTrue(String nombre);

    // Para validar duplicados antes de guardar (RF-9)
    boolean existsByNombreIgnoreCase(String nombre);
}
