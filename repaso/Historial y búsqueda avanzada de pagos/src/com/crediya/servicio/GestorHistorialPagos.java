package com.crediya.servicio;

import com.crediya.dao.PagoDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.excepciones.CrediYaException;
import com.crediya.excepciones.DatosInvalidosException;
import com.crediya.excepciones.ErrorPersistenciaException;
import com.crediya.excepciones.PrestamoNoEncontradoException;
import com.crediya.modelo.EstadoPago;
import com.crediya.modelo.Pago;
import com.crediya.modelo.PagoConEstado;
import com.crediya.modelo.Prestamo;
import com.crediya.modelo.ResumenHistorialPagos;
import com.crediya.util.CalculadoraPrestamo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ============================================================================
 * CLASE DE NEGOCIO — GESTOR DE HISTORIAL Y BÚSQUEDA AVANZADA (EXAMEN B)
 * ============================================================================
 * ¿Para qué sirve esta clase?
 * Resuelve todos los puntos del EXAMEN B utilizando:
 * 1. Stream API de Java (stream(), filter(), sorted(), toList()).
 * 2. Expresiones Lambda: funciones anónimas concisas como 'p -> p.getMonto() >= monto'.
 * 3. Comparadores con 'Comparator.comparing(...)' para ordenamiento cronológico.
 * 4. Manejo de excepciones para parámetros inválidos (fechas invertidas, montos negativos).
 */
public class GestorHistorialPagos {

    // DAOs inyectados para acceder a la base de datos sin duplicar conexiones
    private final PagoDAO pagoDAO;
    private final PrestamoDAO prestamoDAO;

