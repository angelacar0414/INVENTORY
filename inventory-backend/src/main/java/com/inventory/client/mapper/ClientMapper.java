package com.inventory.client.mapper;

import org.springframework.stereotype.Component;

import com.inventory.client.dto.ClientDTO;
import com.inventory.client.entity.ClientEntity;

/**
 * Mapper del módulo Clientes.
 * Convierte Entity <-> DTO. Lo dejamos como una clase aparte (en vez de
 * hacerlo directo en el Service) para que, si un día cambia la forma del
 * DTO, solo toquemos este archivo y no toda la lógica de negocio.
 */
@Component
public class ClientMapper {

    public ClientDTO toDTO(ClientEntity entity) {
        if (entity == null) {
            return null;
        }
        ClientDTO dto = new ClientDTO();
        dto.setIdCliente(entity.getIdCliente());
        dto.setNombre(entity.getNombre());
        dto.setDocumento(entity.getDocumento());
        dto.setTelefono(entity.getTelefono());
        dto.setCorreo(entity.getCorreo());
        dto.setDireccion(entity.getDireccion());
        dto.setActivo(entity.getActivo());
        return dto;
    }

    public ClientEntity toEntity(ClientDTO dto) {
        if (dto == null) {
            return null;
        }
        ClientEntity entity = new ClientEntity();
        entity.setIdCliente(dto.getIdCliente());
        entity.setNombre(dto.getNombre());
        entity.setDocumento(dto.getDocumento());
        entity.setTelefono(dto.getTelefono());
        entity.setCorreo(dto.getCorreo());
        entity.setDireccion(dto.getDireccion());
        entity.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return entity;
    }

    // La usamos cuando editamos un cliente que ya existe, así no
    // perdemos el id ni el estado "activo" que ya tenía guardado.
    public void updateEntityFromDTO(ClientDTO dto, ClientEntity entity) {
        entity.setNombre(dto.getNombre());
        entity.setDocumento(dto.getDocumento());
        entity.setTelefono(dto.getTelefono());
        entity.setCorreo(dto.getCorreo());
        entity.setDireccion(dto.getDireccion());
    }
}
