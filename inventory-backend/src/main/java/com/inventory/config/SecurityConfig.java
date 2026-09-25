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
import org.springframework.http.HttpMethod;

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

    /**
     * Configura BCrypt como algoritmo para encriptar
     * y verificar las contraseñas de los usuarios.
     *
     * @return codificador de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Configura el proveedor de autenticación utilizando
     * el servicio que busca los usuarios en la base de datos.
     *
     * @param usuarioDetailsService servicio encargado de cargar usuarios
     * @return proveedor de autenticación configurado
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UsuarioDetailsService usuarioDetailsService) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        // Indica cómo Spring Security debe obtener los usuarios.
        provider.setUserDetailsService(usuarioDetailsService);

        // Indica que las contraseñas utilizan BCrypt.
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }


    /**
     * Crea el administrador de autenticación utilizado
     * por el sistema para validar las credenciales.
     *
     * @param authenticationConfiguration configuración de autenticación
     * @return administrador de autenticación
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }


    /**
     * Configura las reglas principales de seguridad de la API.
     *
     * Define qué rutas son públicas y cuáles requieren
     * autenticación o un rol específico.
     *
     * @param http configuración de seguridad HTTP
     * @return cadena de filtros de seguridad
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

                // Deshabilitamos CSRF porque trabajamos con una API REST
                // y actualmente manejamos la autenticación mediante sesión HTTP.
                .csrf(csrf -> csrf.disable())


                // ==================== AUTORIZACIÓN ====================

                .authorizeHttpRequests(auth -> auth

                        // Login, registro y logout permanecen públicos.
                        .requestMatchers("/api/v1/auth/**").permitAll()


                        // Proveedores: consultar lo puede hacer cualquier usuario
                        // autenticado (Administrador u Operador), según RF-17.
                        .requestMatchers(HttpMethod.GET, "/api/v1/proveedores/**")
                        .hasAnyRole("ADMINISTRADOR", "OPERADOR")

                        // Proveedores: registrar, editar, desactivar y reactivar
                        // quedan reservados al Administrador, según RF-16, RF-18 y RF-19.
                        .requestMatchers(HttpMethod.POST, "/api/v1/proveedores/**")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/proveedores/**")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/proveedores/**")
                        .hasRole("ADMINISTRADOR")

                        // Dashboard: es de solo lectura, así que cualquier usuario
                        // autenticado (Administrador u Operador) puede consultarlo.
                        .requestMatchers(HttpMethod.GET, "/api/v1/dashboard/**")
                        .hasAnyRole("ADMINISTRADOR", "OPERADOR")


                        // La gestión de usuarios está protegida y solamente
                        // puede ser utilizada por usuarios con rol
                        // ADMINISTRADOR.
                        .requestMatchers("/api/v1/usuarios/**")
                        .hasRole("ADMINISTRADOR")


                        // Todas las demás rutas requieren que el usuario
                        // haya iniciado sesión.
                        .anyRequest().authenticated()
                )


                // ==================== SESIONES ====================

                .sessionManagement(session ->
                        session

                                // Un mismo usuario solamente puede mantener
                                // una sesión activa al mismo tiempo.
                                .maximumSessions(1)
                )


                // ==================== MANEJO DE ERRORES ====================

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                new HttpStatusEntryPoint(
                                        HttpStatus.UNAUTHORIZED
                                )
                        )
                );


        // Construye y devuelve la configuración de seguridad.
        return http.build();
    }
}