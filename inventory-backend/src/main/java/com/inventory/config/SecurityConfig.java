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
 * @author Dario Bustamante
 * @version 1.0
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UsuarioDetailsService usuarioDetailsService) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // Login y registro son públicos.
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Temporal: proveedores abierto mientras se resuelve el login con sesión real
                        .requestMatchers("/api/v1/proveedores/**").permitAll()

                        // El resto de la API requiere autenticación.
                        .anyRequest().authenticated()
                )

                .sessionManagement(session ->
                        session
                                .maximumSessions(1)
                )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                );

        return http.build();
    }
}