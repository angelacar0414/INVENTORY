package com.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Deshabilitamos CSRF porque estamos trabajando con una API REST
                .csrf(csrf -> csrf.disable())

                // Configuración de las rutas
                .authorizeHttpRequests(auth -> auth
                        // Registro y login no requieren autenticación
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}