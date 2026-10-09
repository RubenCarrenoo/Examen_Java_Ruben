package com.crediya.modelo;

import com.crediya.util.Formato;

/**
 * ============================================================================
 * CLASE DTO (Data Transfer Object) — DETALLE FINANCIERO DEL PRÉSTAMO
 * ============================================================================
 * ¿Para qué sirve esta clase?
 * Encapsula y transporta todos los datos financieros calculados de un solo
 * préstamo desde la capa de servicio (GestorEstadoPagos) hasta la vista (Consola).
 *
 * Conceptos clave aplicados aquí:
 * 1. ENCAPSULAMIENTO: Atributos privados accesibles únicamente mediante getters.
 * 2. INMUTABILIDAD: Todos los atributos son 'final', lo que significa que una
 *    vez creados en el constructor, nadie puede alterarlos por error.
 * 3. REGLA DEL EXAMEN A: Discrimina entre pagos aprobados (que descuentan deuda)
 *    y pagos pendientes/rechazados (que se informan pero NO descuentan deuda).
 */
public class DetallePrestamoFinanciero {

    // Identificador único del préstamo en la base de datos MySQL
    private final int prestamoId;

    // Monto total que el cliente debe pagar (Capital solicitado + Intereses calculados)
    private final double montoTotal;

    // Suma acumulada ÚNICAMENTE de los abonos válidos con estado APROBADO
    private final double pagosAprobados;

    // Suma de los pagos que están en proceso (PENDIENTE) o que rebotaron (RECHAZADO).
    // Nota: Por regla del examen, este dinero NO se descuenta del saldo.
    private final double pagosPendientesORechazados;

    // Deuda real actual = (montoTotal - pagosAprobados). Garantizado nunca negativo.
    private final double saldoPendiente;

    // Número de cuotas mensuales que ya vencieron según el calendario y no se han pagado
    private final int cuotasVencidas;

    // Valor en dinero que el cliente debe pagar cada mes
    private final double cuotaMensual;

    // Estado original almacenado en la tabla de la base de datos (PENDIENTE, PAGADO, VENCIDO)
    private final EstadoPrestamo estadoOriginal;

    // Estado financiero calculado dinámicamente según la regla de negocio
    // (PAGADO si saldo es 0, EN_MORA si debe cuotas vencidas, o PENDIENTE si va al día)
    private final EstadoClienteFinanciero estadoCalculado;

    /**
     * Constructor completo: inicializa y asegura la consistencia de los datos.
     *
     * @param prestamoId                   ID del préstamo
     * @param montoTotal                   Total a pagar (capital + intereses)
     * @param pagosAprobados               Total de pagos válidos
     * @param pagosPendientesORechazados   Total de pagos no válidos / en espera
     * @param saldoPendiente               Saldo pendiente calculado
     * @param cuotasVencidas               Cantidad de cuotas vencidas exigibles
     * @param cuotaMensual                 Monto de la cuota mensual
     * @param estadoOriginal               Estado que tenía en la BD
     * @param estadoCalculado              Estado evaluado por el gestor
     */
    public DetallePrestamoFinanciero(int prestamoId, double montoTotal, double pagosAprobados,
                                     double pagosPendientesORechazados, double saldoPendiente,
                                     int cuotasVencidas, double cuotaMensual,
                                     EstadoPrestamo estadoOriginal, EstadoClienteFinanciero estadoCalculado) {
        this.prestamoId = prestamoId;
        this.montoTotal = montoTotal;
        this.pagosAprobados = pagosAprobados;
        this.pagosPendientesORechazados = pagosPendientesORechazados;

        // REGLA DEL EXAMEN A: "El saldo calculado no puede ser negativo".
        // Math.max(0.0, ...) previene saldos como -0.01 por errores de centavos o sobrepagos.
        this.saldoPendiente = Math.max(0.0, saldoPendiente);

        this.cuotasVencidas = cuotasVencidas;
        this.cuotaMensual = cuotaMensual;
        this.estadoOriginal = estadoOriginal;
        this.estadoCalculado = estadoCalculado;
    }

    // ========================================================================
    // GETTERS (Métodos de sólo lectura para respetar el encapsulamiento)
    // ========================================================================

    /** Retorna el ID numérico del préstamo */
    public int getPrestamoId() {
        return prestamoId;
    }

    /** Retorna el monto total pactado (capital + interés) */
    public double getMontoTotal() {
        return montoTotal;
    }

    /** Retorna el dinero efectivamente abonado con pagos aprobados */
    public double getPagosAprobados() {
        return pagosAprobados;
    }

    /** Retorna el dinero en pagos pendientes o rechazados (no descontados) */
    public double getPagosPendientesORechazados() {
        return pagosPendientesORechazados;
    }

    /** Retorna la deuda restante actual que el cliente aún debe pagar */
    public double getSaldoPendiente() {
        return saldoPendiente;
    }

    /** Retorna cuántas cuotas mensuales ya vencieron y están sin pagar */
    public int getCuotasVencidas() {
        return cuotasVencidas;
    }

    /** Retorna el valor individual de una cuota al mes */
    public double getCuotaMensual() {
        return cuotaMensual;
    }

    /** Retorna el estado original guardado en MySQL */
    public EstadoPrestamo getEstadoOriginal() {
        return estadoOriginal;
    }

    /** Retorna el estado financiero calculado (PAGADO, PENDIENTE o EN_MORA) */
    public EstadoClienteFinanciero getEstadoCalculado() {
        return estadoCalculado;
    }

    /**
     * Representación en texto legible para consola o reportes.
     * Aplica la clase Formato.moneda(...) para mostrar símbolos de pesos y puntos de miles.
     */
    @Override
    public String toString() {
        return String.format("Préstamo #%d | Total: %s | Aprobado: %s | Saldo: %s | Cuotas Vencidas: %d | Estado: %s",
                prestamoId,
                Formato.moneda(montoTotal),
                Formato.moneda(pagosAprobados),
                Formato.moneda(saldoPendiente),
                cuotasVencidas,
                estadoCalculado);
    }
}
