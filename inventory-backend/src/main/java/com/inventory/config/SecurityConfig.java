package com.inventory.config;

import com.inventory.auth.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
 * Configuración principal de seguridad de la aplicación INVENTORY.
 *
 * Esta clase define:
 *
 * - El mecanismo de autenticación mediante usuarios registrados.
 * - El algoritmo utilizado para encriptar las contraseñas.
 * - Los permisos de acceso según el rol del usuario.
 * - La cantidad máxima de sesiones permitidas.
 * - La respuesta HTTP cuando un usuario no autenticado intenta
 *   acceder a un recurso protegido.
 *
 * Roles utilizados en el proyecto:
 *
 * - ADMINISTRADOR
 * - OPERADOR
 */
@Configuration
public class SecurityConfig {

    /**
     * Bean encargado de encriptar y verificar las contraseñas.
     *
     * BCrypt es utilizado para almacenar las contraseñas de los
     * usuarios de forma segura.
     *
     * @return codificador de contraseñas BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el proveedor de autenticación.
     *
     * UsuarioDetailsService se encarga de buscar el usuario
     * en la base de datos.
     *
     * PasswordEncoder permite comparar la contraseña ingresada
     * con la contraseña almacenada.
     *
     * @param usuarioDetailsService servicio que obtiene los usuarios
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
     * Crea el AuthenticationManager utilizado por Spring Security
     * para realizar el proceso de autenticación.
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
     * Configuración principal de las reglas de seguridad.
     *
     * Se establecen los permisos de acceso a los diferentes
     * endpoints de la API según el método HTTP y el rol del usuario.
     *
     * @param http configuración HTTP de Spring Security
     * @return cadena de filtros de seguridad
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            /*
             * Se deshabilita CSRF porque actualmente la API utiliza
             * autenticación mediante sesión HTTP y comunicación
             * con el frontend mediante API REST.
             */
            .csrf(csrf -> csrf.disable())

            /*
             * Configuración de autorización de endpoints.
             */
            .authorizeHttpRequests(auth -> auth

                /*
                 * Los endpoints de autenticación son públicos.
                 *
                 * Ejemplo:
                 * POST /api/v1/auth/login
                 */
                .requestMatchers("/api/v1/auth/**")
                .permitAll()

                /*
                 * ==============================
                 * MÓDULO PROVEEDORES
                 * ==============================
                 *
                 * Administradores y operadores pueden consultar.
                 */
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/proveedores/**"
                )
                .hasAnyRole("ADMINISTRADOR", "OPERADOR")

                /*
                 * Crear proveedores:
                 * solamente ADMINISTRADOR.
                 */
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/v1/proveedores/**"
                )
                .hasRole("ADMINISTRADOR")

                /*
                 * Actualizar proveedores:
                 * solamente ADMINISTRADOR.
                 */
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/v1/proveedores/**"
                )
                .hasRole("ADMINISTRADOR")

                /*
                 * Eliminar/desactivar proveedores:
                 * solamente ADMINISTRADOR.
                 */
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/v1/proveedores/**"
                )
                .hasRole("ADMINISTRADOR")

                /*
                 * ==============================
                 * DASHBOARD
                 * ==============================
                 *
                 * Administradores y operadores pueden consultar
                 * las estadísticas del inventario.
                 */
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/dashboard/**"
                )
                .hasAnyRole("ADMINISTRADOR", "OPERADOR")

                /*
                 * ==============================
                 * MÓDULO PRODUCTOS
                 * ==============================
                 *
                 * Administradores y operadores pueden consultar
                 * los productos.
                 */
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/productos/**"
                )
                .hasAnyRole("ADMINISTRADOR", "OPERADOR")

                /*
                 * Crear productos:
                 * solamente ADMINISTRADOR.
                 */
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/v1/productos/**"
                )
                .hasRole("ADMINISTRADOR")

                /*
                 * Actualizar y reactivar productos:
                 * solamente ADMINISTRADOR.
                 */
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/v1/productos/**"
                )
                .hasRole("ADMINISTRADOR")

                /*
                 * Desactivar productos:
                 * solamente ADMINISTRADOR.
                 */
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/v1/productos/**"
                )
                .hasRole("ADMINISTRADOR")

                /*
                 * ==============================
                 * MÓDULO USUARIOS
                 * ==============================
                 *
                 * La gestión de usuarios es exclusiva
                 * del administrador.
                 */
                .requestMatchers("/api/v1/usuarios/**")
                .hasRole("ADMINISTRADOR")

                /*
                 * Cualquier otro endpoint requiere
                 * autenticación.
                 */
                .anyRequest()
                .authenticated()
            )

            /*
             * ==============================
             * CONTROL DE SESIONES
             * ==============================
             *
             * Un usuario solamente puede mantener
             * una sesión activa simultáneamente.
             */
            .sessionManagement(session ->
                session.maximumSessions(1)
            )

            /*
             * ==============================
             * MANEJO DE USUARIOS NO AUTENTICADOS
             * ==============================
             *
             * Cuando un usuario no autenticado intenta
             * acceder a un recurso protegido, la API
             * responde HTTP 401 Unauthorized.
             */
            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(
                    new HttpStatusEntryPoint(
                        HttpStatus.UNAUTHORIZED
                    )
                )
            );

        /*
         * Construye y devuelve la configuración de seguridad.
         */
        return http.build();
    }
}