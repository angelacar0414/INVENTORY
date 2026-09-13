package com.inventory.user.mapper;

import org.springframework.stereotype.Component;

import com.inventory.auth.UsuarioEntity;
import com.inventory.user.dto.UserDTO;

/**
 * Mapper encargado de convertir la entidad UsuarioEntity
 * en el DTO utilizado por el módulo de usuarios.
 *
 * También permite actualizar una entidad existente
 * utilizando la información recibida en un UserDTO.
 *
 * @author Darío Bustamante
 * @version 1.0
 */
@Component
public class UserMapper {

    // ==================== CONVERSIÓN A DTO ====================

    /**
     * Convierte una entidad UsuarioEntity en un UserDTO.
     *
     * La contraseña no se incluye porque el UserDTO
     * está diseñado para no exponer información sensible.
     *
     * @param usuario entidad que se desea convertir
     * @return DTO con la información pública del usuario
     */
    public UserDTO toDTO(UsuarioEntity usuario) {

        if (usuario == null) {
            return null;
        }

        return new UserDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getActivo()
        );
    }

    // ==================== ACTUALIZACIÓN DE ENTIDAD ====================

    /**
     * Actualiza los datos básicos de una entidad existente
     * utilizando la información recibida en el DTO.
     *
     * No modifica la contraseña ni el estado activo,
     * ya que estos datos tendrán operaciones específicas.
     *
     * @param dto información recibida
     * @param usuario entidad que se desea actualizar
     */
    public void updateEntity(UserDTO dto, UsuarioEntity usuario) {

        if (dto == null || usuario == null) {
            return;
        }

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(dto.getRol());
    }
}