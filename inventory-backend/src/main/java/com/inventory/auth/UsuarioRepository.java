package com.inventory.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository para la entidad Usuario
 * Proporciona métodos CRUD y consultas personalizadas
 *
 * @author Darío Bustamante
 * @version 1.0
 */
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    /**
     * Busca un usuario por su email
     *
     * @param email el email del usuario a buscar
     * @return Optional con el usuario si existe
     */
    Optional<UsuarioEntity> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email dado
     *
     * @param email el email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);
}