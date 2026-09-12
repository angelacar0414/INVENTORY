package com.inventory.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio utilizado por Spring Security para buscar
 * los usuarios registrados en la base de datos.
 *
 * @author Darío Bustamante
 * @version 1.0
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Busca un usuario por su username.
     *
     * @param username nombre de usuario utilizado para iniciar sesión
     * @return información del usuario que Spring Security necesita
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UsuarioEntity usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "El usuario no existe"));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getContraseñaEncriptada())
                .roles(usuario.getRol())
                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .build();
    }
}