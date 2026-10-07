-- ============================================================
-- PROYECTO INVENTORY - Script de base de datos
-- Módulo: MOVIMIENTOS
-- ============================================================

USE inventory_db;

-- Crear tabla movimiento
CREATE TABLE IF NOT EXISTS movimiento (

    id_movimiento BIGINT AUTO_INCREMENT PRIMARY KEY,

    tipo_movimiento ENUM('ENTRADA', 'SALIDA') NOT NULL,

    cantidad INT NOT NULL,

    stock_anterior INT NOT NULL,

    stock_posterior INT NOT NULL,

    fecha_movimiento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    observacion VARCHAR(255),

    -- Claves foráneas
    id_producto BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    id_cliente BIGINT,

    -- Restricciones de integridad
    CONSTRAINT fk_movimiento_producto
        FOREIGN KEY (id_producto)
        REFERENCES producto(id_producto),

    CONSTRAINT fk_movimiento_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_movimiento_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente),

    -- Restricciones de negocio
    CONSTRAINT chk_movimiento_cantidad
        CHECK (cantidad > 0),

    CONSTRAINT chk_movimiento_stock_anterior
        CHECK (stock_anterior >= 0),

    CONSTRAINT chk_movimiento_stock_posterior
        CHECK (stock_posterior >= 0)
);

-- Crear índices para optimización
CREATE INDEX idx_movimiento_producto ON movimiento(id_producto);
CREATE INDEX idx_movimiento_usuario ON movimiento(id_usuario);
CREATE INDEX idx_movimiento_cliente ON movimiento(id_cliente);
CREATE INDEX idx_movimiento_fecha ON movimiento(fecha_movimiento);
