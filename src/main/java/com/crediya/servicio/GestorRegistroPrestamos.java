package com.crediya.servicio;

import com.crediya.dao.ClienteDAO;
import com.crediya.dao.EmpleadoDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.excepciones.CrediYaException;
import com.crediya.excepciones.ErrorPersistenciaException;
import com.crediya.modelo.Cliente;
import com.crediya.modelo.Empleado;
import com.crediya.modelo.EstadoPrestamo;
import com.crediya.modelo.Prestamo;
import com.crediya.util.CalculadoraPrestamo;
import com.crediya.util.Formato;
import java.time.LocalDate;

/**
 * ============================================================================
  — GESTOR DE REGISTRO SEGURO DE PRÉSTAMOS
 * ============================================================================
 * Orquesta el flujo de registro seguro:
 * 1. Recepción de datos.
 * 2. Validación integral previa con ValidadorPrestamos.
 * 3. Confirmación de existencia de Cliente y Empleado.
 * 4. Cálculo financiero de monto total y cuota mensual.
 * 5. Persistencia JDBC con control transaccional e integridad referencial.
 * 6. Reporte claro del resultado o mensaje de error correctivo.
 */
public class GestorRegistroPrestamos {

    private final PrestamoDAO prestamoDAO;
    private final ClienteDAO clienteDAO;
    private final EmpleadoDAO empleadoDAO;
    private final ValidadorPrestamos validador;

    public GestorRegistroPrestamos() {
        this(new PrestamoDAO(), new ClienteDAO(), new EmpleadoDAO());
    }

    public GestorRegistroPrestamos(PrestamoDAO prestamoDAO, ClienteDAO clienteDAO, EmpleadoDAO empleadoDAO) {
        this.prestamoDAO = prestamoDAO;
        this.clienteDAO = clienteDAO;
        this.empleadoDAO = empleadoDAO;
        this.validador = new ValidadorPrestamos(clienteDAO, empleadoDAO);
    }

    /**
     * Registra un nuevo préstamo garantizando validación total previa.
     * Si ocurre algún fallo de validación, la base de datos jamás es modificada.
     */
    public Prestamo registrarPrestamoSeguro(int clienteId, int empleadoId, double monto,
                                            double interes, int cuotas, LocalDate fechaInicio) throws CrediYaException {
        System.out.println("\n=== REGISTRO DE PRÉSTAMO ===");

        // 1. Obtener objetos de referencia
        Cliente clienteRef = clienteDAO.buscarPorId(clienteId).orElse(null);
        if (clienteRef == null) {
            clienteRef = new Cliente(clienteId, "", "", "", "");
        }

        Empleado empleadoRef = empleadoDAO.buscarPorId(empleadoId).orElse(null);
        if (empleadoRef == null) {
            empleadoRef = new Empleado(empleadoId, "", "", "", "", 0);
        }

        // 2. Crear instancia temporal para validación
        double totalCalculado = CalculadoraPrestamo.calcularMontoTotal(monto, interes);
        Prestamo prestamoCandidato = new Prestamo(
                0,
                clienteRef,
                empleadoRef,
                monto,
                interes,
                cuotas,
                fechaInicio,
                EstadoPrestamo.PENDIENTE,
                totalCalculado
        );

        // 3. Ejecutar validaciones obligatorias antes de cualquier inserción
        try {
            validador.validarPrestamo(prestamoCandidato);
            System.out.println("Cliente validado correctamente.");
            System.out.println("Empleado validado correctamente.");
            System.out.println("Datos del préstamo validados correctamente.");
        } catch (CrediYaException e) {
            System.out.println("\nError: " + e.getMessage());
            System.out.println("El préstamo no fue registrado.");
            throw e;
        }

        // 4. Confirmación de cálculos financieros
        double cuota = prestamoCandidato.getCuotaMensual();
        System.out.println("Monto total calculado: " + Formato.moneda(totalCalculado));
        System.out.println("Cuota mensual calculada: " + Formato.moneda(cuota));

        // 5. Persistencia segura mediante JDBC en MySQL
        try {
            prestamoDAO.guardar(prestamoCandidato);
            System.out.println("Préstamo registrado correctamente con ID: " + prestamoCandidato.getId());
            return prestamoCandidato;
        } catch (ErrorPersistenciaException e) {
            System.err.println("Falla crítica al persistir el préstamo: " + e.getMessage());
            throw e;
        }
    }

    public ValidadorPrestamos getValidador() {
        return validador;
    }
}
