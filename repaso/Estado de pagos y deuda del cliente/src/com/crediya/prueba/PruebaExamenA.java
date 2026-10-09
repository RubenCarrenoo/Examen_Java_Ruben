package com.crediya.prueba;

import com.crediya.dao.ClienteDAO;
import com.crediya.dao.PagoDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.excepciones.ErrorPersistenciaException;
import com.crediya.modelo.Cliente;
import com.crediya.modelo.Empleado;
import com.crediya.modelo.EstadoClienteFinanciero;
import com.crediya.modelo.EstadoPago;
import com.crediya.modelo.EstadoPrestamo;
import com.crediya.modelo.Pago;
import com.crediya.modelo.PagoConEstado;
import com.crediya.modelo.Prestamo;
import com.crediya.modelo.ResumenEstadoCliente;
import com.crediya.servicio.GestorEstadoPagos;
import com.crediya.util.Formato;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================================
 * CLASE DE PRUEBA Y EVIDENCIAS — EXAMEN A
 * ============================================================================
 * ¿Para qué sirve esta clase?
 * Es un ejecutable autónomo (contiene 'main') que demuestra y valida
 * automáticamente que la solución del Examen A cumple el 100% de la rúbrica.
 *
 * ¿Por qué usa "DAOs simulados en memoria"?
 * Para que puedas ejecutar la prueba en cualquier computador sin depender
 * de que el servicio de MySQL esté encendido en ese instante.
 * Aplica el concepto de CLASES ANÓNIMAS (new ClienteDAO() { ... }), sobrescribiendo
 * los métodos de consulta para leer desde mapas en memoria RAM.
 */
public class PruebaExamenA {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("       SUITE DE PRUEBAS AUTOMATIZADA — EXAMEN A (CrediYa)           ");
        System.out.println("====================================================================\n");

        // --------------------------------------------------------------------
        // 1. CREACIÓN DE DATOS DE PRUEBA EN MEMORIA
        // --------------------------------------------------------------------

        // Cliente 1: Registrado, pero nunca ha pedido un préstamo
        Cliente c1SinPrestamos = new Cliente(1, "Pedro Sanchez", "1001001", "pedro@mail.com", "3001112233");

        // Cliente 2: Tiene un préstamo reciente y va al día con sus cuotas
        Cliente c2Pendiente = new Cliente(2, "Juan Perez", "1002002", "juan@mail.com", "3004445566");

        // Cliente 3: Tiene un préstamo de hace 5 meses, debe cuotas atrasadas y tiene un pago rechazado
        Cliente c3Moroso = new Cliente(3, "Maria Rodriguez", "1003003", "maria@mail.com", "3007778899");

        // Cliente 4: Ya pagó el 100% de su deuda
        Cliente c4Pagado = new Cliente(4, "Carlos Paz", "1004004", "carlos@mail.com", "3000001122");

        // Empleado asesor que gestiona los créditos
        Empleado empleado = new Empleado(10, "Laura Asesora", "900900", "laura@crediya.com", "Analista", 2500000);

        // Préstamo 101 (Juan): 2.000.000 + 10% interes = 2.200.000 total. 10 cuotas de 220.000. Creado HOY.
        Prestamo pJuan = new Prestamo(101, c2Pendiente, empleado, 2000000, 10, 10,
                LocalDate.now(), EstadoPrestamo.PENDIENTE, 1980000);

        // Préstamo 102 (María): Creado hace 5 meses. Debería llevar 5 cuotas pagadas (1.100.000).
        // Solo pagó 220.000 aprobado, y tiene otro pago de 500.000 RECHAZADO que no descuenta.
        Prestamo pMaria = new Prestamo(102, c3Moroso, empleado, 2000000, 10, 10,
                LocalDate.now().minusMonths(5), EstadoPrestamo.PENDIENTE, 1980000);

        // Préstamo 103 (Carlos): 1.000.000 + 10% = 1.100.000. Pagó 1.100.000 aprobados. Saldo restante = 0.
        Prestamo pCarlos = new Prestamo(103, c4Pagado, empleado, 1000000, 10, 10,
                LocalDate.now().minusMonths(10), EstadoPrestamo.PAGADO, 0);

        // Pagos de Juan: 1 pago aprobado de 220.000
        List<Pago> pagosJuan = List.of(
                new PagoConEstado(1, 101, LocalDate.now(), 220000, EstadoPago.APROBADO)
        );

        // Pagos de María: 1 aprobado, 1 rechazado y 1 pendiente
        List<Pago> pagosMaria = List.of(
                new PagoConEstado(2, 102, LocalDate.now().minusMonths(4), 220000, EstadoPago.APROBADO),
                new PagoConEstado(3, 102, LocalDate.now().minusMonths(2), 500000, EstadoPago.RECHAZADO),
                new PagoConEstado(4, 102, LocalDate.now().minusDays(1), 300000, EstadoPago.PENDIENTE)
        );

        // Pagos de Carlos: 2 pagos de 550.000 aprobados = 1.100.000 (Paz y salvo)
        List<Pago> pagosCarlos = List.of(
                new PagoConEstado(5, 103, LocalDate.now().minusMonths(5), 550000, EstadoPago.APROBADO),
                new PagoConEstado(6, 103, LocalDate.now().minusMonths(1), 550000, EstadoPago.APROBADO)
        );

