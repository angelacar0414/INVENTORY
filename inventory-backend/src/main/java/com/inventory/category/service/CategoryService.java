package com.inventory.category.service;

import com.inventory.category.dto.CategoryDTO;
import com.inventory.category.entity.CategoryEntity;
import com.inventory.category.mapper.CategoryMapper;
import com.inventory.category.repository.CategoryRepository;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CLASE SERVICE
 * --------------
 * Aquí vive TODA la lógica del negocio del módulo Categorías.
 * El Controller nunca decide reglas de negocio, solo el Service.
 *
 * Reglas implementadas:
 *   Registrar categoría (nombre no puede repetirse)
 *   Editar categoría (nombre no puede repetirse con otra)
 *   Desactivar categoría (eliminación lógica, no física)
 *   Consultar categorías (solo activas por defecto)
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    // Spring "inyecta" automáticamente el Repository y el Mapper aquí
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    // ---------------------------------------------------------------
    // CONSULTAR CATEGORÍAS
    // ---------------------------------------------------------------
    public List<CategoryDTO> findAllActive() {
        return categoryRepository.findByActivoTrue()
                .stream()
                .map(categoryMapper::toDTO)
                .toList();
    }
    // Consultar TODAS las categorías, sin filtrar por activo.
    public List<CategoryDTO> findAllIncludingInactive() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDTO)
                .toList();
    }

    public CategoryDTO findById(Long id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la categoría con id " + id));
        return categoryMapper.toDTO(entity);
    }

    // ---------------------------------------------------------------
    // REGISTRAR CATEGORÍA
    // ---------------------------------------------------------------
    public CategoryDTO create(CategoryDTO dto) {
        // Regla de negocio: el nombre no puede estar duplicado
        if (categoryRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new DuplicateResourceException("La categoría ya está registrada.");
        }

        CategoryEntity entity = categoryMapper.toEntity(dto);
        entity.setId(null);      // Por seguridad: nunca dejamos que nos manden un id al crear
        entity.setActivo(true);  // Toda categoría nueva nace "activo"

        CategoryEntity guardada = categoryRepository.save(entity);
        return categoryMapper.toDTO(guardada);
    }

    // ---------------------------------------------------------------
    // EDITAR CATEGORÍA
    // ---------------------------------------------------------------
    public CategoryDTO update(Long id, CategoryDTO dto) {
        CategoryEntity existente = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la categoría con id " + id));

        // Si el nombre cambió, validamos que el nuevo nombre no esté repetido
        boolean nombreCambio = !existente.getNombre().equalsIgnoreCase(dto.getNombre());
        if (nombreCambio && categoryRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new DuplicateResourceException("Ya existe otra categoría con ese nombre.");
        }

        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());

        CategoryEntity actualizada = categoryRepository.save(existente);
        return categoryMapper.toDTO(actualizada);
    }

    // ---------------------------------------------------------------
    // DESACTIVAR CATEGORÍA (eliminación lógica)
    // ---------------------------------------------------------------
    public void deactivate(Long id) {
        CategoryEntity existente = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la categoría con id " + id));

        // Nunca hacemos delete físico. Solo cambiamos el estado.
        existente.setActivo(false);
        categoryRepository.save(existente);
    }

// ------------------------------------------------------------
// REACTIVAR CATEGORÍA
// ------------------------------------------------------------
public void reactivate(Long id) {
    CategoryEntity existente = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "No se encontró la categoria con id " + id));

    // La volvemos a marcar como activa
    existente.setActivo(true);
    categoryRepository.save(existente);
    }
}
