-- ============================================================
-- PROYECTO INVENTORY - Script de base de datos
-- Módulo: CATEGORÍAS (versión Servlets + JSP)
-- Si ya tienes esta tabla creada de la actividad anterior,
-- NO hace falta volver a ejecutar este script.
-- ============================================================

CREATE DATABASE IF NOT EXISTS inventory_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE inventory_db;

CREATE TABLE IF NOT EXISTS categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL UNIQUE,
    descripcion  VARCHAR(255),
    activo       BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO categoria (nombre, descripcion, activo) VALUES
    ('Accesorios para moto', 'CarPlay, soportes para celular, camaras, sliders', TRUE),
    ('Indumentaria motera', 'Chaquetas, cascos, guantes', TRUE),
    ('Repuestos y otros', 'Llaveros y accesorios varios', TRUE);
