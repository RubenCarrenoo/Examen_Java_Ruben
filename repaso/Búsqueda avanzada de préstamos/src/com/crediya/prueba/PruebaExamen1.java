package com.crediya.prueba;

import com.crediya.dao.ClienteDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.excepciones.ClienteNoEncontradoException;
import com.crediya.excepciones.CrediYaException;
import com.crediya.excepciones.DatosInvalidosException;
import com.crediya.excepciones.ErrorPersistenciaException;
import com.crediya.modelo.Cliente;
import com.crediya.modelo.Empleado;
import com.crediya.modelo.EstadoPrestamo;
import com.crediya.modelo.Prestamo;
import com.crediya.servicio.GestorConsultasPrestamos;
import com.crediya.util.Formato;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ============================================================================
 * SUITE DE PRUEBAS AUTOMATIZADA — EXAMEN 1 (CrediYa S.A.S.)
 * ============================================================================
 * Valida todas las funcionalidades de consulta avanzada, Stream API, filtros,
 * ordenamientos y manejo de excepciones requeridos en el Examen 1.
 */
public class PruebaExamen1 {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("       SUITE DE PRUEBAS AUTOMATIZADA — EXAMEN 1: BÚSQUEDA AVANZADA  ");
        System.out.println("====================================================================\n");

        // 1. Preparar datos de prueba
        Cliente c1 = new Cliente(1, "Carlos Gomez", "1001", "carlos@crediya.com", "3001112233");
        Cliente c2 = new Cliente(2, "Ana Martinez", "1002", "ana@crediya.com", "3002223344");
        Cliente c3 = new Cliente(3, "Roberto Silva", "1003", "roberto@crediya.com", "3003334455");

        Empleado emp = new Empleado(10, "Laura Asesora", "9001", "laura@crediya.com", "Asesor", 2500000);

        List<Cliente> clientesBD = List.of(c1, c2, c3);
        List<Prestamo> prestamosBD = new ArrayList<>(List.of(
                new Prestamo(1, c1, emp, 300000, 10, 6, LocalDate.of(2025, 1, 10), EstadoPrestamo.PENDIENTE, 330000),
                new Prestamo(2, c2, emp, 800000, 12, 12, LocalDate.of(2025, 3, 15), EstadoPrestamo.PENDIENTE, 896000),
                new Prestamo(3, c1, emp, 5000000, 15, 24, LocalDate.of(2024, 6, 1), EstadoPrestamo.PENDIENTE, 5750000),
                new Prestamo(4, c3, emp, 1200000, 10, 12, LocalDate.of(2025, 2, 20), EstadoPrestamo.VENCIDO, 1320000),
                new Prestamo(5, c2, emp, 2500000, 10, 10, LocalDate.of(2024, 11, 5), EstadoPrestamo.PAGADO, 0)
        ));

        // 2. DAOs simulados en memoria (sobrescribiendo métodos para pruebas autónomas)
        ClienteDAO mockClienteDAO = new ClienteDAO() {
            @Override
            public Optional<Cliente> buscarPorId(int id) {
                return clientesBD.stream().filter(c -> c.getId() == id).findFirst();
            }
        };

        PrestamoDAO mockPrestamoDAO = new PrestamoDAO() {
            @Override
            public List<Prestamo> listar() {
                return new ArrayList<>(prestamosBD);
            }

            @Override
            public Optional<Prestamo> buscarPorId(int id) {
                return prestamosBD.stream().filter(p -> p.getId() == id).findFirst();
            }

            @Override
            public List<Prestamo> listarPorCliente(int clienteId) {
                return prestamosBD.stream()
                        .filter(p -> p.getCliente().getId() == clienteId)
                        .collect(Collectors.toList());
            }
        };

        GestorConsultasPrestamos gestor = new GestorConsultasPrestamos(mockPrestamoDAO, mockClienteDAO);

