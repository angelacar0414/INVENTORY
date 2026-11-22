-- ============================================================
-- PROYECTO INVENTORY - Script de instalación completa
-- Versión 1.0  |  MySQL 8.0
-- Crea la base de datos inventory_db con sus 6 tablas, llaves,
-- restricciones e índices, y carga datos iniciales de ejemplo.
-- Orden: primero las tablas sin llaves foráneas y al final
-- la tabla movimiento, que depende de las demás.
-- ============================================================

-- 1) Base de datos
CREATE DATABASE IF NOT EXISTS inventory_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE inventory_db;

-- 2) Tabla usuario (personas que ingresan al sistema)
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario            BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre                VARCHAR(255),
    apellido              VARCHAR(255),
    username              VARCHAR(255) NOT NULL,
    email                 VARCHAR(255) NOT NULL,
    `contraseña_encriptada` VARCHAR(255) NOT NULL,
    rol                   VARCHAR(255) NOT NULL,
    activo                BOOLEAN DEFAULT TRUE,
    fecha_creacion        DATETIME(6),
    fecha_actualizacion   DATETIME(6),
    CONSTRAINT uk_usuario_username UNIQUE (username),
    CONSTRAINT uk_usuario_email    UNIQUE (email),
    CONSTRAINT chk_usuario_rol     CHECK (rol IN ('ADMINISTRADOR', 'OPERADOR'))
);

-- 3) Tabla categoria (clasificación de productos)
CREATE TABLE IF NOT EXISTS categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    descripcion  VARCHAR(255),
    activo       BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_categoria_nombre UNIQUE (nombre)
);

-- 4) Tabla proveedor (quien suministra los productos)
CREATE TABLE IF NOT EXISTS proveedor (
    id_proveedor BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(150) NOT NULL,
    documento    VARCHAR(30),
    telefono     VARCHAR(20),
    correo       VARCHAR(120),
    direccion    VARCHAR(200),
    activo       BOOLEAN NOT NULL DEFAULT TRUE
);

-- 5) Tabla cliente (quien recibe productos en las salidas)
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(150) NOT NULL,
    documento  VARCHAR(30),
    telefono   VARCHAR(20),
    correo     VARCHAR(120),
    direccion  VARCHAR(200),
    activo     BOOLEAN NOT NULL DEFAULT TRUE
);

-- 6) Tabla producto (entidad central del inventario)
CREATE TABLE IF NOT EXISTS producto (
    id_producto   BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo        VARCHAR(30)  NOT NULL,
    nombre        VARCHAR(150) NOT NULL,
    descripcion   VARCHAR(255),
    precio_compra DECIMAL(10,2) NOT NULL,
    precio_venta  DECIMAL(10,2) NOT NULL,
    stock_actual  INT NOT NULL DEFAULT 0,
    stock_minimo  INT NOT NULL DEFAULT 0,
    estado        ENUM('DISPONIBLE', 'AGOTADO', 'INACTIVO') NOT NULL DEFAULT 'AGOTADO',
    activo        BOOLEAN NOT NULL DEFAULT TRUE,
    id_categoria  BIGINT NOT NULL,
    id_proveedor  BIGINT NOT NULL,
    CONSTRAINT uk_producto_codigo UNIQUE (codigo),
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria),
    CONSTRAINT fk_producto_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor),
    CONSTRAINT chk_producto_stock_actual  CHECK (stock_actual >= 0),
    CONSTRAINT chk_producto_stock_minimo  CHECK (stock_minimo >= 0),
    CONSTRAINT chk_producto_precio_compra CHECK (precio_compra >= 0),
    CONSTRAINT chk_producto_precio_venta  CHECK (precio_venta >= 0)
);

-- 7) Tabla movimiento (historial de entradas y salidas, no se edita ni se borra)
CREATE TABLE IF NOT EXISTS movimiento (
    id_movimiento    BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_movimiento  ENUM('ENTRADA', 'SALIDA') NOT NULL,
    cantidad         INT NOT NULL,
    stock_anterior   INT NOT NULL,
    stock_posterior  INT NOT NULL,
    fecha_movimiento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion      VARCHAR(255),
    id_producto      BIGINT NOT NULL,
    id_usuario       BIGINT NOT NULL,
    id_cliente       BIGINT NULL,
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto),
    CONSTRAINT fk_movimiento_usuario  FOREIGN KEY (id_usuario)  REFERENCES usuario (id_usuario),
    CONSTRAINT fk_movimiento_cliente  FOREIGN KEY (id_cliente)  REFERENCES cliente (id_cliente),
    CONSTRAINT chk_movimiento_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_movimiento_stock    CHECK (stock_anterior >= 0 AND stock_posterior >= 0)
);

-- 8) Índices para las búsquedas más comunes (historial y reportes)
CREATE INDEX idx_movimiento_fecha    ON movimiento (fecha_movimiento);
CREATE INDEX idx_movimiento_tipo     ON movimiento (tipo_movimiento);
CREATE INDEX idx_producto_nombre     ON producto (nombre);

-- 9) Datos iniciales de ejemplo
INSERT INTO categoria (nombre, descripcion) VALUES
    ('Materia Prima', 'Insumos base para producción'),
    ('Producto Terminado', 'Artículos listos para la venta'),
    ('Papelería', 'Artículos de oficina y papelería');

INSERT INTO proveedor (nombre, documento, telefono, correo, direccion) VALUES
    ('Proveedor de prueba', '900000001', '3000000000', 'proveedor@ejemplo.com', 'Dirección de prueba');

INSERT INTO cliente (nombre, documento, telefono, correo, direccion) VALUES
    ('Cliente de prueba', '1000000001', '3100000000', 'cliente@ejemplo.com', 'Dirección de prueba');

-- 10) Primer usuario ADMINISTRADOR
-- Por seguridad este script NO trae contraseñas.
-- a) Registrar el usuario desde la pantalla de inicio de sesión
--    (o con POST /api/v1/auth/registrar). El sistema lo crea como OPERADOR
--    y guarda la contraseña cifrada con BCrypt.
-- b) Darle el rol de administrador con esta sentencia
--    (cambiar 'admin' por el username registrado):
-- UPDATE usuario SET rol = 'ADMINISTRADOR' WHERE username = 'admin';
