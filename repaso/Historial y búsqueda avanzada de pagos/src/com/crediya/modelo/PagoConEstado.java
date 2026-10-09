package com.crediya.modelo;

import java.time.LocalDate;

/**
 * ============================================================================
 * MODELO ADAPTADO — PAGO CON ESTADO (EXAMEN B)
 * ============================================================================
 * ¿Para qué sirve?
 * Extiende la clase 'Pago' para incorporar la propiedad 'EstadoPago estado'.
 *
 * Utiliza 'super(...)' para invocar el constructor del padre con:
 *   super(id, prestamoId, fechaPago, monto);
 */
public class PagoConEstado extends Pago {

    private EstadoPago estado;

    /** Constructor por defecto: asigna APROBADO */
    public PagoConEstado(int id, int prestamoId, LocalDate fechaPago, double monto) {
        super(id, prestamoId, fechaPago, monto);
        this.estado = EstadoPago.APROBADO;
    }

    /** Constructor completo con estado explícito */
    public PagoConEstado(int id, int prestamoId, LocalDate fechaPago, double monto, EstadoPago estado) {
        super(id, prestamoId, fechaPago, monto);
        this.estado = (estado != null) ? estado : EstadoPago.APROBADO;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public boolean isAprobado() {
        return this.estado == EstadoPago.APROBADO;
    }

    @Override
    public String toString() {
        return super.toString() + " | Estado: " + estado;
    }
}
