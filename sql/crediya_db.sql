-- =====================================================================
--  CrediYa S.A.S. - Script de base de datos (MySQL 8)
--  Sistema de gestión de préstamos y cobros de cartera
-- ---------------------------------------------------------------------
--  Cambios respecto al script base:
--   * prestamos: se agregan cuota_mensual, monto_total y saldo_pendiente
--     (el sistema los calcula y los usa para pagos y reportes).
--   * documento y correo son UNIQUE en empleados y clientes.
--   * NOT NULL, valores por defecto y CHECK para evitar datos inválidos.
--   * pagos.monto pasa a DECIMAL(12,2) para soportar abonos grandes.
--   * Índices para las búsquedas más usadas.
--   * Datos de prueba al final (opcionales).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS crediya_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE crediya_db;

-- ---------------------------------------------------------------------
-- EMPLEADOS
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS empleados (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(80)    NOT NULL,
    documento  VARCHAR(30)    NOT NULL,
    rol        VARCHAR(30)    NOT NULL,
    correo     VARCHAR(80)    NOT NULL,
    salario    DECIMAL(10,2)  NOT NULL,
    CONSTRAINT uq_empleados_documento UNIQUE (documento),
    CONSTRAINT uq_empleados_correo    UNIQUE (correo),
    CONSTRAINT ck_empleados_rol       CHECK (rol IN ('ADMINISTRADOR', 'ASESOR', 'COBRADOR')),
    CONSTRAINT ck_empleados_salario   CHECK (salario > 0)
);

-- ---------------------------------------------------------------------
-- CLIENTES
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clientes (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(80)  NOT NULL,
    documento  VARCHAR(30)  NOT NULL,
    correo     VARCHAR(80)  NOT NULL,
    telefono   VARCHAR(20)  NOT NULL,
    CONSTRAINT uq_clientes_documento UNIQUE (documento),
    CONSTRAINT uq_clientes_correo    UNIQUE (correo)
);

-- ---------------------------------------------------------------------
-- PRÉSTAMOS
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS prestamos (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id       INT            NOT NULL,
    empleado_id      INT            NOT NULL,
    monto            DECIMAL(12,2)  NOT NULL,
    interes          DECIMAL(5,2)   NOT NULL,
    cuotas           INT            NOT NULL,
    cuota_mensual    DECIMAL(14,2)  NOT NULL,
    fecha_inicio     DATE           NOT NULL,
    estado           VARCHAR(20)    NOT NULL DEFAULT 'PENDIENTE',
    monto_total      DECIMAL(14,2)  NOT NULL,
    saldo_pendiente  DECIMAL(14,2)  NOT NULL,
    CONSTRAINT fk_prestamos_cliente  FOREIGN KEY (cliente_id)  REFERENCES clientes(id),
    CONSTRAINT fk_prestamos_empleado FOREIGN KEY (empleado_id) REFERENCES empleados(id),
    CONSTRAINT ck_prestamos_monto    CHECK (monto > 0),
    CONSTRAINT ck_prestamos_interes  CHECK (interes BETWEEN 0 AND 100),
    CONSTRAINT ck_prestamos_cuotas   CHECK (cuotas BETWEEN 1 AND 120),
    CONSTRAINT ck_prestamos_estado   CHECK (estado IN ('PENDIENTE', 'PAGADO')),
    CONSTRAINT ck_prestamos_saldo    CHECK (saldo_pendiente >= 0)
);

CREATE INDEX idx_prestamos_estado ON prestamos (estado);

-- ---------------------------------------------------------------------
-- PAGOS
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pagos (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    prestamo_id  INT            NOT NULL,
    fecha_pago   DATE           NOT NULL,
    monto        DECIMAL(12,2)  NOT NULL,
    CONSTRAINT fk_pagos_prestamo FOREIGN KEY (prestamo_id) REFERENCES prestamos(id),
    CONSTRAINT ck_pagos_monto    CHECK (monto > 0)
);

CREATE INDEX idx_pagos_prestamo ON pagos (prestamo_id);

-- =====================================================================
--  DATOS DE PRUEBA (opcional)
--  Préstamo 1: activo al día. Préstamo 2: pagado. Préstamo 3: vencido
--  (cliente moroso). Ejecute esta sección solo en una base vacía.
-- =====================================================================

INSERT INTO empleados (nombre, documento, rol, correo, salario) VALUES
('Laura Gómez',  '1098111222', 'ADMINISTRADOR', 'laura.gomez@crediya.com',  4500000.00),
('Carlos Ruiz',  '1098333444', 'ASESOR',        'carlos.ruiz@crediya.com',  2800000.00),
('Diana Castro', '1098555666', 'COBRADOR',      'diana.castro@crediya.com', 2200000.00);

INSERT INTO clientes (nombre, documento, correo, telefono) VALUES
('Andrea Martínez', '1095222333', 'andrea.martinez@gmail.com', '3157778899'),
('Jorge Pabón',     '91234567',   'jorge.pabon@hotmail.com',   '3204445566'),
('Sofía Rangel',    '1005666777', 'sofia.rangel@outlook.com',  '6076543210');

INSERT INTO prestamos
(cliente_id, empleado_id, monto, interes, cuotas, cuota_mensual, fecha_inicio, estado, monto_total, saldo_pendiente)
VALUES
(1, 2, 2000000.00, 10.00, 6, 366666.67, '2026-06-01', 'PENDIENTE', 2200000.00, 1466666.66),
(2, 2, 1000000.00,  5.00, 2, 525000.00, '2026-01-15', 'PAGADO',    1050000.00,       0.00),
(3, 1, 3000000.00, 12.00, 6, 560000.00, '2025-09-01', 'PENDIENTE', 3360000.00, 2800000.00);

INSERT INTO pagos (prestamo_id, fecha_pago, monto) VALUES
(1, '2026-07-01', 366666.67),
(1, '2026-08-01', 366666.67),
(2, '2026-02-15', 525000.00),
(2, '2026-03-15', 525000.00),
(3, '2025-10-01', 560000.00);
