-- ============================================================================
-- SCRIPT SQL Y ESTRATEGIA DE INTEGRIDAD — EXAMEN 3: CREDÍYA S.A.S.
-- ============================================================================

-- CONTEXTO:
-- La validación previa en Java mediante Stream API consulta la base de datos y compara.
-- Sin embargo, bajo alta concurrencia (dos cajeros o procesos registrando exactamente
-- el mismo pago en el mismo milisegundo), ambas consultas podrían pasar antes de que
-- cualquiera de las dos inserte (Race Condition / Condición de Carrera).

-- SOLUCIÓN A NIVEL DE BASE DE DATOS (RECOMENDADA):
-- Se puede definir un índice único compuesto sobre (prestamo_id, fecha_pago, monto):

-- 1. Verificar si existen duplicados históricos antes de aplicar el índice
SELECT prestamo_id, fecha_pago, monto, COUNT(*) AS repeticiones
FROM pagos
GROUP BY prestamo_id, fecha_pago, monto
HAVING COUNT(*) > 1;

-- 2. Si las reglas de negocio establecen que no pueden existir dos abonos exactamente
-- iguales en el mismo préstamo en la misma fecha calendario, se aplica el siguiente índice único:
-- ALTER TABLE pagos ADD CONSTRAINT uq_pago_duplicado UNIQUE (prestamo_id, fecha_pago, monto);

-- NOTA TÉCNICA:
-- En caso de que el negocio permita que un cliente realice dos abonos iguales en ventanilla
-- el mismo día (por ejemplo dos depósitos de $250.000 a diferentes horas), se recomienda
-- agregar una columna `numero_transaccion` VARCHAR(50) UNIQUE o `hora_pago` TIME.
