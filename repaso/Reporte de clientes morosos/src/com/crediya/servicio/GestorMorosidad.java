package com.crediya.servicio;

import com.crediya.dao.ClienteDAO;
import com.crediya.dao.PagoDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.excepciones.CrediYaException;
import com.crediya.excepciones.DatosInvalidosException;
import com.crediya.excepciones.ErrorPersistenciaException;
import com.crediya.modelo.Cliente;
import com.crediya.modelo.DetalleClienteMoroso;
import com.crediya.modelo.EstadoPrestamo;
import com.crediya.modelo.Pago;
import com.crediya.modelo.Prestamo;
import com.crediya.util.CalculadoraPrestamo;
import com.crediya.util.Formato;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ============================================================================
 * CLASE DE NEGOCIO — GESTOR DE REPORTES DE MOROSIDAD (EXAMEN C)
 * ============================================================================
 * ¿Para qué sirve esta clase?
 * Resuelve todos los requerimientos del EXAMEN C:
 * 1. Identifica qué clientes tienen obligaciones vencidas e impagas.
 * 2. REGLA CLAVE DE LA RÚBRICA: No clasifica como moroso a un cliente únicamente
 *    por tener saldo pendiente si sus cuotas a la fecha están al día.
 * 3. Calcula la deuda total a recuperar por cliente.
 * 4. Aplica filtros avanzados con Stream API: sorted(), limit(), filter().
 * 5. Exporta los resultados a archivos físicos en disco (.csv y .txt).
 */
public class GestorMorosidad {

    // DAOs inyectados para acceder a la base de datos sin reconexiones innecesarias
    private final ClienteDAO clienteDAO;
    private final PrestamoDAO prestamoDAO;
    private final PagoDAO pagoDAO;

