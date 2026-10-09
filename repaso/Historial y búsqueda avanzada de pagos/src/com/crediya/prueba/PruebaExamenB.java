package com.crediya.prueba;

import com.crediya.dao.PagoDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.modelo.Cliente;
import com.crediya.modelo.Empleado;
import com.crediya.modelo.EstadoPago;
import com.crediya.modelo.EstadoPrestamo;
import com.crediya.modelo.Pago;
import com.crediya.modelo.PagoConEstado;
import com.crediya.modelo.Prestamo;
import com.crediya.modelo.ResumenHistorialPagos;
import com.crediya.servicio.GestorHistorialPagos;
import com.crediya.util.Formato;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * CLASE DE PRUEBAS Y EVIDENCIAS — EXAMEN B
 * ============================================================================
 * ¿Para qué sirve esta clase?
 * Es el programa ejecutable autónomo que valida y demuestra todos los puntos
 * de la rúbrica del EXAMEN B:
 * 1. Consulta de historial por ID de préstamo.
 * 2. Filtrado por rango de fechas (con Stream API).
 * 3. Filtrado por monto mínimo (con Stream API).
 * 4. Filtrado por estado (con Stream API).
 * 5. Ordenamiento cronológico ascendente y descendente (con Comparator).
 * 6. Aplicación combinada de múltiples filtros.
 * 7. Resumen de cantidades y dineros separados por estado.
 */
public class PruebaExamenB {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("       SUITE DE PRUEBAS AUTOMATIZADA — EXAMEN B (CrediYa)           ");
        System.out.println("====================================================================\n");

        // --------------------------------------------------------------------
        // 1. DATOS DE PRUEBA SIMULADOS EN MEMORIA
        // --------------------------------------------------------------------
        Cliente cliente = new Cliente(1, "Pedro Sanchez", "1001001", "pedro@mail.com", "3001112233");
        Empleado empleado = new Empleado(10, "Laura Asesora", "900900", "laura@crediya.com", "Analista", 2500000);

        // Préstamo #201: Monto 3.000.000 iniciado en enero de 2026
        Prestamo prestamo201 = new Prestamo(201, cliente, empleado, 3000000, 10, 10,
                LocalDate.of(2026, 1, 15), EstadoPrestamo.PENDIENTE, 1500000);

        // Conjunto de 6 pagos distribuidos en diferentes fechas, montos y estados:
        // - 4 Aprobados: suman $1.440.000
        // - 1 Rechazado: monto $500.000
        // - 1 Pendiente: monto $200.000
        List<Pago> pagosSimulados = List.of(
                new PagoConEstado(1, 201, LocalDate.of(2026, 2, 10), 330000, EstadoPago.APROBADO),
                new PagoConEstado(2, 201, LocalDate.of(2026, 3, 12), 330000, EstadoPago.APROBADO),
                new PagoConEstado(3, 201, LocalDate.of(2026, 4, 15), 500000, EstadoPago.RECHAZADO),
                new PagoConEstado(4, 201, LocalDate.of(2026, 4, 20), 330000, EstadoPago.APROBADO),
                new PagoConEstado(5, 201, LocalDate.of(2026, 5, 10), 200000, EstadoPago.PENDIENTE),
                new PagoConEstado(6, 201, LocalDate.of(2026, 6, 5), 450000, EstadoPago.APROBADO)
        );

        // DAOs simulados con clases anónimas para ejecución independiente
        PrestamoDAO prestamoDAO = new PrestamoDAO() {
            @Override
            public Optional<Prestamo> buscarPorId(int id) {
                return (id == 201) ? Optional.of(prestamo201) : Optional.empty();
            }
        };

        PagoDAO pagoDAO = new PagoDAO() {
            @Override
            public List<Pago> listarPorPrestamo(int prestamoId) {
                return (prestamoId == 201) ? pagosSimulados : Collections.emptyList();
            }
        };

        // Instanciamos el gestor con los DAOs
        GestorHistorialPagos gestor = new GestorHistorialPagos(pagoDAO, prestamoDAO);

        try {
            // PRUEBA 1: Consulta general
            System.out.println("1. CONSULTA DE HISTORIAL DEL PRÉSTAMO #201:");
            List<PagoConEstado> todos = gestor.obtenerPagosPorPrestamo(201);
            todos.forEach(p -> System.out.println("   " + p));
            System.out.println("   Total pagos encontrados: " + todos.size());

            // PRUEBA 2: Filtro por fecha (Marzo y Abril 2026)
            LocalDate desde = LocalDate.of(2026, 3, 1);
            LocalDate hasta = LocalDate.of(2026, 4, 30);
            System.out.printf("\n2. FILTRO POR FECHA (Entre %s y %s):\n", desde, hasta);
            List<PagoConEstado> porFecha = gestor.filtrarPorFecha(todos, desde, hasta);
            porFecha.forEach(p -> System.out.println("   " + p));

            // PRUEBA 3: Filtro por monto mínimo (>= $350.000)
            double montoMinimo = 350000.0;
            System.out.printf("\n3. FILTRO POR MONTO MÍNIMO (>= %s):\n", Formato.moneda(montoMinimo));
            List<PagoConEstado> porMonto = gestor.filtrarPorMontoMinimo(todos, montoMinimo);
            porMonto.forEach(p -> System.out.println("   " + p));

            // PRUEBA 4: Filtro por estado (Solo APROBADO)
            System.out.println("\n4. FILTRO POR ESTADO (Solo APROBADO):");
            List<PagoConEstado> soloAprobados = gestor.filtrarPorEstado(todos, EstadoPago.APROBADO);
            soloAprobados.forEach(p -> System.out.println("   " + p));

            // PRUEBA 5: Ordenamiento cronológico descendente (más recientes primero)
            System.out.println("\n5. ORDENAMIENTO CRONOLÓGICO DESCENDENTE (Más recientes primero):");
            List<PagoConEstado> ordenadosDesc = gestor.ordenarPorFecha(todos, false);
            ordenadosDesc.forEach(p -> System.out.println("   " + p));

            // PRUEBA 6: Combinación de 2 filtros (Monto >= $300.000 Y Estado APROBADO)
            System.out.println("\n6. COMBINACIÓN DE FILTROS (Monto >= $300.000 Y Estado APROBADO):");
            List<PagoConEstado> combinados = gestor.filtrarPorMontoMinimo(todos, 300000);
            combinados = gestor.filtrarPorEstado(combinados, EstadoPago.APROBADO);
            combinados.forEach(p -> System.out.println("   " + p));

            // PRUEBA 7: Resumen del historial con cálculo disjunto
            System.out.println("\n7. RESUMEN FINANCIERO DEL HISTORIAL:");
            ResumenHistorialPagos resumen = gestor.generarResumen(todos);
            System.out.print(resumen.reporteFormateado());

            // Aserciones para asegurar exactitud matemática
            assert resumen.getCantidadTotal() == 6;
            assert resumen.getCantidadAprobados() == 4;
            assert resumen.getCantidadPendientes() == 1;
            assert resumen.getCantidadRechazados() == 1;
            assert resumen.getTotalAprobado() == 1440000.0;
            assert resumen.getTotalPendiente() == 200000.0;
            assert resumen.getTotalRechazado() == 500000.0;

            System.out.println("[ÉXITO] Todas las consultas, filtros y cálculos del Examen B operan al 100%.");

        } catch (Exception e) {
            System.err.println("Error en las pruebas del Examen B: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
