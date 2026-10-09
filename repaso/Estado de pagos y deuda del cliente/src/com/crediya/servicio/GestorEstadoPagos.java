package com.crediya.servicio;

import com.crediya.dao.ClienteDAO;
import com.crediya.dao.PagoDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.excepciones.ClienteNoEncontradoException;
import com.crediya.excepciones.CrediYaException;
import com.crediya.excepciones.DatosInvalidosException;
import com.crediya.excepciones.ErrorPersistenciaException;
import com.crediya.excepciones.PrestamoNoEncontradoException;
import com.crediya.modelo.Cliente;
import com.crediya.modelo.DetallePrestamoFinanciero;
import com.crediya.modelo.EstadoClienteFinanciero;
import com.crediya.modelo.EstadoPago;
import com.crediya.modelo.Pago;
import com.crediya.modelo.PagoConEstado;
import com.crediya.modelo.Prestamo;
import com.crediya.modelo.ResumenEstadoCliente;
import com.crediya.util.CalculadoraPrestamo;
import com.crediya.util.Validaciones;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * CLASE DE NEGOCIO — GESTOR DE ESTADOS FINANCIEROS Y DEUDA (EXAMEN A)
 * ============================================================================
 * ¿Para qué sirve esta clase?
 * Es el componente principal exigido en el EXAMEN A.
 * Se encuentra en el paquete 'com.crediya.servicio' (capa de lógica de negocio).
 *
 * PRINCIPIOS DE ARQUITECTURA APLICADOS:
 * 1. INYECCIÓN DE DEPENDENCIAS: Recibe los DAOs (ClienteDAO, PrestamoDAO, PagoDAO)
 *    por el constructor. No crea conexiones nuevas ni hace 'new ConexionBD()' directamente.
 * 2. SEPARACIÓN DE RESPONSABILIDADES (SRP): Esta clase NO imprime en pantalla
 *    ni ejecuta sentencias SQL; solo realiza cálculos de saldos, validaciones
 *    de reglas bancarias y determina estados financieros.
 * 3. MANEJO DE EXCEPCIONES: Lanza excepciones de negocio controladas
 *    (ClienteNoEncontradoException, PrestamoNoEncontradoException, DatosInvalidosException)
 *    para que la vista las capture limpiamente.
 */
public class GestorEstadoPagos {

    // Dependencias a la capa de acceso a datos (DAO)
    private final ClienteDAO clienteDAO;
    private final PrestamoDAO prestamoDAO;
    private final PagoDAO pagoDAO;

