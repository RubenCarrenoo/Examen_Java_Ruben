package com.crediya.modelo;

import com.crediya.util.Formato;
import java.time.LocalDate;

/**
 * ============================================================================
 * CLASE MODELO ADAPTADA — PAGO CON ESTADO
 * ============================================================================
 * ¿Para qué sirve esta clase?
 * Resuelve la adaptación requerida por el examen para que cada pago tenga un
 * estado (APROBADO, PENDIENTE, RECHAZADO), sin romper la clase Pago original.
 *
 * CONCEPTOS FUNDAMENTALES DE JAVA UTILIZADOS AQUÍ:
 *
 * 1. HERENCIA (extends Pago):
 *    - 'PagoConEstado' hereda todos los atributos y métodos de la clase 'Pago':
 *      id, prestamoId, fechaPago, monto, getId(), getMonto(), etc.
 *
 * 2. PALABRA CLAVE 'super(...)':
 *    - Se usa en los constructores para invocar el constructor de la clase padre (Pago).
 *    - IMPORTANTE PARA EL EXAMEN: 'super(...)' DEBE ser siempre la PRIMERA línea
 *      del constructor de la clase hija. Le pasa id, prestamoId, fechaPago y monto
 *      a la clase padre para que ella los inicialice.
 *
 * 3. SOBRECARGA DE CONSTRUCTORES (Overloading):
 *    - Tenemos 2 constructores con distinta cantidad de parámetros: uno que recibe
 *      el estado y otro que asume EstadoPago.APROBADO por defecto.
 *
 * 4. REUTILIZACIÓN CON 'super.toString()':
 *    - Sobrescribe (@Override) el método toString(), pero en vez de reescribir todo,
 *      llama a 'super.toString()' y solo le concatena el estado.
 */
public class PagoConEstado extends Pago {

    // Atributo propio de esta clase hija: estado del abono
    private EstadoPago estado;

    /**
     * Constructor 1: Crea un pago asumiendo que está APROBADO por defecto.
     * Útil para mantener compatibilidad con los pagos ya existentes en la base de datos.
     */
    public PagoConEstado(int id, int prestamoId, LocalDate fechaPago, double monto) {
        // Invocamos al constructor de la clase padre Pago(int, int, LocalDate, double)
        super(id, prestamoId, fechaPago, monto);
        this.estado = EstadoPago.APROBADO;
    }

    /**
     * Constructor 2: Crea un pago especificando explícitamente su estado.
     */
    public PagoConEstado(int id, int prestamoId, LocalDate fechaPago, double monto, EstadoPago estado) {
        // Invocamos al constructor padre
        super(id, prestamoId, fechaPago, monto);
        // Operador ternario: si viene nulo, lo protege asignando APROBADO por seguridad
        this.estado = (estado != null) ? estado : EstadoPago.APROBADO;
    }

    // ========================================================================
    // GETTERS Y SETTERS
    // ========================================================================

    /** Retorna el estado actual del pago (APROBADO, PENDIENTE o RECHAZADO) */
    public EstadoPago getEstado() {
        return estado;
    }

    /** Permite modificar el estado (ej. cuando un pago en verificación pasa a aprobado) */
    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    /**
     * Método de conveniencia: devuelve 'true' si el pago está APROBADO.
     * Permite escribir código limpio como: if (pago.isAprobado()) { ... }
     */
    public boolean isAprobado() {
        return this.estado == EstadoPago.APROBADO;
    }

    /**
     * Sobrescribe el toString() para incluir el estado.
     * super.toString() genera: "Pago #1 | Préstamo #101 | Fecha: 2026-10-08 | Monto: $220.000"
     * Y le añadimos: " | Estado: APROBADO"
     */
    @Override
    public String toString() {
        return super.toString() + " | Estado: " + estado;
    }
}
