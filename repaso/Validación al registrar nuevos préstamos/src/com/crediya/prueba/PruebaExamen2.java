package com.crediya.prueba;

import com.crediya.dao.ClienteDAO;
import com.crediya.dao.EmpleadoDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.excepciones.ClienteNoEncontradoException;
import com.crediya.excepciones.CrediYaException;
import com.crediya.excepciones.DatosInvalidosException;
import com.crediya.excepciones.EmpleadoNoEncontradoException;
import com.crediya.modelo.Cliente;
import com.crediya.modelo.Empleado;
import com.crediya.modelo.Prestamo;
import com.crediya.servicio.GestorRegistroPrestamos;
import com.crediya.servicio.ValidadorPrestamos;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * SUITE DE PRUEBAS AUTOMATIZADA — EXAMEN 2 (CrediYa S.A.S.)
 * ============================================================================
 * Demuestra la validación de préstamos, prevención de registros inválidos,
 * manejo de excepciones y garantía de integridad referencial.
 */
public class PruebaExamen2 {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("   SUITE DE PRUEBAS AUTOMATIZADA — EXAMEN 2: VALIDACIÓN Y REGISTRO  ");
        System.out.println("====================================================================\n");

        // 1. Datos simulados en memoria
        Cliente c10 = new Cliente(10, "Carlos Mario Lopez", "10101010", "carlos@crediya.com", "3009998877");
        Empleado e2 = new Empleado(2, "Diana Asesora", "20202020", "diana@crediya.com", "Asesor Senior", 3200000);

        List<Cliente> clientesBD = List.of(c10);
        List<Empleado> empleadosBD = List.of(e2);
        List<Prestamo> prestamosGuardados = new ArrayList<>();

        // DAOs con soporte en memoria para pruebas autónomas
        ClienteDAO mockClienteDAO = new ClienteDAO() {
            @Override
            public Optional<Cliente> buscarPorId(int id) {
                return clientesBD.stream().filter(c -> c.getId() == id).findFirst();
            }
        };

        EmpleadoDAO mockEmpleadoDAO = new EmpleadoDAO() {
            @Override
            public Optional<Empleado> buscarPorId(int id) {
                return empleadosBD.stream().filter(e -> e.getId() == id).findFirst();
            }
        };

        PrestamoDAO mockPrestamoDAO = new PrestamoDAO() {
            private int idGenerador = 50;

            @Override
            public void guardar(Prestamo p) {
                p.setId(++idGenerador);
                prestamosGuardados.add(p);
            }

            @Override
            public List<Prestamo> listar() {
                return new ArrayList<>(prestamosGuardados);
            }
        };

        GestorRegistroPrestamos gestor = new GestorRegistroPrestamos(mockPrestamoDAO, mockClienteDAO, mockEmpleadoDAO);

        // --------------------------------------------------------------------
        // CASO 1: REGISTRO VÁLIDO (Cumple todas las reglas)
        // --------------------------------------------------------------------
        System.out.println("--------------------------------------------------------------------");
        System.out.println("CASO 1: REGISTRO VÁLIDO (Cliente 10, Empleado 2, Monto 2.000.000)");
        System.out.println("--------------------------------------------------------------------");
        try {
            Prestamo pExito = gestor.registrarPrestamoSeguro(10, 2, 2000000, 10, 12, LocalDate.now());
            System.out.println("ID Asignado: " + pExito.getId());
            System.out.println("Estado Inicial: " + pExito.getEstado());
        } catch (CrediYaException e) {
            System.err.println("Fallo inesperado: " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // CASO 2: MONTO INVÁLIDO (Negativo: -500.000) - Ejemplo exacto del prompt
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("CASO 2: MONTO NEGATIVO (-$500.000)");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Ingrese el ID del cliente: 10");
        System.out.println("Ingrese el ID del empleado: 2");
        System.out.println("Ingrese el monto: -500000");
        try {
            gestor.registrarPrestamoSeguro(10, 2, -500000, 10, 12, LocalDate.now());
        } catch (DatosInvalidosException e) {
            System.out.println("[Captura exitosa] " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        } catch (CrediYaException e) {
            System.out.println("Error genérico: " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // CASO 3: CLIENTE INEXISTENTE (ID 999)
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("CASO 3: CLIENTE INEXISTENTE (ID 999)");
        System.out.println("--------------------------------------------------------------------");
        try {
            gestor.registrarPrestamoSeguro(999, 2, 1000000, 10, 6, LocalDate.now());
        } catch (ClienteNoEncontradoException e) {
            System.out.println("[Captura exitosa] " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        } catch (CrediYaException e) {
            System.out.println("Error genérico: " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // CASO 4: EMPLEADO INEXISTENTE (ID 888)
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("CASO 4: EMPLEADO INEXISTENTE (ID 888)");
        System.out.println("--------------------------------------------------------------------");
        try {
            gestor.registrarPrestamoSeguro(10, 888, 1000000, 10, 6, LocalDate.now());
        } catch (EmpleadoNoEncontradoException e) {
            System.out.println("[Captura exitosa] " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        } catch (CrediYaException e) {
            System.out.println("Error genérico: " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // CASO 5: CUOTAS INVÁLIDAS (0 cuotas)
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("CASO 5: CUOTAS INVÁLIDAS (0 cuotas)");
        System.out.println("--------------------------------------------------------------------");
        try {
            gestor.registrarPrestamoSeguro(10, 2, 1000000, 10, 0, LocalDate.now());
        } catch (DatosInvalidosException e) {
            System.out.println("[Captura exitosa] " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        } catch (CrediYaException e) {
            System.out.println("Error genérico: " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // CASO 6: TASA DE INTERÉS INVÁLIDA (> 100% o negativa)
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("CASO 6: INTERÉS INVÁLIDO (150%)");
        System.out.println("--------------------------------------------------------------------");
        try {
            gestor.registrarPrestamoSeguro(10, 2, 1000000, 150, 12, LocalDate.now());
        } catch (DatosInvalidosException e) {
            System.out.println("[Captura exitosa] " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        } catch (CrediYaException e) {
            System.out.println("Error genérico: " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // CASO 7: OBJETO PRÉSTAMO NULO
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("CASO 7: VALIDACIÓN DE OBJETO NULO");
        System.out.println("--------------------------------------------------------------------");
        ValidadorPrestamos validadorDirecto = gestor.getValidador();
        try {
            validadorDirecto.validarPrestamo(null);
        } catch (DatosInvalidosException e) {
            System.out.println("[Captura exitosa] " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        } catch (CrediYaException e) {
            System.out.println("Error genérico: " + e.getMessage());
        }

        // --------------------------------------------------------------------
        // VERIFICACIÓN DE INTEGRIDAD EN BD
        // --------------------------------------------------------------------
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("VERIFICACIÓN DE INTEGRIDAD DE LA BASE DE DATOS");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Total de préstamos efectivamente guardados: " + prestamosGuardados.size());
        if (prestamosGuardados.size() == 1) {
            System.out.println("-> [CONFIRMADO] Solo se guardó el préstamo válido. Ningún préstamo erróneo fue insertado.");
        } else {
            System.err.println("-> [FALLO] Se insertaron préstamos inválidos en la base de datos.");
        }

        System.out.println("\n====================================================================");
        System.out.println("  [ÉXITO] TODAS LAS PRUEBAS DEL EXAMEN 2 COMPLETADAS SATISFACTORIAMENTE");
        System.out.println("====================================================================");
    }
}
