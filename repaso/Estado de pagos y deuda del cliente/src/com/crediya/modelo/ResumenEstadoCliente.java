package com.crediya.modelo;

import com.crediya.util.Formato;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 * CLASE DTO CONSOLIDADO — RESUMEN DEL ESTADO FINANCIERO DEL CLIENTE
 * ============================================================================
 * ¿Para qué sirve esta clase?
 * Agrupa toda la información resultante de ejecutar el método:
 *   consultarEstadoCliente(int clienteId)
 *
 * Contiene:
 * 1. Los datos de la persona (Cliente).
 * 2. La lista de detalles de todos sus préstamos individuales (DetallePrestamoFinanciero).
 * 3. La suma total de deuda acumulada de todos sus créditos.
 * 4. La suma total de pagos aprobados en toda la historia del cliente.
 * 5. El estado financiero general consolidado (PAGADO, PENDIENTE, EN_MORA, SIN_PRESTAMOS).
 */
public class ResumenEstadoCliente {

    // Referencia al objeto Cliente del sistema
    private final Cliente cliente;

    // Lista inmutable con el desglose de cada crédito del cliente
    private final List<DetallePrestamoFinanciero> prestamos;

    // Gran total adeudado por el cliente sumando todos sus créditos
    private final double deudaTotal;

    // Total de dinero pagado y aprobado acumulado
    private final double totalPagosAprobados;

    // Estado financiero global del cliente
    private final EstadoClienteFinanciero estadoGeneral;

    /**
     * Constructor consolidado.
     * Aplica buenas prácticas de programación defensiva:
     * - Collections.unmodifiableList para proteger la lista contra modificaciones externas.
     * - Math.max(0.0, ...) para evitar que la deuda sea negativa.
     */
    public ResumenEstadoCliente(Cliente cliente, List<DetallePrestamoFinanciero> prestamos,
                                double deudaTotal, double totalPagosAprobados,
                                EstadoClienteFinanciero estadoGeneral) {
        this.cliente = cliente;
        // Si la lista recibida es nula, asigna una lista vacía inmutable; si no, la envuelve como de solo lectura
        this.prestamos = (prestamos != null) ? Collections.unmodifiableList(prestamos) : Collections.emptyList();
        this.deudaTotal = Math.max(0.0, deudaTotal);
        this.totalPagosAprobados = totalPagosAprobados;
        this.estadoGeneral = estadoGeneral;
    }

    // ========================================================================
    // GETTERS
    // ========================================================================

    public Cliente getCliente() {
        return cliente;
    }

    public List<DetallePrestamoFinanciero> getPrestamos() {
        return prestamos;
    }

    public double getDeudaTotal() {
        return deudaTotal;
    }

    public double getTotalPagosAprobados() {
        return totalPagosAprobados;
    }

    public EstadoClienteFinanciero getEstadoGeneral() {
        return estadoGeneral;
    }

    /**
     * Construye un informe formateado en texto (factura / estado de cuenta)
     * listo para imprimir en la consola o exportar.
     *
     * Usa StringBuilder en lugar de concatenaciones sucesivas con '+'
     * para optimizar el rendimiento y el uso de memoria en la JVM.
     */
    public String reporteFormateado() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================\n");
        sb.append("         ESTADO FINANCIERO Y DEUDA DEL CLIENTE                      \n");
        sb.append("====================================================================\n");
        sb.append(String.format("Cliente:        %s (#%d)\n", cliente.getNombre(), cliente.getId()));
        sb.append(String.format("Documento:      %s\n", cliente.getDocumento()));
        sb.append(String.format("Teléfono:       %s\n", cliente.getTelefono()));
        sb.append(String.format("Correo:         %s\n", cliente.getCorreo()));
        sb.append("--------------------------------------------------------------------\n");
        sb.append(String.format("Estado General: %s\n", estadoGeneral));
        sb.append(String.format("Deuda Total:    %s\n", Formato.moneda(deudaTotal)));
        sb.append(String.format("Pagos Aprobados:%s\n", Formato.moneda(totalPagosAprobados)));
        sb.append("--------------------------------------------------------------------\n");

        if (prestamos.isEmpty()) {
            sb.append("  (El cliente no tiene préstamos registrados)\n");
        } else {
            sb.append("Detalle de Préstamos:\n");
            for (DetallePrestamoFinanciero dp : prestamos) {
                sb.append("  • ").append(dp.toString()).append("\n");
                // Si el cliente tiene pagos rechazados o pendientes, se muestra una nota aclaratoria
                if (dp.getPagosPendientesORechazados() > 0) {
                    sb.append(String.format("    [Nota: %s en pagos pendientes o rechazados no descontados]\n",
                            Formato.moneda(dp.getPagosPendientesORechazados())));
                }
            }
        }
        sb.append("====================================================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Cliente #%d (%s) | Estado: %s | Deuda: %s | Préstamos: %d",
                cliente.getId(), cliente.getNombre(), estadoGeneral, Formato.moneda(deudaTotal), prestamos.size());
    }
}
