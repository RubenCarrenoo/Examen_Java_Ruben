package com.crediya.modelo;

import com.crediya.util.Formato;

/**
 * ============================================================================
 * CLASE DTO — RESUMEN FINANCIERO DEL HISTORIAL DE PAGOS (EXAMEN B)
 * ============================================================================
 * ¿Para qué sirve esta clase?
 * Cumple con el REQUERIMIENTO 4 DEL EXAMEN B:
 * "Mostrar cantidad de pagos, total aprobado, total pendiente y total rechazado.
 *  Los valores deben calcularse por separado, evitando sumar un mismo pago en varias categorías."
 *
 * CONCEPTOS CLAVE APLICADOS:
 * 1. INMUTABILIDAD: Todos los campos son 'final', asignados en el constructor.
 * 2. CÁLCULO DISJUNTO: Mantiene separados los contadores y los acumuladores de dinero
 *    para cada estado (Aprobado, Pendiente, Rechazado).
 * 3. FORMATO PROFESIONAL: Proporciona un método 'reporteFormateado()' para imprimir
 *    el balance en consola sin necesidad de escribir código repetitivo en la vista.
 */
public class ResumenHistorialPagos {

    // Total de registros de pagos analizados
    private final int cantidadTotal;

    // Número de pagos con estado APROBADO
    private final int cantidadAprobados;

    // Número de pagos con estado PENDIENTE
    private final int cantidadPendientes;

    // Número de pagos con estado RECHAZADO
    private final int cantidadRechazados;

    // Dinero total acumulado de pagos APROBADOS (que redujeron saldo)
    private final double totalAprobado;

    // Dinero total en pagos PENDIENTES (en canje bancario)
    private final double totalPendiente;

    // Dinero total en pagos RECHAZADOS (fondos insuficientes o anulados)
    private final double totalRechazado;

    /**
     * Constructor con todos los campos calculados por el gestor.
     */
    public ResumenHistorialPagos(int cantidadTotal, int cantidadAprobados, int cantidadPendientes,
                                 int cantidadRechazados, double totalAprobado, double totalPendiente,
                                 double totalRechazado) {
        this.cantidadTotal = cantidadTotal;
        this.cantidadAprobados = cantidadAprobados;
        this.cantidadPendientes = cantidadPendientes;
        this.cantidadRechazados = cantidadRechazados;
        this.totalAprobado = totalAprobado;
        this.totalPendiente = totalPendiente;
        this.totalRechazado = totalRechazado;
    }

    // ========================================================================
    // GETTERS
    // ========================================================================

    public int getCantidadTotal() {
        return cantidadTotal;
    }

    public int getCantidadAprobados() {
        return cantidadAprobados;
    }

    public int getCantidadPendientes() {
        return cantidadPendientes;
    }

    public int getCantidadRechazados() {
        return cantidadRechazados;
    }

    public double getTotalAprobado() {
        return totalAprobado;
    }

    public double getTotalPendiente() {
        return totalPendiente;
    }

    public double getTotalRechazado() {
        return totalRechazado;
    }

    /**
     * Calcula la suma bruta total de dinero procesado (aprobado + pendiente + rechazado).
     */
    public double getTotalGeneral() {
        return totalAprobado + totalPendiente + totalRechazado;
    }

    /**
     * Retorna una representación tabular lista para mostrar en el menú de consola.
     */
    public String reporteFormateado() {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------------------------\n");
        sb.append("                  RESUMEN DEL HISTORIAL DE PAGOS                    \n");
        sb.append("--------------------------------------------------------------------\n");
        sb.append(String.format("Total Pagos Registrados: %d\n", cantidadTotal));
        sb.append(String.format("  • Aprobados:           %d  |  Monto: %s\n", cantidadAprobados, Formato.moneda(totalAprobado)));
        sb.append(String.format("  • Pendientes:          %d  |  Monto: %s\n", cantidadPendientes, Formato.moneda(totalPendiente)));
        sb.append(String.format("  • Rechazados:          %d  |  Monto: %s\n", cantidadRechazados, Formato.moneda(totalRechazado)));
        sb.append("--------------------------------------------------------------------\n");
        sb.append(String.format("Monto Total Procesado:   %s\n", Formato.moneda(getTotalGeneral())));
        sb.append("--------------------------------------------------------------------\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Resumen[Total: %d, Aprobado: %s, Pendiente: %s, Rechazado: %s]",
                cantidadTotal, Formato.moneda(totalAprobado), Formato.moneda(totalPendiente), Formato.moneda(totalRechazado));
    }
}
