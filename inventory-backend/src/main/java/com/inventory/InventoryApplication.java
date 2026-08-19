package com.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal. Aquí empieza a ejecutarse todo el backend.
 * Al correr este "main", Spring Boot levanta un servidor en
 * http://localhost:8080 y deja listos todos los endpoints
 * (por ahora, los de /api/v1/categorias).
 */
@SpringBootApplication
public class InventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }
}
