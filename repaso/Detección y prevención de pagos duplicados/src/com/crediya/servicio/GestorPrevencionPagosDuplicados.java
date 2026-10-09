package com.crediya.servicio;

import com.crediya.dao.PagoDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.excepciones.CrediYaException;
import com.crediya.excepciones.DatosInvalidosException;
import com.crediya.excepciones.ErrorPersistenciaException;
import com.crediya.excepciones.PagoDuplicadoException;
import com.crediya.excepciones.PagoInvalidoException;
import com.crediya.excepciones.PrestamoNoEncontradoException;
import com.crediya.modelo.EstadoPrestamo;
import com.crediya.modelo.Pago;
import com.crediya.modelo.Prestamo;
import com.crediya.util.CalculadoraPrestamo;
import com.crediya.util.Formato;
import com.crediya.util.Validaciones;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * EXAMEN 3 — GESTOR DE DETECCIÓN Y PREVENCIÓN DE PAGOS DUPLICADOS
 * ============================================================================
 * Implementa la consulta JDBC a MySQL, validación funcional con Stream API
 * y expresiones lambda para detectar y prevenir registros de pagos repetidos.
 */
public class GestorPrevencionPagosDuplicados {

    private final PagoDAO pagoDAO;
    private final PrestamoDAO prestamoDAO;

    public GestorPrevencionPagosDuplicados() {
        this(new PagoDAO(), new PrestamoDAO());
    }

    public GestorPrevencionPagosDuplicados(PagoDAO pagoDAO, PrestamoDAO prestamoDAO) {
        this.pagoDAO = pagoDAO;
        this.prestamoDAO = prestamoDAO;
    }

    // ========================================================================
    // PUNTO 1. CONSULTAR PAGOS EXISTENTES
    // ========================================================================

    /**
     * Consulta los pagos registrados de un préstamo en MySQL mediante JDBC
     * y los almacena en un ArrayList.
     */
    public ArrayList<Pago> consultarPagosPorPrestamo(int prestamoId) throws CrediYaException {
        if (prestamoId <= 0) {
            throw new DatosInvalidosException("El ID del préstamo debe ser mayor que cero.");
        }
        // Validar que el préstamo existe en la base de datos
        prestamoDAO.buscarPorId(prestamoId)
                .orElseThrow(() -> new PrestamoNoEncontradoException("No existe un préstamo con ID " + prestamoId));

        try {
            List<Pago> lista = pagoDAO.listarPorPrestamo(prestamoId);
            ArrayList<Pago> resultado = new ArrayList<>(lista);
            if (resultado.isEmpty()) {
                System.out.println("El préstamo #" + prestamoId + " no tiene pagos registrados actualmente.");
            }
            return resultado;
        } catch (ErrorPersistenciaException e) {
            System.err.println("Error de conexión al consultar pagos: " + e.getMessage());
            throw e;
        }
    }

    // ========================================================================
    // PUNTO 2. DETECTAR POSIBLES DUPLICADOS (Stream API & Lambdas)
    // ========================================================================

    /**
     * Valida si un pago es duplicado comparándolo contra los pagos activos del préstamo.
     * Criterio: Coincidencia en prestamoId, fechaPago y monto.
     * Utiliza Stream API y expresiones lambda.
     */
    public void validarPagoDuplicado(Pago nuevoPago) throws CrediYaException {
        if (nuevoPago == null) {
            throw new DatosInvalidosException("El pago no puede ser nulo.");
        }

        // Obtener la colección de pagos existentes
        ArrayList<Pago> pagosExistentes = consultarPagosPorPrestamo(nuevoPago.getPrestamoId());

        // Detección funcional con Stream API y expresiones Lambda
        boolean esDuplicado = pagosExistentes.stream().anyMatch(existente ->
                existente.getPrestamoId() == nuevoPago.getPrestamoId()
                        && existente.getFechaPago().equals(nuevoPago.getFechaPago())
                        && Math.abs(existente.getMonto() - nuevoPago.getMonto()) < 0.001
        );

        if (esDuplicado) {
            throw new PagoDuplicadoException(
                    "Ya existe un pago activo con el mismo préstamo (" + nuevoPago.getPrestamoId()
                            + "), fecha (" + nuevoPago.getFechaPago()
                            + ") y monto (" + Formato.moneda(nuevoPago.getMonto()) + ")."
            );
        }
    }

