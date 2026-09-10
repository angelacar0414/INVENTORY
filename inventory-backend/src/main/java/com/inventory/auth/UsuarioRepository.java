package com.inventory.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para la entidad Usuario.
 * Proporciona operaciones CRUD y consultas personalizadas
 * para la gestión de usuarios.
 *
 * @author Darío Bustamante
 * @version 1.0
 */
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    /**
     * Busca un usuario por su username.
     *
     * @param username nombre de usuario a buscar
     * @return Optional con el usuario si existe
     */
    Optional<UsuarioEntity> findByUsername(String username);

    /**
     * Verifica si existe un usuario con el username dado.
     *
     * @param username nombre de usuario a verificar
     * @return true si existe, false si no
     */
    boolean existsByUsername(String username);

    /**
     * Busca un usuario por su email.
     *
     * @param email correo electrónico del usuario a buscar
     * @return Optional con el usuario si existe
     */
    Optional<UsuarioEntity> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email dado.
     *
     * @param email correo electrónico a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);
}