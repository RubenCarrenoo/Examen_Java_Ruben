package com.crediya.servicio;

import com.crediya.dao.ClienteDAO;
import com.crediya.dao.EmpleadoDAO;
import com.crediya.excepciones.ClienteNoEncontradoException;
import com.crediya.excepciones.CrediYaException;
import com.crediya.excepciones.DatosInvalidosException;
import com.crediya.excepciones.EmpleadoNoEncontradoException;
import com.crediya.excepciones.ErrorPersistenciaException;
import com.crediya.modelo.Cliente;
import com.crediya.modelo.Empleado;
import com.crediya.modelo.Prestamo;
import com.crediya.util.Validaciones;

/**
 * ============================================================================
 * EXAMEN 2 — VALIDADOR DE PRÉSTAMOS
 * ============================================================================
 * Encargado de validar de forma estricta las reglas de negocio, integridad
 * referencial y datos financieros de un préstamo antes de su persistencia.
 */
public class ValidadorPrestamos {

    private final ClienteDAO clienteDAO;
    private final EmpleadoDAO empleadoDAO;

    public ValidadorPrestamos() {
        this(new ClienteDAO(), new EmpleadoDAO());
    }

    public ValidadorPrestamos(ClienteDAO clienteDAO, EmpleadoDAO empleadoDAO) {
        this.clienteDAO = clienteDAO;
        this.empleadoDAO = empleadoDAO;
    }

    /**
     * Valida integralmente el objeto préstamo según los requerimientos:
     * 1. Que no sea nulo.
     * 2. Que el cliente exista en la base de datos y esté habilitado.
     * 3. Que el empleado responsable exista en la base de datos.
     * 4. Que el monto sea mayor que cero.
     * 5. Que la tasa de interés sea válida (entre 0% y 100%).
     * 6. Que el número de cuotas sea mayor que cero (máx 120).
     * 7. Que la fecha de inicio no sea nula.
     */
    public void validarPrestamo(Prestamo prestamo) throws CrediYaException {
        // 1. Objeto préstamo no nulo
        if (prestamo == null) {
            throw new DatosInvalidosException("El préstamo no puede ser nulo.");
        }

        // 2. Validación de cliente existente
        if (prestamo.getCliente() == null || prestamo.getCliente().getId() <= 0) {
            throw new DatosInvalidosException("El préstamo debe tener asociado un cliente válido.");
        }
        int clienteId = prestamo.getCliente().getId();
        Cliente clienteBD = clienteDAO.buscarPorId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException("El cliente con ID " + clienteId + " no existe en la base de datos."));

        // Validar habilitación del cliente (si posee datos de contacto válidos)
        if (clienteBD.getNombre() == null || clienteBD.getNombre().isBlank()) {
            throw new DatosInvalidosException("El cliente con ID " + clienteId + " no está habilitado (datos incompletos).");
        }

        // 3. Validación de empleado responsable existente
        if (prestamo.getEmpleado() == null || prestamo.getEmpleado().getId() <= 0) {
            throw new DatosInvalidosException("El préstamo debe tener asociado un empleado responsable.");
        }
        int empleadoId = prestamo.getEmpleado().getId();
        empleadoDAO.buscarPorId(empleadoId)
                .orElseThrow(() -> new EmpleadoNoEncontradoException("El empleado con ID " + empleadoId + " no existe en la base de datos."));

        // 4. Monto mayor que cero
        Validaciones.validarMontoPositivo(prestamo.getMonto(), "monto del préstamo");

        // 5. Tasa de interés válida
        Validaciones.validarInteres(prestamo.getInteres());

        // 6. Número de cuotas mayor que cero
        Validaciones.validarCuotas(prestamo.getCuotas());

        // 7. Fecha de inicio válida
        if (prestamo.getFechaInicio() == null) {
            throw new DatosInvalidosException("La fecha de inicio del préstamo es obligatoria.");
        }
    }
}
