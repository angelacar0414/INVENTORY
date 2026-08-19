package com.inventory.category.mapper;

import com.inventory.category.dto.CategoryDTO;
import com.inventory.category.entity.CategoryEntity;
import org.springframework.stereotype.Component;

/**
 * CLASE MAPPER
 * -------------
 * Su único trabajo es "traducir":
 *   Entity  -> DTO   (para mandar información al Frontend)
 *   DTO     -> Entity (para guardar lo que llega del Frontend)
 *
 * Así el Service nunca tiene que hacer esa conversión a mano,
 * y si mañana cambia algún campo, solo se corrige aquí.
 */
@Component
public class CategoryMapper {

    // Convierte lo que viene de la base de datos (Entity)
    // en lo que se va a enviar al Frontend (DTO)
    public CategoryDTO toDTO(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return new CategoryDTO(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getActivo()
        );
    }

    // Convierte lo que llega del Frontend (DTO)
    // en lo que se va a guardar en la base de datos (Entity)
    public CategoryEntity toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        CategoryEntity entity = new CategoryEntity();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        // Si no envían "activo", por defecto queda true (categoría nueva)
        entity.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return entity;
    }
}
