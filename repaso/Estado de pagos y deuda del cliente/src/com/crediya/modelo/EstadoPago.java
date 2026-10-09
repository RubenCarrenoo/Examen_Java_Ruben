package com.crediya.modelo;

/**
 * ============================================================================
 * ENUMERACIÓN (ENUM) — ESTADO DE UN PAGO
 * ============================================================================
 * ¿Para qué sirve un enum en Java?
 * Un enum define un conjunto fijo y cerrado de constantes con nombre.
 *
 * ¿Por qué usar un Enum en lugar de un String o un int?
 * 1. SEGURIDAD DE TIPOS (Type-Safety): El compilador solo permite asignar uno
 *    de estos 3 valores. Es imposible cometer errores de tipeo como "aprovado"
 *    o "Aprobado".
 * 2. LEGIBILIDAD: El código es autoexplicativo al leerlo.
 * 3. CONTROL DE REGLAS DE NEGOCIO (Examen A y B):
 *    - APROBADO: El pago es efectivo y descuenta saldo del préstamo.
 *    - PENDIENTE: El abono está en proceso (ej. cheque en canje); NO descuenta saldo.
 *    - RECHAZADO: El abono rebotó o fue devuelto; NO descuenta saldo.
 */
public enum EstadoPago {

    /** El dinero ingresó exitosamente a la cuenta de CrediYa; reduce la deuda del cliente. */
    APROBADO,

    /** El pago fue registrado pero aún no ha sido confirmado por el banco; NO descuenta deuda. */
    PENDIENTE,

    /** El pago fue rechazado por fondos insuficientes u orden judicial; NO descuenta deuda. */
    RECHAZADO
}
