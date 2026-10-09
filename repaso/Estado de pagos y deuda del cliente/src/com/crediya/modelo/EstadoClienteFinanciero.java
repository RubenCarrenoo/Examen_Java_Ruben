package com.crediya.modelo;

/**
 * ============================================================================
 * ENUMERACIÓN (ENUM) — ESTADOS FINANCIEROS DEL CLIENTE (EXAMEN A)
 * ============================================================================
 * ¿Para qué sirve este Enum?
 * Cumple con el REQUERIMIENTO 4 DEL EXAMEN A, que solicita clasificar al cliente
 * en exactamente uno de cuatro resultados posibles:
 *
 * 1. PAGADO:
 *    - No existe saldo pendiente en ninguno de los préstamos del cliente (deuda = $0).
 *
 * 2. PENDIENTE:
 *    - Existe deuda pendiente (saldo > $0), pero el cliente va al día con todas
 *      sus cuotas mensuales y no tiene cuotas vencidas impagas.
 *
 * 3. EN_MORA:
 *    - Existe al menos una cuota mensual que ya venció en el calendario y no fue pagada,
 *      o el préstamo fue marcado manualmente como vencido.
 *
 * 4. SIN_PRESTAMOS:
 *    - El cliente está registrado en el sistema pero nunca se le ha otorgado
 *      ningún préstamo.
 */
public enum EstadoClienteFinanciero {

    /** Todos los créditos del cliente tienen saldo cero ($0). */
    PAGADO,

    /** El cliente debe dinero pero todas sus cuotas exigibles a la fecha están cubiertas. */
    PENDIENTE,

    /** El cliente tiene al menos una cuota vencida exigible sin pagar. */
    EN_MORA,

    /** El cliente no tiene ningún préstamo asociado en el sistema. */
    SIN_PRESTAMOS
}
