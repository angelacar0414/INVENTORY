package com.inventory.config;

import com.inventory.auth.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Configuración principal de Spring Security.
 *
 * Gestiona la autenticación, autorización y seguridad
 * de las rutas protegidas del sistema INVENTORY.
 *
 * @author Darío Bustamante
 * @version 1.0
 */
@Configuration
public class SecurityConfig {

    /**
     * Codificador de contraseñas utilizado por Spring Security.
     *
     * @return PasswordEncoder basado en BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proveedor de autenticación que utiliza nuestro
     * UsuarioDetailsService para buscar usuarios.
     *
     * @param usuarioDetailsService servicio que busca usuarios
     * @return proveedor de autenticación configurado
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UsuarioDetailsService usuarioDetailsService) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /**
     * Administrador de autenticación de Spring Security.
     *
     * @param authenticationConfiguration configuración de autenticación
     * @return AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configuración de seguridad de las solicitudes HTTP.
     *
     * @param http configuración de seguridad HTTP
     * @return cadena de filtros de seguridad
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // CSRF deshabilitado temporalmente para nuestra API REST.
                .csrf(csrf -> csrf.disable())

                // Configuración de las rutas.
                .authorizeHttpRequests(auth -> auth

                        // Login y registro son públicos.
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // El resto de la API requiere autenticación.
                        .anyRequest().authenticated()
                )

                // Utilizamos autenticación mediante sesión HTTP.
                .sessionManagement(session ->
                        session
                                .maximumSessions(1)
                )

                // Cuando un usuario no autenticado intenta
                // acceder a una ruta protegida, respondemos
                // con HTTP 401 Unauthorized.
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                );

        return http.build();
    }
}