    /**
     * Constructor: Inyección de dependencias.
     */
    public GestorMorosidad(ClienteDAO clienteDAO, PrestamoDAO prestamoDAO, PagoDAO pagoDAO) {
        this.clienteDAO = clienteDAO;
        this.prestamoDAO = prestamoDAO;
        this.pagoDAO = pagoDAO;
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 2: obtenerClientesMorosos()
     * ========================================================================
     * Devuelve la lista única de clientes que tengan al menos una cuota vencida sin pagar.
     *
     * EXPLICACIÓN PARA EL EXAMEN:
     * - Un cliente con un préstamo de 10 millones que va en el mes 1 y ya pagó su cuota,
     *   TIENE DEUDA, pero NO ES MOROSO. Por eso se valida con 'prestamoEstaEnMora(p)'.
     * - Se utiliza 'Collectors.toMap' con LinkedHashMap para eliminar clientes duplicados
     *   si un mismo cliente tiene más de un crédito en mora.
     *
     * @return Lista de clientes morosos sin repeticiones
     * @throws ErrorPersistenciaException Si ocurre un error al consultar la BD
     */
    public List<Cliente> obtenerClientesMorosos() throws ErrorPersistenciaException {
        List<Prestamo> todosLosPrestamos = prestamoDAO.listar();

        // Filtra los préstamos que realmente tienen cuotas vencidas y agrupa por cliente
        Map<Integer, Cliente> morososUnicos = todosLosPrestamos.stream()
                .filter(this::prestamoEstaEnMora) // Referencia a método para evaluar la mora
                .map(Prestamo::getCliente)        // Transforma de Préstamo a Cliente
                .collect(Collectors.toMap(
                        Cliente::getId,
                        c -> c,
                        (existente, reemplazo) -> existente, // Si ya existe en el mapa, conserva el primero
                        LinkedHashMap::new                    // Mantiene el orden de inserción
                ));

        return new ArrayList<>(morososUnicos.values());
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 3: calcularDeudaCliente(int clienteId)
     * ========================================================================
     * Suma los saldos pendientes de todos los créditos del cliente,
     * restando los pagos válidos registrados.
     *
     * @param clienteId ID del cliente
     * @return Suma de deuda total redondeada
     */
    public double calcularDeudaCliente(int clienteId) throws ErrorPersistenciaException {
        if (clienteId <= 0) {
            return 0.0;
        }

        // Filtra los préstamos que pertenecen a este cliente específico
        List<Prestamo> prestamosCliente = prestamoDAO.listar().stream()
                .filter(p -> p.getCliente().getId() == clienteId)
                .toList();

        double deudaTotal = 0.0;
        for (Prestamo p : prestamosCliente) {
            List<Pago> pagos = pagoDAO.listarPorPrestamo(p.getId());
            // Suma los montos de los pagos realizados
            double totalPagado = pagos.stream().mapToDouble(Pago::getMonto).sum();
            // Saldo pendiente = Monto Total - Pagos (mínimo 0)
            double saldo = Math.max(0.0, p.getMontoTotal() - totalPagado);
            deudaTotal += saldo;
        }

        return CalculadoraPrestamo.redondear(deudaTotal);
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 5: obtenerDetalleMorosos()
     * ========================================================================
     * Genera la lista completa de objetos DetalleClienteMoroso con las 5 columnas:
     * Identificación, Nombre, Préstamos Pendientes, Deuda Total y Cuotas Vencidas.
     */
    public List<DetalleClienteMoroso> obtenerDetalleMorosos() throws ErrorPersistenciaException {
        List<Prestamo> todosPrestamos = prestamoDAO.listar();

        // Agrupa todos los préstamos por el ID del cliente utilizando Stream API
        Map<Integer, List<Prestamo>> prestamosPorCliente = todosPrestamos.stream()
                .collect(Collectors.groupingBy(p -> p.getCliente().getId()));

        List<DetalleClienteMoroso> resultado = new ArrayList<>();

        for (Map.Entry<Integer, List<Prestamo>> entry : prestamosPorCliente.entrySet()) {
            List<Prestamo> prestamos = entry.getValue();
            Cliente cliente = prestamos.get(0).getCliente();

            // REGLA FUNDAMENTAL: Solo procesamos clientes que tengan al menos 1 préstamo en mora
            boolean tieneMora = prestamos.stream().anyMatch(this::prestamoEstaEnMora);
            if (!tieneMora) {
                continue; // Si el cliente tiene préstamos al día, se ignora del reporte de morosidad
            }

            // Contar cuántos préstamos tiene con saldo pendiente (estado != PAGADO)
            int prestamosPendientes = (int) prestamos.stream()
                    .filter(p -> p.getEstado() != EstadoPrestamo.PAGADO)
                    .count();

            // Sumar las cuotas vencidas acumuladas de todos sus préstamos en mora
            int cuotasVencidas = prestamos.stream()
                    .filter(this::prestamoEstaEnMora)
                    .mapToInt(Prestamo::getCuotasVencidas)
                    .sum();

            // Calcular su deuda consolidada
            double deudaTotal = calcularDeudaCliente(cliente.getId());

            resultado.add(new DetalleClienteMoroso(
                    cliente.getId(),
                    cliente.getDocumento(),
                    cliente.getNombre(),
                    prestamosPendientes,
                    deudaTotal,
                    cuotasVencidas
            ));
        }

        return resultado;
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 4.1: filtrarPorDeudaMinima(...) con Stream API
     * ========================================================================
     * Filtra morosos cuya deuda supere o iguale el valor mínimo indicado.
     */
    public List<DetalleClienteMoroso> filtrarPorDeudaMinima(List<DetalleClienteMoroso> morosos, double valorMinimo)
            throws DatosInvalidosException {
        if (morosos == null) {
            return List.of();
        }
        if (valorMinimo < 0) {
            throw new DatosInvalidosException("El valor mínimo de deuda no puede ser negativo.");
        }

        return morosos.stream()
                .filter(m -> m.getDeudaTotal() >= valorMinimo)
                .toList();
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 4.2: ordenarMorososPorDeuda(...) con Stream API
     * ========================================================================
     * Ordena a los clientes morosos de MAYOR A MENOR deuda (orden descendente).
     */
    public List<DetalleClienteMoroso> ordenarMorososPorDeuda(List<DetalleClienteMoroso> morosos) {
        if (morosos == null || morosos.isEmpty()) {
            return List.of();
        }

        return morosos.stream()
                .sorted(Comparator.comparingDouble(DetalleClienteMoroso::getDeudaTotal).reversed())
                .toList();
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 4.3: obtenerTopClientesMorosos(...) con sorted y limit
     * ========================================================================
     * Obtiene los N clientes con mayor deuda morosa utilizando Stream API:
     *   .sorted(...) -> ordena de mayor a menor deuda
     *   .limit(limite) -> toma solo los primeros N elementos
     */
    public List<DetalleClienteMoroso> obtenerTopClientesMorosos(List<DetalleClienteMoroso> morosos, int limite)
            throws DatosInvalidosException {
        if (morosos == null || morosos.isEmpty()) {
            return List.of();
        }
        if (limite <= 0) {
            throw new DatosInvalidosException("El límite debe ser un número entero mayor a cero.");
        }

        return morosos.stream()
                .sorted(Comparator.comparingDouble(DetalleClienteMoroso::getDeudaTotal).reversed())
                .limit(limite)
                .toList();
    }

    /**
     * ========================================================================
     * REQUERIMIENTO 6: exportarReporte(...) a CSV o Texto Plano
     * ========================================================================
     * Escribe físicamente en el disco duro el informe generado.
     *
     * @param morosos     Lista de clientes a exportar
     * @param rutaArchivo Ruta física del archivo destino (Path)
     * @param formatoCsv  true para generar .csv (Excel); false para .txt (legible con tabla)
     * @throws ErrorPersistenciaException Si ocurre un fallo de E/S al escribir en disco
     */
    public void exportarReporte(List<DetalleClienteMoroso> morosos, Path rutaArchivo, boolean formatoCsv)
            throws ErrorPersistenciaException {
        if (morosos == null) {
            morosos = List.of();
        }

        List<String> lineas = new ArrayList<>();

        if (formatoCsv) {
            // Formato CSV delimitado por punto y coma ';'
            lineas.add("#documento;nombre;prestamos_pendientes;deuda_total;cuotas_vencidas");
            for (DetalleClienteMoroso m : morosos) {
                lineas.add(m.toCsvLine());
            }
        } else {
            // Formato de tabla con bordes y columnas alineadas
            lineas.add("=========================================================================================");
            lineas.add("                     REPORTE DE CLIENTES MOROSOS — CrediYa S.A.S.                         ");
            lineas.add("=========================================================================================");
            lineas.add(String.format("| %-12s | %-24s | %-12s | %-16s | %-15s |",
                    "DOCUMENTO", "NOMBRE CLIENTE", "PRÉSTAMOS P.", "DEUDA TOTAL", "CUOTAS VENCIDAS"));
            lineas.add("-----------------------------------------------------------------------------------------");
            for (DetalleClienteMoroso m : morosos) {
                lineas.add(m.toTabularLine());
            }
            lineas.add("=========================================================================================");
            // Suma total de cartera vencida con mapToDouble y sum
            double granTotal = morosos.stream().mapToDouble(DetalleClienteMoroso::getDeudaTotal).sum();
            lineas.add(String.format("Total Morosos: %d  |  Total Cartera Vencida: %s",
                    morosos.size(), Formato.moneda(granTotal)));
        }

        try {
            // Crea las carpetas contenedoras si no existen en disco
            if (rutaArchivo.getParent() != null) {
                Files.createDirectories(rutaArchivo.getParent());
            }
            // Escribe todas las líneas en disco en codificación UTF-8
            Files.write(rutaArchivo, lineas, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            // Envuelve la excepción técnica IOException en una excepción propia del negocio
            throw new ErrorPersistenciaException("Error al exportar reporte de morosidad: " + e.getMessage(), e);
        }
    }

    /**
     * Regla de negocio para determinar si un préstamo está en mora:
     * 1. Si fue marcado manualmente con estado VENCIDO -> Está en mora.
     * 2. Si ya fue PAGADO -> NO está en mora.
     * 3. Si tiene saldo pendiente y se cumplió la fecha de cuotas sin el abono suficiente -> Está en mora.
     */
    private boolean prestamoEstaEnMora(Prestamo p) {
        if (p.getEstado() == EstadoPrestamo.VENCIDO) {
            return true;
        }
        if (p.getEstado() == EstadoPrestamo.PAGADO) {
            return false;
        }
        return p.getSaldoPendiente() > 0 && p.estaEnMora();
    }
}
