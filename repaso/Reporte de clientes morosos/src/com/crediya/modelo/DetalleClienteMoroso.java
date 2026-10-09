package com.crediya.modelo;

import com.crediya.util.Formato;

/**
 * ============================================================================
 * CLASE DTO — DETALLE DEL CLIENTE MOROSO (EXAMEN C)
 * ============================================================================
 * ¿Para qué sirve esta clase?
 * Modela la estructura exigida en el REQUERIMIENTO 5 DEL EXAMEN C:
 * "Mostrar identificación, nombre, cantidad de préstamos pendientes,
 *  deuda total y número de cuotas vencidas."
 *
 * Además, incluye los métodos de formateo para la exportación a disco
 * exigida en el REQUERIMIENTO 6 (archivos CSV y TXT).
 */
public class DetalleClienteMoroso {

    // ID interno del cliente en la base de datos
    private final int clienteId;

    // Número de cédula o documento de identidad del cliente
    private final String documento;

    // Nombre completo del cliente
    private final String nombre;

    // Cantidad de préstamos que el cliente tiene activos y sin pagar totalmente
    private final int prestamosPendientes;

    // Dinero total adeudado a CrediYa sumando todos sus préstamos
    private final double deudaTotal;

    // Suma total de cuotas mensuales vencidas que el cliente debe a la fecha
    private final int cuotasVencidas;

    /**
     * Constructor con inicialización y protección de valores no negativos.
     */
    public DetalleClienteMoroso(int clienteId, String documento, String nombre,
                                int prestamosPendientes, double deudaTotal, int cuotasVencidas) {
        this.clienteId = clienteId;
        this.documento = documento;
        this.nombre = nombre;
        this.prestamosPendientes = prestamosPendientes;
        this.deudaTotal = Math.max(0.0, deudaTotal);
        this.cuotasVencidas = cuotasVencidas;
    }

    // ========================================================================
    // GETTERS
    // ========================================================================

    public int getClienteId() {
        return clienteId;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrestamosPendientes() {
        return prestamosPendientes;
    }

    public double getDeudaTotal() {
        return deudaTotal;
    }

    public int getCuotasVencidas() {
        return cuotasVencidas;
    }

    /**
     * Formato para exportación a archivo CSV (valores separados por punto y coma ';').
     * Compatible con Microsoft Excel y LibreOffice Calc.
     */
    public String toCsvLine() {
        return String.format("%s;%s;%d;%.2f;%d",
                documento, nombre.replace(";", ""), prestamosPendientes, deudaTotal, cuotasVencidas);
    }

    /**
     * Formato para exportación a archivo de texto legible (.txt) o consola,
     * organizando los campos en columnas con anchos fijos alineados mediante barras '|'.
     */
    public String toTabularLine() {
        return String.format("| %-12s | %-24s | %-12d | %-16s | %-15d |",
                documento, nombre, prestamosPendientes, Formato.moneda(deudaTotal), cuotasVencidas);
    }

    @Override
    public String toString() {
        return String.format("Doc: %s | %s | Préstamos: %d | Deuda: %s | Cuotas Vencidas: %d",
                documento, nombre, prestamosPendientes, Formato.moneda(deudaTotal), cuotasVencidas);
    }
}