    /**
     * Constructor: Inyección de dependencias.
     * Recibe los DAOs ya existentes en CrediYa.
     */
    public GestorHistorialPagos(PagoDAO pagoDAO, PrestamoDAO prestamoDAO) {
        this.pagoDAO = pagoDAO;
        this.prestamoDAO = prestamoDAO;
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 2: obtenerPagosPorPrestamo(int prestamoId)
     * ========================================================================
     * Consulta y retorna la lista completa de pagos asociados al crédito indicado.
     *
     * Validaciones:
     * - Si el ID <= 0 -> lanza DatosInvalidosException.
     * - Si el préstamo no existe en la BD -> lanza PrestamoNoEncontradoException.
     *
     * @param prestamoId Identificador del préstamo
     * @return Lista de pagos adaptados con estado
     * @throws CrediYaException Si el préstamo no existe o hay error de persistencia
     */
    public List<PagoConEstado> obtenerPagosPorPrestamo(int prestamoId) throws CrediYaException {
        if (prestamoId <= 0) {
            throw new DatosInvalidosException("El ID del préstamo debe ser mayor a cero.");
        }

        // Valida existencia del préstamo utilizando PrestamoDAO
        prestamoDAO.buscarPorId(prestamoId)
                .orElseThrow(() -> new PrestamoNoEncontradoException("No existe un préstamo con ID " + prestamoId + "."));

        // Recupera los pagos originales desde MySQL
        List<Pago> pagos = pagoDAO.listarPorPrestamo(prestamoId);
        List<PagoConEstado> resultado = new ArrayList<>();

        // Adapta cada pago para garantizar que tenga un EstadoPago válido
        for (Pago p : pagos) {
            if (p instanceof PagoConEstado pce) {
                resultado.add(pce);
            } else {
                // Si el pago viene del modelo anterior sin estado, se asume APROBADO por defecto
                resultado.add(new PagoConEstado(p.getId(), p.getPrestamoId(), p.getFechaPago(), p.getMonto(), EstadoPago.APROBADO));
            }
        }

        return resultado;
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 3.1: filtrarPorFecha(...) con Stream API
     * ========================================================================
     * Filtra los pagos cuya fecha esté dentro del intervalo [desde, hasta] inclusive.
     *
     * Lógica de Stream:
     *   !p.getFechaPago().isBefore(desde) -> Equivale a fecha >= desde
     *   !p.getFechaPago().isAfter(hasta)  -> Equivale a fecha <= hasta
     *
     * @throws DatosInvalidosException Si alguna fecha es nula o si desde > hasta
     */
    public List<PagoConEstado> filtrarPorFecha(List<PagoConEstado> pagos, LocalDate desde, LocalDate hasta)
            throws DatosInvalidosException {
        if (pagos == null) {
            return List.of();
        }
        if (desde == null || hasta == null) {
            throw new DatosInvalidosException("Las fechas inicial y final son obligatorias para el filtro.");
        }
        // Validación de coherencia cronológica
        if (desde.isAfter(hasta)) {
            throw new DatosInvalidosException(String.format(
                    "La fecha inicial (%s) no puede ser posterior a la fecha final (%s).", desde, hasta));
        }

        // Filtrado declarativo con Stream API
        return pagos.stream()
                .filter(p -> !p.getFechaPago().isBefore(desde) && !p.getFechaPago().isAfter(hasta))
                .toList();
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 3.2: filtrarPorMontoMinimo(...) con Stream API
     * ========================================================================
     * Filtra los pagos cuyo valor en dinero sea mayor o igual al monto especificado.
     *
     * @throws DatosInvalidosException Si el monto es negativo
     */
    public List<PagoConEstado> filtrarPorMontoMinimo(List<PagoConEstado> pagos, double montoMinimo)
            throws DatosInvalidosException {
        if (pagos == null) {
            return List.of();
        }
        if (montoMinimo < 0) {
            throw new DatosInvalidosException("El monto mínimo no puede ser un valor negativo.");
        }

        return pagos.stream()
                .filter(p -> p.getMonto() >= montoMinimo)
                .toList();
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 3.3: filtrarPorEstado(...) con Stream API
     * ========================================================================
     * Filtra los pagos que coincidan exactamente con un estado (APROBADO, PENDIENTE o RECHAZADO).
     *
     * @throws DatosInvalidosException Si el estado es nulo
     */
    public List<PagoConEstado> filtrarPorEstado(List<PagoConEstado> pagos, EstadoPago estado)
            throws DatosInvalidosException {
        if (pagos == null) {
            return List.of();
        }
        if (estado == null) {
            throw new DatosInvalidosException("Debe especificar un estado de pago válido.");
        }

        return pagos.stream()
                .filter(p -> p.getEstado() == estado)
                .toList();
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 3.4: ordenarPorFecha(...) con Comparator y Stream API
     * ========================================================================
     * Ordena la lista de pagos cronológicamente por su fecha.
     *
     * @param ascendente true para ordenar del más antiguo al más reciente;
     *                   false para ordenar del más reciente al más antiguo.
     */
    public List<PagoConEstado> ordenarPorFecha(List<PagoConEstado> pagos, boolean ascendente) {
        if (pagos == null || pagos.isEmpty()) {
            return List.of();
        }

        // Comparator.comparing extrae la fecha del pago para comparar
        // thenComparingInt desempata por ID si dos pagos se hicieron el mismo día
        Comparator<PagoConEstado> comparador = Comparator.comparing(PagoConEstado::getFechaPago)
                .thenComparingInt(PagoConEstado::getId);

        // Si se pide descendente, invertimos el comparador con .reversed()
        if (!ascendente) {
            comparador = comparador.reversed();
        }

        return pagos.stream()
                .sorted(comparador)
                .toList();
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 4: generarResumen(...) con Cálculo Disjunto
     * ========================================================================
     * Calcula la cantidad y valor total de pagos discriminados por cada estado.
     *
     * REGLA ESTRICTA DE LA RÚBRICA:
     * "Los valores deben calcularse por separado, evitando sumar un mismo pago
     *  en varias categorías."
     *
     * Se utiliza un 'switch' exhaustivo con expresiones lambda de Java 14+
     * que asegura que cada pago cae en una y solo una rama del acumulador.
     */
    public ResumenHistorialPagos generarResumen(List<PagoConEstado> pagos) {
        if (pagos == null || pagos.isEmpty()) {
            return new ResumenHistorialPagos(0, 0, 0, 0, 0.0, 0.0, 0.0);
        }

        int totalAprobadosCount = 0;
        int totalPendientesCount = 0;
        int totalRechazadosCount = 0;

        double sumaAprobados = 0.0;
        double sumaPendientes = 0.0;
        double sumaRechazados = 0.0;

        for (PagoConEstado p : pagos) {
            switch (p.getEstado()) {
                case APROBADO -> {
                    totalAprobadosCount++;
                    sumaAprobados += p.getMonto();
                }
                case PENDIENTE -> {
                    totalPendientesCount++;
                    sumaPendientes += p.getMonto();
                }
                case RECHAZADO -> {
                    totalRechazadosCount++;
                    sumaRechazados += p.getMonto();
                }
            }
        }

        return new ResumenHistorialPagos(
                pagos.size(),
                totalAprobadosCount,
                totalPendientesCount,
                totalRechazadosCount,
                CalculadoraPrestamo.redondear(sumaAprobados),
                CalculadoraPrestamo.redondear(sumaPendientes),
                CalculadoraPrestamo.redondear(sumaRechazados)
        );
    }
}
