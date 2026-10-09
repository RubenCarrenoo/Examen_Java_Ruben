-- =====================================================================
-- CrediYa S.A.S. — Datos de Prueba para Examen B
-- Historial y Búsqueda Avanzada de Pagos
-- =====================================================================

-- 1. Seleccionar la base de datos
USE crediya_db;

-- 2. Inserción de abonos distribuidos para probar filtros y ordenamiento:
--    - Muestra pagos en distintos meses (febrero a junio de 2026).
--    - Muestra pagos con diferentes montos (desde 200.000 hasta 500.000).
--    - Muestra los 3 estados: APROBADO (4 pagos), RECHAZADO (1 pago), PENDIENTE (1 pago).

INSERT INTO pagos (prestamo_id, fecha_pago, monto, estado) VALUES
(1, '2026-02-10', 330000.00, 'APROBADO'),   -- Pago mes 1
(1, '2026-03-12', 330000.00, 'APROBADO'),   -- Pago mes 2
(1, '2026-04-15', 500000.00, 'RECHAZADO'),  -- Pago mes 3 devuelto (fondos insuficientes)
(1, '2026-04-20', 330000.00, 'APROBADO'),   -- Reintento del pago del mes 3 exitoso
(1, '2026-05-10', 200000.00, 'PENDIENTE'),  -- Abono parcial en canje
(1, '2026-06-05', 450000.00, 'APROBADO');   -- Abono extraordinario aprobado
