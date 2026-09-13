package com.inventory.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CLASE DE CONFIGURACIÓN
 * ------------------------
 * Aquí le decimos al Backend que confíe en las peticiones que
 * vengan desde el Frontend (React), que corre en otro puerto (5173).
 * Sin esto, el navegador bloquea las peticiones por seguridad (CORS).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