    // ========================================================================
    // PUNTO 3. PREVENIR REGISTROS DUPLICADOS Y REGISTRO SEGURO
    // ========================================================================

    /**
     * Orquesta el proceso de registro seguro y atómico de un pago previniendo duplicados:
     * 1. Validar no nulo.
     * 2. Verificar existencia del préstamo.
     * 3. Comprobar monto y fecha.
     * 4. Consultar pagos existentes.
     * 5. Ejecutar validación de duplicados con Stream API.
     * 6. Guardar en MySQL mediante JDBC con actualización transaccional.
     * 7. Mostrar mensaje de confirmación o error.
     */
    public synchronized Pago registrarPagoSeguro(int prestamoId, double monto, LocalDate fecha) throws CrediYaException {
        System.out.println("\n=== REGISTRO DE PAGO ===");
        System.out.printf("Préstamo: %d%nFecha: %s%nMonto: %s%n%n", prestamoId, fecha, Formato.moneda(monto));

        // 1. Validar datos básicos
        if (prestamoId <= 0) {
            throw new DatosInvalidosException("El ID del préstamo debe ser mayor que cero.");
        }
        Validaciones.validarMontoPositivo(monto, "monto del pago");
        Validaciones.validarFechaPago(fecha);

        // 2. Verificar existencia del préstamo
        Prestamo prestamo = prestamoDAO.buscarPorId(prestamoId)
                .orElseThrow(() -> new PrestamoNoEncontradoException("No existe un préstamo con ID " + prestamoId));

        if (prestamo.getEstado() == EstadoPrestamo.PAGADO) {
            throw new PagoInvalidoException("El préstamo #" + prestamoId + " ya está completamente pagado.");
        }
        if (fecha.isBefore(prestamo.getFechaInicio())) {
            throw new PagoInvalidoException("La fecha del pago no puede ser anterior al inicio del crédito ("
                    + prestamo.getFechaInicio() + ").");
        }

        double montoRedondeado = CalculadoraPrestamo.redondear(monto);
        if (montoRedondeado > prestamo.getSaldoPendiente()) {
            throw new PagoInvalidoException("El monto a pagar (" + Formato.moneda(montoRedondeado)
                    + ") supera el saldo pendiente (" + Formato.moneda(prestamo.getSaldoPendiente()) + ").");
        }

        Pago pagoCandidato = new Pago(0, prestamoId, fecha, montoRedondeado);

        // 3 y 4. Consultar existentes y validar duplicados
        System.out.println("Validando información del pago...");
        System.out.println("Consultando pagos existentes...");
        try {
            validarPagoDuplicado(pagoCandidato);
            System.out.println("No se encontraron duplicados.");
        } catch (PagoDuplicadoException de) {
            System.out.println("Error: " + de.getMessage());
            System.out.println("No se registró el pago duplicado.");
            throw de;
        }

        // 5. Persistencia transaccional en MySQL
        double nuevoSaldo = CalculadoraPrestamo.redondear(prestamo.getSaldoPendiente() - montoRedondeado);
        EstadoPrestamo nuevoEstado = (nuevoSaldo <= 0) ? EstadoPrestamo.PAGADO : prestamo.getEstado();

        pagoDAO.registrarPagoYActualizarPrestamo(pagoCandidato, nuevoSaldo, nuevoEstado);
        prestamo.setSaldoPendiente(nuevoSaldo);
        prestamo.setEstado(nuevoEstado);

        System.out.println("Pago registrado correctamente. Nuevo saldo: " + Formato.moneda(nuevoSaldo));
        return pagoCandidato;
    }
}
