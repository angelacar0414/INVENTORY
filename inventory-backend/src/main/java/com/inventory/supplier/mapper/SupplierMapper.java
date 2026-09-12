package com.inventory.supplier.mapper;

import com.inventory.supplier.dto.SupplierDTO;
import com.inventory.supplier.entity.SupplierEntity;
import org.springframework.stereotype.Component;

/**
 * Aquí convierto la información entre SupplierEntity (lo que se guarda en
 * la base de datos) y SupplierDTO (lo que se envía y recibe por la API).
 *
 * Separar esta conversión en su propia clase mantiene el código del
 * Service más limpio y organizado, siguiendo el mismo patrón que ya
 * usamos en el módulo de Categorías.
 *
 * @author Angela Carvajal Ortiz
 * @version 1.0
 */
@Component
public class SupplierMapper {

    // Convierto una entidad ya guardada en la base de datos hacia un DTO,
    // que es lo que finalmente se le devuelve al Frontend.
    public SupplierDTO toDTO(SupplierEntity entity) {
        SupplierDTO dto = new SupplierDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDocumento(entity.getDocumento());
        dto.setTelefono(entity.getTelefono());
        dto.setCorreo(entity.getCorreo());
        dto.setDireccion(entity.getDireccion());
        dto.setActivo(entity.getActivo());
        return dto;
    }

    // Convierto un DTO que llega desde el Frontend hacia una entidad nueva,
    // lista para ser guardada en la base de datos.
    public SupplierEntity toEntity(SupplierDTO dto) {
        SupplierEntity entity = new SupplierEntity();
        entity.setNombre(dto.getNombre());
        entity.setDocumento(dto.getDocumento());
        entity.setTelefono(dto.getTelefono());
        entity.setCorreo(dto.getCorreo());
        entity.setDireccion(dto.getDireccion());
        return entity;
    }

    // Actualizo los campos de una entidad existente con los datos de un DTO.
    // La uso cuando se edita un proveedor que ya existe, para no perder el id
    // ni el estado activo que ya tenía.
    public void actualizarEntity(SupplierEntity entity, SupplierDTO dto) {
        entity.setNombre(dto.getNombre());
        entity.setDocumento(dto.getDocumento());
        entity.setTelefono(dto.getTelefono());
        entity.setCorreo(dto.getCorreo());
        entity.setDireccion(dto.getDireccion());
    }
}