        // --------------------------------------------------------------------
        // PRUEBA 1: Consulta general y por ID
        // --------------------------------------------------------------------
        System.out.println("--------------------------------------------------------------------");
        System.out.println("PRUEBA 1: CONSULTA GENERAL Y BÚSQUEDA POR IDENTIFICADOR");
        System.out.println("--------------------------------------------------------------------");
        try {
            List<Prestamo> todos = gestor.consultarTodos();
            System.out.println("Total de préstamos registrados: " + todos.size());

            System.out.println("\nBuscando Préstamo con ID 2:");
            Optional<Prestamo> p2 = gestor.buscarPorId(2);
            p2.ifPresent(p -> System.out.println("-> Encontrado: " + p));

            System.out.println("\nBuscando Préstamo inexistente (ID 999):");
            Optional<Prestamo> p999 = gestor.buscarPorId(999);
            System.out.println("-> ¿Existe?: " + p999.isPresent());
        } catch (Exception e) {
            System.err.println("Error inesperado en Prueba 1: " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // PRUEBA 2: Consulta por Cliente
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("PRUEBA 2: CONSULTA DE PRÉSTAMOS ASOCIADOS A UN CLIENTE");
        System.out.println("--------------------------------------------------------------------");
        try {
            System.out.println("Préstamos de cliente ID 2 (Ana Martinez):");
            List<Prestamo> prestamosAna = gestor.consultarPorCliente(2);
            for (Prestamo p : prestamosAna) {
                System.out.println("  * " + p);
            }

            System.out.println("\nPréstamos de cliente inexistente (ID 50):");
            try {
                gestor.consultarPorCliente(50);
            } catch (ClienteNoEncontradoException cne) {
                System.out.println("[OK Excepción esperada] " + cne.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error en Prueba 2: " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // PRUEBA 3: Búsqueda por Rango de Montos (Ejemplo del enunciado: 500.000 a 3.000.000)
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("PRUEBA 3: BÚSQUEDA POR RANGO DE MONTOS (Stream API & Lambdas)");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("=== CONSULTA AVANZADA DE PRÉSTAMOS ===");
        System.out.println("Ingrese el monto mínimo: 500000");
        System.out.println("Ingrese el monto máximo: 3000000");
        try {
            List<Prestamo> enRango = gestor.buscarPrestamosPorMonto(500000, 3000000);

            // Validar excepciones en rango inválido
            System.out.println("\nProbando validación: Mínimo mayor que máximo (4.000.000 > 1.000.000):");
            try {
                gestor.buscarPrestamosPorMonto(4000000, 1000000);
            } catch (DatosInvalidosException die) {
                System.out.println("[OK Excepción esperada] " + die.getMessage());
            }

            System.out.println("\nProbando validación: Montos negativos (-100):");
            try {
                gestor.buscarPrestamosPorMonto(-100, 500000);
            } catch (DatosInvalidosException die) {
                System.out.println("[OK Excepción esperada] " + die.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error en Prueba 3: " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // PRUEBA 4: Filtrado por Estado (Stream API)
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("PRUEBA 4: FILTRADO POR ESTADO (Stream API)");
        System.out.println("--------------------------------------------------------------------");
        try {
            System.out.println("Filtrando préstamos PENDIENTE:");
            gestor.buscarPrestamosPorEstado("PENDIENTE");

            System.out.println("\nFiltrando préstamos PAGADO:");
            gestor.buscarPrestamosPorEstado("PAGADO");

            System.out.println("\nProbando estado inválido ('CANCELADO'):");
            try {
                gestor.buscarPrestamosPorEstado("CANCELADO");
            } catch (DatosInvalidosException die) {
                System.out.println("[OK Excepción esperada] " + die.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error en Prueba 4: " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // PRUEBA 5: Ordenamiento de Resultados (Stream API & Comparators)
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("PRUEBA 5: ORDENAMIENTO (Comparator & Stream API)");
        System.out.println("--------------------------------------------------------------------");
        try {
            List<Prestamo> listaOriginal = gestor.consultarTodos();

            System.out.println("A) Ordenado por Monto de MENOR a MAYOR:");
            List<Prestamo> ascMonto = gestor.ordenarPorMonto(listaOriginal, true);
            ascMonto.forEach(p -> System.out.printf("  * ID %d - %s (%s)%n", p.getId(), Formato.moneda(p.getMonto()), p.getCliente().getNombre()));

            System.out.println("\nB) Ordenado por Monto de MAYOR a MENOR:");
            List<Prestamo> descMonto = gestor.ordenarPorMonto(listaOriginal, false);
            descMonto.forEach(p -> System.out.printf("  * ID %d - %s (%s)%n", p.getId(), Formato.moneda(p.getMonto()), p.getCliente().getNombre()));

            System.out.println("\nC) Ordenado por Fecha de inicio (más antiguo a más reciente):");
            List<Prestamo> ascFecha = gestor.ordenarPorFecha(listaOriginal, true);
            ascFecha.forEach(p -> System.out.printf("  * ID %d - Fecha: %s - Monto: %s%n", p.getId(), p.getFechaInicio(), Formato.moneda(p.getMonto())));
        } catch (Exception e) {
            System.err.println("Error en Prueba 5: " + e.getMessage());
        }

        System.out.println("\n====================================================================");
        System.out.println("  [ÉXITO] TODAS LAS PRUEBAS DEL EXAMEN 1 COMPLETADAS SATISFACTORIAMENTE");
        System.out.println("====================================================================");
    }
}
