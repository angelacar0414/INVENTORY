CREATE TABLE IF NOT EXISTS producto (

    id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,

    codigo VARCHAR(30) NOT NULL UNIQUE,

    nombre VARCHAR(150) NOT NULL,

    descripcion VARCHAR(255),

    precio_compra DECIMAL(10,2) NOT NULL,

    precio_venta DECIMAL(10,2) NOT NULL,

    stock_actual INT NOT NULL DEFAULT 0,

    stock_minimo INT NOT NULL DEFAULT 0,

    estado ENUM(
        'DISPONIBLE',
        'AGOTADO',
        'INACTIVO'
    ) NOT NULL DEFAULT 'AGOTADO',

    activo BOOLEAN NOT NULL DEFAULT TRUE,

    id_categoria BIGINT NOT NULL,

    id_proveedor BIGINT NOT NULL,

    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categoria(id_categoria),

    CONSTRAINT fk_producto_proveedor
        FOREIGN KEY (id_proveedor)
        REFERENCES proveedor(id_proveedor),

    CONSTRAINT chk_producto_stock_actual
        CHECK (stock_actual >= 0),

    CONSTRAINT chk_producto_stock_minimo
        CHECK (stock_minimo >= 0),

    CONSTRAINT chk_producto_precio_compra
        CHECK (precio_compra >= 0),

    CONSTRAINT chk_producto_precio_venta
        CHECK (precio_venta >= 0)

);