    /**
     * Constructor: Inyección de dependencias manual.
     * Recibe los objetos DAO ya instanciados desde Main o la prueba.
     */
    public GestorEstadoPagos(ClienteDAO clienteDAO, PrestamoDAO prestamoDAO, PagoDAO pagoDAO) {
        this.clienteDAO = clienteDAO;
        this.prestamoDAO = prestamoDAO;
        this.pagoDAO = pagoDAO;
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 2 DEL EXAMEN A: consultarEstadoCliente(int clienteId)
     * ========================================================================
     * Consulta todos los créditos del cliente y consolida su estado financiero.
     *
     * Pasos que ejecuta:
     * 1. Valida que el ID recibido sea mayor a cero.
     * 2. Busca al cliente en la base de datos (lanza ClienteNoEncontradoException si no existe).
     * 3. Recupera los préstamos del cliente reutilizando 'prestamoDAO.listar()'.
     * 4. Si el cliente no tiene préstamos, retorna de inmediato el estado SIN_PRESTAMOS.
     * 5. Si tiene préstamos, calcula el saldo de cada uno restando ÚNICAMENTE los pagos aprobados.
     * 6. Determina si el cliente está PAGADO, PENDIENTE o EN_MORA según las cuotas vencidas.
     *
     * @param clienteId ID del cliente a consultar
     * @return Objeto ResumenEstadoCliente con el informe completo
     * @throws CrediYaException Si el ID es inválido o el cliente no existe
     */
    public ResumenEstadoCliente consultarEstadoCliente(int clienteId) throws CrediYaException {
        // Paso 1: Validación básica de parámetros
        if (clienteId <= 0) {
            throw new DatosInvalidosException("El ID del cliente debe ser un número entero positivo.");
        }

        // Paso 2: Búsqueda del cliente usando Optional.orElseThrow(...)
        // Si el cliente no existe en la BD, lanza la excepción personalizada en una sola línea
        Cliente cliente = clienteDAO.buscarPorId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException("No existe un cliente con ID " + clienteId + "."));

        // Paso 3: Obtener todos los préstamos del cliente usando Stream API y filter
        List<Prestamo> prestamos = prestamoDAO.listar().stream()
                .filter(p -> p.getCliente().getId() == clienteId)
                .toList();

        // Paso 4: Caso especial del examen -> Cliente sin créditos
        if (prestamos.isEmpty()) {
            return new ResumenEstadoCliente(cliente, List.of(), 0.0, 0.0, EstadoClienteFinanciero.SIN_PRESTAMOS);
        }

        // Paso 5: Procesar cada préstamo para calcular su saldo real y cuotas vencidas
        List<DetallePrestamoFinanciero> detalles = new ArrayList<>();
        double deudaTotal = 0.0;
        double totalAprobadosGeneral = 0.0;

        for (Prestamo prestamo : prestamos) {
            // Consultamos los abonos realizados a este préstamo específico
            List<Pago> pagos = pagoDAO.listarPorPrestamo(prestamo.getId());

            double sumaAprobados = 0.0;
            double sumaNoAprobados = 0.0;

            // Recorremos los pagos para separar los APROBADOS de los PENDIENTES/RECHAZADOS
            for (Pago p : pagos) {
                // Comprobamos con 'instanceof' si el pago soporta estados (Java 16+ Pattern Matching)
                if (p instanceof PagoConEstado pConEstado) {
                    if (pConEstado.isAprobado()) {
                        sumaAprobados += p.getMonto(); // Solo los aprobados se suman para el descuento
                    } else {
                        sumaNoAprobados += p.getMonto(); // Los rechazados/pendientes se contabilizan aparte
                    }
                } else {
                    // Si es un Pago original clásico de CrediYa sin estado, se considera aprobado
                    sumaAprobados += p.getMonto();
                }
            }

            // Redondeo a 2 decimales para evitar residuos matemáticos de punto flotante
            sumaAprobados = CalculadoraPrestamo.redondear(sumaAprobados);
            sumaNoAprobados = CalculadoraPrestamo.redondear(sumaNoAprobados);

            // REQUERIMIENTO 3 DEL EXAMEN A:
            // "Debe calcular el saldo restando al valor total del préstamo únicamente los pagos aprobados."
            // "El saldo calculado no puede ser negativo."
            double saldoCalculado = Math.max(0.0,
                    CalculadoraPrestamo.redondear(prestamo.getMontoTotal() - sumaAprobados));

            // Evaluar vencimiento de cuotas:
            // Cada mes vencido exige una cuota mensual. Si lo pagado es menor a lo exigible, entra en mora.
            int cuotasVencidas = prestamo.getCuotasVencidas();
            double valorEsperado = prestamo.getValorEsperadoPagado();
            boolean enMora = saldoCalculado > 0 && (prestamo.estaEnMora() || sumaAprobados < valorEsperado - 0.005);

            // Determinar el estado individual de este préstamo
            EstadoClienteFinanciero estadoPrestamo;
            if (saldoCalculado <= 0.0) {
                estadoPrestamo = EstadoClienteFinanciero.PAGADO;
            } else if (enMora) {
                estadoPrestamo = EstadoClienteFinanciero.EN_MORA;
            } else {
                estadoPrestamo = EstadoClienteFinanciero.PENDIENTE;
            }

            // Creamos el DTO individual del préstamo
            DetallePrestamoFinanciero detalle = new DetallePrestamoFinanciero(
                    prestamo.getId(),
                    prestamo.getMontoTotal(),
                    sumaAprobados,
                    sumaNoAprobados,
                    saldoCalculado,
                    cuotasVencidas,
                    prestamo.getCuotaMensual(),
                    prestamo.getEstado(),
                    estadoPrestamo
            );

            detalles.add(detalle);
            deudaTotal += saldoCalculado;
            totalAprobadosGeneral += sumaAprobados;
        }

        deudaTotal = CalculadoraPrestamo.redondear(deudaTotal);
        totalAprobadosGeneral = CalculadoraPrestamo.redondear(totalAprobadosGeneral);

        // Paso 6: Determinar el estado consolidado general del cliente
        EstadoClienteFinanciero estadoGeneral = determinarEstadoGeneral(detalles);

        // Retornamos el DTO consolidado completo
        return new ResumenEstadoCliente(cliente, detalles, deudaTotal, totalAprobadosGeneral, estadoGeneral);
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 3 DEL EXAMEN A: calcularSaldoPendiente(int prestamoId)
     * ========================================================================
     * Calcula el saldo de un préstamo específico restando ÚNICAMENTE los pagos aprobados.
     *
     * Reglas aplicadas:
     * - Si el ID no existe, lanza PrestamoNoEncontradoException.
     * - Los pagos pendientes o rechazados NO se descuentan.
     * - El saldo final no puede ser negativo (Math.max(0.0, ...)).
     *
     * @param prestamoId ID del préstamo a calcular
     * @return Saldo pendiente numérico redondeado
     * @throws CrediYaException Si el préstamo no existe o el ID es inválido
     */
    public double calcularSaldoPendiente(int prestamoId) throws CrediYaException {
        // Validación de parámetro
        if (prestamoId <= 0) {
            throw new DatosInvalidosException("El ID del préstamo debe ser mayor a cero.");
        }

        // Búsqueda del préstamo en la base de datos
        Prestamo prestamo = prestamoDAO.buscarPorId(prestamoId)
                .orElseThrow(() -> new PrestamoNoEncontradoException("No existe un préstamo con ID " + prestamoId + "."));

        // Recuperar abonos de este préstamo
        List<Pago> pagos = pagoDAO.listarPorPrestamo(prestamoId);

        // Sumar únicamente los pagos aprobados
        double totalAprobado = 0.0;
        for (Pago p : pagos) {
            if (p instanceof PagoConEstado pce) {
                if (pce.isAprobado()) {
                    totalAprobado += pce.getMonto();
                }
            } else {
                totalAprobado += p.getMonto();
            }
        }

        totalAprobado = CalculadoraPrestamo.redondear(totalAprobado);
        double saldo = prestamo.getMontoTotal() - totalAprobado;

        // Regla: El saldo calculado no puede ser negativo
        return Math.max(0.0, CalculadoraPrestamo.redondear(saldo));
    }

    /**
     * Lógica de jerarquía para determinar el estado general del cliente:
     * 1. Prioridad Máxima: Si AL MENOS UNO de sus préstamos está en mora -> el cliente está EN_MORA.
     * 2. Si no hay mora pero AL MENOS UNO tiene saldo pendiente -> el cliente está PENDIENTE.
     * 3. Si ninguno tiene deuda pendiente -> el cliente está PAGADO.
     */
    private EstadoClienteFinanciero determinarEstadoGeneral(List<DetallePrestamoFinanciero> detalles) {
        if (detalles.isEmpty()) {
            return EstadoClienteFinanciero.SIN_PRESTAMOS;
        }

        // ¿Hay algún préstamo en mora?
        boolean hayMora = detalles.stream().anyMatch(d -> d.getEstadoCalculado() == EstadoClienteFinanciero.EN_MORA);
        if (hayMora) {
            return EstadoClienteFinanciero.EN_MORA;
        }

        // ¿Hay algún préstamo con saldo pendiente de pago?
        boolean hayPendiente = detalles.stream().anyMatch(d -> d.getEstadoCalculado() == EstadoClienteFinanciero.PENDIENTE);
        if (hayPendiente) {
            return EstadoClienteFinanciero.PENDIENTE;
        }

        // Si llegó hasta aquí, todos sus créditos están totalmente pagados (saldo 0)
        return EstadoClienteFinanciero.PAGADO;
    }
}
