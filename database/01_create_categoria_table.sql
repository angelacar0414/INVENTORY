-- ============================================================
-- PROYECTO INVENTORY - Script de base de datos
-- Módulo: CATEGORÍAS (Incremento 1, ver Documento 10 del EKB)
-- ============================================================

-- 1) Crear la base de datos (si no existe)
CREATE DATABASE IF NOT EXISTS inventory_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE inventory_db;

-- 2) Crear la tabla categoria 
CREATE TABLE IF NOT EXISTS categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL UNIQUE,
    descripcion  VARCHAR(255),
    activo       BOOLEAN NOT NULL DEFAULT TRUE
);

-- 3) Datos de ejemplo para poder probar el módulo de una vez
INSERT INTO categoria (nombre, descripcion, activo) VALUES
    ('Materia Prima', 'Insumos base para producción', TRUE),
    ('Producto Terminado', 'Artículos listos para la venta', TRUE),
    ('Papelería', 'Artículos de oficina y papelería', TRUE);