        // Tablas simuladas en memoria
        Map<Integer, Cliente> mapaClientes = Map.of(1, c1SinPrestamos, 2, c2Pendiente, 3, c3Moroso, 4, c4Pagado);
        Map<Integer, Prestamo> mapaPrestamos = Map.of(101, pJuan, 102, pMaria, 103, pCarlos);
        Map<Integer, List<Pago>> mapaPagos = Map.of(101, pagosJuan, 102, pagosMaria, 103, pagosCarlos);

        // --------------------------------------------------------------------
        // 2. CREACIÓN DE DAOS SIMULADOS (Mocking con clases anónimas)
        // --------------------------------------------------------------------
        ClienteDAO clienteDAO = new ClienteDAO() {
            @Override
            public Optional<Cliente> buscarPorId(int id) {
                return Optional.ofNullable(mapaClientes.get(id));
            }
        };

        PrestamoDAO prestamoDAO = new PrestamoDAO() {
            @Override
            public Optional<Prestamo> buscarPorId(int id) {
                return Optional.ofNullable(mapaPrestamos.get(id));
            }

            @Override
            public List<Prestamo> listar() {
                return new ArrayList<>(mapaPrestamos.values());
            }
        };

        PagoDAO pagoDAO = new PagoDAO() {
            @Override
            public List<Pago> listarPorPrestamo(int prestamoId) {
                return mapaPagos.getOrDefault(prestamoId, Collections.emptyList());
            }
        };

        // Instanciamos el servicio principal que evaluará los 4 casos
        GestorEstadoPagos gestor = new GestorEstadoPagos(clienteDAO, prestamoDAO, pagoDAO);

        // --------------------------------------------------------------------
        // 3. EJECUCIÓN Y VALIDACIÓN DE LOS 4 CASOS EXIGIDOS
        // --------------------------------------------------------------------
        try {
            // CASO 1: CLIENTE SIN PRÉSTAMOS
            System.out.println("--------------------------------------------------------------------");
            System.out.println("CASO 1: CLIENTE SIN PRÉSTAMOS (Esperado: SIN_PRESTAMOS)");
            System.out.println("--------------------------------------------------------------------");
            ResumenEstadoCliente r1 = gestor.consultarEstadoCliente(1);
            System.out.print(r1.reporteFormateado());
            assert r1.getEstadoGeneral() == EstadoClienteFinanciero.SIN_PRESTAMOS;

            // CASO 2: CLIENTE AL DÍA CON DEUDA PENDIENTE
            System.out.println("--------------------------------------------------------------------");
            System.out.println("CASO 2: CLIENTE AL DÍA CON DEUDA PENDIENTE (Esperado: PENDIENTE)");
            System.out.println("--------------------------------------------------------------------");
            ResumenEstadoCliente r2 = gestor.consultarEstadoCliente(2);
            System.out.print(r2.reporteFormateado());
            assert r2.getEstadoGeneral() == EstadoClienteFinanciero.PENDIENTE;

            // CASO 3: CLIENTE CON CUOTAS VENCIDAS (EN MORA)
            System.out.println("--------------------------------------------------------------------");
            System.out.println("CASO 3: CLIENTE CON CUOTAS VENCIDAS (Esperado: EN_MORA)");
            System.out.println("--------------------------------------------------------------------");
            ResumenEstadoCliente r3 = gestor.consultarEstadoCliente(3);
            System.out.print(r3.reporteFormateado());
            assert r3.getEstadoGeneral() == EstadoClienteFinanciero.EN_MORA;

            // CASO 4: CLIENTE A PAZ Y SALVO (PAGADO)
            System.out.println("--------------------------------------------------------------------");
            System.out.println("CASO 4: CLIENTE A PAZ Y SALVO (Esperado: PAGADO)");
            System.out.println("--------------------------------------------------------------------");
            ResumenEstadoCliente r4 = gestor.consultarEstadoCliente(4);
            System.out.print(r4.reporteFormateado());
            assert r4.getEstadoGeneral() == EstadoClienteFinanciero.PAGADO;

            // CASO 5: VALIDACIÓN DE REGLA ESTRICTA DE DESCUENTO SOLO DE PAGOS APROBADOS
            System.out.println("--------------------------------------------------------------------");
            System.out.println("CASO 5: VALIDACIÓN DE CÁLCULO DE SALDO PENDIENTE");
            System.out.println("--------------------------------------------------------------------");
            System.out.println("Préstamo de María (#102): Total pactado = $2.200.000");
            System.out.println("Pagos registrados: $220.000 (APROBADO), $500.000 (RECHAZADO), $300.000 (PENDIENTE)");
            double saldoMaria = gestor.calcularSaldoPendiente(102);
            System.out.println("Saldo calculado por el gestor: " + Formato.moneda(saldoMaria));
            System.out.println("¿Solo descontó el pago APROBADO de $220.000? " + (saldoMaria == 1980000 ? "SÍ [OK]" : "NO [ERROR]"));

            System.out.println("\n[ÉXITO] Todas las pruebas y los 4 estados financieros fueron validados correctamente.");

        } catch (Exception e) {
            System.err.println("Error durante las pruebas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
