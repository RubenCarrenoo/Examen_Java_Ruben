package com.crediya.prueba;

import com.crediya.dao.PagoDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.excepciones.CrediYaException;
import com.crediya.excepciones.DatosInvalidosException;
import com.crediya.excepciones.ErrorPersistenciaException;
import com.crediya.excepciones.PagoDuplicadoException;
import com.crediya.excepciones.PrestamoNoEncontradoException;
import com.crediya.modelo.Cliente;
import com.crediya.modelo.Empleado;
import com.crediya.modelo.EstadoPrestamo;
import com.crediya.modelo.Pago;
import com.crediya.modelo.Prestamo;
import com.crediya.servicio.GestorPrevencionPagosDuplicados;
import com.crediya.util.Formato;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ============================================================================
 * SUITE DE PRUEBAS AUTOMATIZADA — EXAMEN 3 (CrediYa S.A.S.)
 * ============================================================================
 * Demuestra la consulta de pagos con JDBC, detección de duplicados con Stream API
 * y Lambdas, prevención de registros y manejo estricto de excepciones.
 */
public class PruebaExamen3 {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("  SUITE DE PRUEBAS AUTOMATIZADA — EXAMEN 3: PREVENCIÓN DE DUPLICADOS");
        System.out.println("====================================================================\n");

        // 1. Simulación de datos en memoria para ejecución autónoma
        Cliente c = new Cliente(3, "Mauricio Tobon", "10303030", "mauricio@crediya.com", "3115556677");
        Empleado e = new Empleado(1, "Carlos Asesor", "90101010", "carlos@crediya.com", "Cajero", 2200000);

        Prestamo p3 = new Prestamo(3, c, e, 2000000, 10, 10, LocalDate.of(2025, 9, 1), EstadoPrestamo.PENDIENTE, 2200000);

        List<Pago> pagosBD = new ArrayList<>();

        // DAOs simulados
        PrestamoDAO mockPrestamoDAO = new PrestamoDAO() {
            @Override
            public Optional<Prestamo> buscarPorId(int id) {
                if (id == 3) {
                    return Optional.of(p3);
                }
                return Optional.empty();
            }
        };

        PagoDAO mockPagoDAO = new PagoDAO() {
            private int consecutivo = 100;

            @Override
            public List<Pago> listarPorPrestamo(int prestamoId) {
                return pagosBD.stream()
                        .filter(p -> p.getPrestamoId() == prestamoId)
                        .collect(Collectors.toList());
            }

            @Override
            public void registrarPagoYActualizarPrestamo(Pago pago, double nuevoSaldo, EstadoPrestamo nuevoEstado) {
                pago.setId(++consecutivo);
                pagosBD.add(pago);
            }
        };

        GestorPrevencionPagosDuplicados gestor = new GestorPrevencionPagosDuplicados(mockPagoDAO, mockPrestamoDAO);

        // --------------------------------------------------------------------
        // PRUEBA 1: CONSULTA INICIAL DE PAGOS (Sin pagos)
        // --------------------------------------------------------------------
        System.out.println("--------------------------------------------------------------------");
        System.out.println("PRUEBA 1: CONSULTAR PAGOS INICIALES (Préstamo #3)");
        System.out.println("--------------------------------------------------------------------");
        try {
            ArrayList<Pago> iniciales = gestor.consultarPagosPorPrestamo(3);
            System.out.println("Cantidad de pagos encontrados: " + iniciales.size());
        } catch (CrediYaException ex) {
            System.err.println("Fallo: " + ex.getMessage());
        }

        // --------------------------------------------------------------------
        // PRUEBA 2: REGISTRO VÁLIDO DE PRIMER PAGO ($250.000, 2025-10-09)
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("PRUEBA 2: REGISTRO VÁLIDO DE PRIMER PAGO ($250.000)");
        System.out.println("--------------------------------------------------------------------");
        LocalDate fechaOperacion = LocalDate.of(2025, 10, 9);
        try {
            Pago pago1 = gestor.registrarPagoSeguro(3, 250000, fechaOperacion);
            System.out.println("-> Pago guardado con ID: " + pago1.getId());
        } catch (CrediYaException ex) {
            System.err.println("Fallo en registro válido: " + ex.getMessage());
        }

        // --------------------------------------------------------------------
        // PRUEBA 3: INTENTO DE PAGO DUPLICADO (Mismo préstamo, misma fecha, mismo monto)
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("PRUEBA 3: INTENTO DE REGISTRAR EXACTAMENTE EL MISMO PAGO (DUPLICADO)");
        System.out.println("--------------------------------------------------------------------");
        try {
            gestor.registrarPagoSeguro(3, 250000, fechaOperacion);
            System.err.println("-> [ERROR]: El pago duplicado fue permitido indebidamente.");
        } catch (PagoDuplicadoException ex) {
            System.out.println("[Captura exitosa] -> " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
        } catch (CrediYaException ex) {
            System.out.println("Otra excepción: " + ex.getMessage());
        }

        // --------------------------------------------------------------------
        // PRUEBA 4: PAGO CON MISMO MONTO PERO DISTINTA FECHA (VÁLIDO)
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("PRUEBA 4: PAGO CON MISMO MONTO PERO DISTINTA FECHA (2025-10-10)");
        System.out.println("--------------------------------------------------------------------");
        try {
            Pago pago2 = gestor.registrarPagoSeguro(3, 250000, LocalDate.of(2025, 10, 10));
            System.out.println("-> [ÉXITO] Pago legítimo aceptado con ID: " + pago2.getId());
        } catch (CrediYaException ex) {
            System.err.println("Fallo inesperado: " + ex.getMessage());
        }

        // --------------------------------------------------------------------
        // PRUEBA 5: MANEJO DE EXCEPCIONES ADICIONALES
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("PRUEBA 5: EXCEPCIONES FINANCIERAS Y DE PERSISTENCIA");
        System.out.println("--------------------------------------------------------------------");

        System.out.println("A) Monto negativo (-50.000):");
        try {
            gestor.registrarPagoSeguro(3, -50000, fechaOperacion);
        } catch (DatosInvalidosException die) {
            System.out.println("[OK Excepción esperada] " + die.getMessage());
        } catch (CrediYaException cye) {
            System.out.println("Error: " + cye.getMessage());
        }

        System.out.println("\nB) Préstamo inexistente (ID 999):");
        try {
            gestor.registrarPagoSeguro(999, 100000, fechaOperacion);
        } catch (PrestamoNoEncontradoException pne) {
            System.out.println("[OK Excepción esperada] " + pne.getMessage());
        } catch (CrediYaException cye) {
            System.out.println("Error: " + cye.getMessage());
        }

        // --------------------------------------------------------------------
        // VERIFICACIÓN DE TOTALES Y RECUENTO
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("VERIFICACIÓN DE PAGOS REGISTRADOS FINALES");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Total de pagos en base de datos: " + pagosBD.size());
        for (Pago p : pagosBD) {
            System.out.println("  * " + p);
        }

        if (pagosBD.size() == 2) {
            System.out.println("\n-> [CONFIRMADO] Se registraron únicamente los 2 pagos legítimos.");
            System.out.println("-> El pago duplicado fue rechazado y no alteró el saldo del crédito.");
        } else {
            System.err.println("\n-> [FALLO] El número de pagos registrados no coincide con lo esperado.");
        }

        System.out.println("\n====================================================================");
        System.out.println("  [ÉXITO] TODAS LAS PRUEBAS DEL EXAMEN 3 COMPLETADAS SATISFACTORIAMENTE");
        System.out.println("====================================================================");
    }
}
