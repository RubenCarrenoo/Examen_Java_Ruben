-- =====================================================================
-- CrediYa S.A.S. — Script de Actualización para Examen A
-- Objetivo: Añadir soporte de estados para los pagos en MySQL
--           (APROBADO, PENDIENTE, RECHAZADO)
-- =====================================================================

-- 1. Seleccionar la base de datos del proyecto CrediYa
USE crediya_db;

-- 2. Modificar la estructura de la tabla 'pagos' agregando la columna 'estado'
--    - ENUM('APROBADO', 'PENDIENTE', 'RECHAZADO'): Restringe los valores permitidos a nivel de motor de BD.
--    - NOT NULL: Obliga a que todo pago tenga un estado.
--    - DEFAULT 'APROBADO': Asegura que si alguien hace un INSERT sin especificar estado, quede como Aprobado.
--    - AFTER monto: Ubica visualmente la columna justo después del monto del pago.
ALTER TABLE pagos
ADD COLUMN estado ENUM('APROBADO', 'PENDIENTE', 'RECHAZADO') NOT NULL DEFAULT 'APROBADO'
AFTER monto;

-- 3. Actualizar los pagos preexistentes en la base de datos
--    Garantiza que ningún registro histórico quede con valor nulo.
UPDATE pagos SET estado = 'APROBADO' WHERE estado IS NULL;

-- 4. Inserción de registros de prueba (Descomentar para probar con datos reales):
-- Pago 1: Aprobado normal (descuenta saldo)
-- INSERT INTO pagos (prestamo_id, fecha_pago, monto, estado) VALUES (1, '2026-09-15', 220000.00, 'APROBADO');

-- Pago 2: Rechazado por fondos insuficientes (NO debe descontar saldo)
-- INSERT INTO pagos (prestamo_id, fecha_pago, monto, estado) VALUES (1, '2026-09-20', 500000.00, 'RECHAZADO');

-- Pago 3: Pendiente en canje bancario (NO debe descontar saldo todavía)
-- INSERT INTO pagos (prestamo_id, fecha_pago, monto, estado) VALUES (1, '2026-10-01', 300000.00, 'PENDIENTE');
