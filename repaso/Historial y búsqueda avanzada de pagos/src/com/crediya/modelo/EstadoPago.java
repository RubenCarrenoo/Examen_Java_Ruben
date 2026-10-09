package com.crediya.modelo;

/**
 * ============================================================================
 * ENUMERACIÓN — ESTADOS DE UN PAGO (EXAMEN B)
 * ============================================================================
 * ¿Para qué sirve este Enum en el Examen B?
 * Permite filtrar pagos usando Stream API:
 *   gestor.filtrarPorEstado(pagos, EstadoPago.APROBADO)
 *
 * Y clasificar el resumen contable sin ambigüedades.
 */
public enum EstadoPago {

    /** Pago exitoso y abonado a la deuda. */
    APROBADO,

    /** Pago registrado pero en proceso de verificación contable. */
    PENDIENTE,

    /** Pago devuelto, anulado o con cheque rebotado. */
    RECHAZADO
}
