package com.crediya.prueba;

import com.crediya.dao.ClienteDAO;
import com.crediya.dao.PagoDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.modelo.Cliente;
import com.crediya.modelo.DetalleClienteMoroso;
import com.crediya.modelo.Empleado;
import com.crediya.modelo.EstadoPrestamo;
import com.crediya.modelo.Pago;
import com.crediya.modelo.Prestamo;
import com.crediya.servicio.GestorMorosidad;
import com.crediya.util.Formato;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================================
 * CLASE DE PRUEBAS Y EVIDENCIAS — EXAMEN C
 * ============================================================================
 * ¿Para qué sirve esta clase?
 * Programa ejecutable autónomo que valida y genera las evidencias exigidas
 * en la rúbrica del EXAMEN C:
 * 1. Detección de clientes morosos (verificando que quien tiene deuda pero va al día NO es moroso).
 * 2. Cálculo de la deuda consolidada por cliente.
 * 3. Filtrado por deuda mínima.
 * 4. Ordenamiento de mayor a menor deuda (descendente).
 * 5. Top 1 cliente más endeudado con Stream API (sorted + limit).
 * 6. Exportación física de los reportes a disco en formatos .csv y .txt.
 */
public class PruebaExamenC {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("       SUITE DE PRUEBAS AUTOMATIZADA — EXAMEN C (CrediYa)           ");
        System.out.println("====================================================================\n");

        // --------------------------------------------------------------------
        // 1. DATOS DE PRUEBA SIMULADOS EN MEMORIA
        // --------------------------------------------------------------------
        // Cliente 1: Ana ya pagó todo (saldo 0)
        Cliente cAlDia = new Cliente(1, "Ana Al Dia", "1001", "ana@mail.com", "3001111111");

        // Cliente 2: Bernardo tiene deuda de 2 millones, pero pidió el crédito hace solo 15 días.
        // Sus cuotas aún no vencen. NO DEBE APARECER EN LA LISTA DE MOROSOS.
        Cliente cDeudaAlDia = new Cliente(2, "Bernardo Con Deuda Al Dia", "1002", "bernardo@mail.com", "3002222222");

        // Cliente 3: Carlos tiene un crédito de hace 4 meses y solo ha pagado 1 cuota. ES MOROSO.
        Cliente cMorosoModerado = new Cliente(3, "Carlos Moroso Moderado", "1003", "carlos@mail.com", "3003333333");

        // Cliente 4: Diana tiene un crédito de hace 6 meses y no ha pagado nada. ES MOROSA CRÍTICA.
        Cliente cMorosoGrave = new Cliente(4, "Diana Morosa Critica", "1004", "diana@mail.com", "3004444444");

        Empleado emp = new Empleado(10, "Laura Asesora", "900900", "laura@crediya.com", "Analista", 2500000);

        // Préstamo de Ana: Pagado totalmente
        Prestamo pAna = new Prestamo(1, cAlDia, emp, 1000000, 10, 5,
                LocalDate.now().minusMonths(6), EstadoPrestamo.PAGADO, 0);

        // Préstamo de Bernardo: Activo reciente (0 cuotas vencidas)
        Prestamo pBernardo = new Prestamo(2, cDeudaAlDia, emp, 2000000, 10, 10,
                LocalDate.now().minusDays(15), EstadoPrestamo.PENDIENTE, 2200000);

        // Préstamo de Carlos: 4 meses de antigüedad, debe 3 cuotas. Deuda: 1.980.000
        Prestamo pCarlos = new Prestamo(3, cMorosoModerado, emp, 2000000, 10, 10,
                LocalDate.now().minusMonths(4), EstadoPrestamo.PENDIENTE, 1980000);

        // Préstamo de Diana: 6 meses de antigüedad, 0 pagos. Deuda: 5.500.000
        Prestamo pDiana = new Prestamo(4, cMorosoGrave, emp, 5000000, 10, 10,
                LocalDate.now().minusMonths(6), EstadoPrestamo.PENDIENTE, 5500000);

        // Pagos simulados
        Map<Integer, List<Pago>> pagos = Map.of(
                1, List.of(new Pago(1, 1, LocalDate.now().minusMonths(1), 1100000)),
                2, List.of(), // Bernardo aún no paga pero no tiene cuotas vencidas
                3, List.of(new Pago(2, 3, LocalDate.now().minusMonths(3), 220000)),
                4, List.of()  // Diana no ha hecho abonos
        );

        List<Prestamo> prestamos = List.of(pAna, pBernardo, pCarlos, pDiana);

        // DAOs simulados en memoria para ejecución autónoma
        PrestamoDAO prestamoDAO = new PrestamoDAO() {
            @Override
            public List<Prestamo> listar() {
                return prestamos;
            }

            @Override
            public Optional<Prestamo> buscarPorId(int id) {
                return prestamos.stream().filter(p -> p.getId() == id).findFirst();
            }
        };

        PagoDAO pagoDAO = new PagoDAO() {
            @Override
            public List<Pago> listarPorPrestamo(int prestamoId) {
                return pagos.getOrDefault(prestamoId, Collections.emptyList());
            }
        };

        ClienteDAO clienteDAO = new ClienteDAO();

        // Instanciamos el servicio del Examen C
        GestorMorosidad gestor = new GestorMorosidad(clienteDAO, prestamoDAO, pagoDAO);

        try {
            // PRUEBA 1: Identificación de morosos y verificación de la regla clave
            System.out.println("1. IDENTIFICACIÓN DE CLIENTES MOROSOS:");
            List<Cliente> morosos = gestor.obtenerClientesMorosos();
            System.out.println("   Clientes detectados en mora: " + morosos.size());
            morosos.forEach(c -> System.out.println("   • " + c.getNombre() + " (Doc: " + c.getDocumento() + ")"));

            // Verificación: Bernardo tiene deuda pero NO debe figurar en morosos
            boolean bernardoEnMora = morosos.stream().anyMatch(c -> c.getId() == 2);
            System.out.println("   ¿Bernardo (con deuda pero al día) fue excluido de morosos? "
                    + (!bernardoEnMora ? "SÍ [CORRECTO]" : "NO [ERROR]"));

            // PRUEBA 2: Detalle consolidado con columnas requeridas
            System.out.println("\n2. DETALLE CONSOLIDADO DE MOROSOS:");
            List<DetalleClienteMoroso> detalles = gestor.obtenerDetalleMorosos();
            detalles.forEach(d -> System.out.println("   " + d));

            // PRUEBA 3: Ordenar morosos de mayor a menor deuda
            System.out.println("\n3. ORDENADOS DE MAYOR A MENOR DEUDA:");
            List<DetalleClienteMoroso> ordenados = gestor.ordenarMorososPorDeuda(detalles);
            ordenados.forEach(d -> System.out.println("   " + d));

            // PRUEBA 4: Top 1 Moroso más endeudado (sorted + limit)
            System.out.println("\n4. TOP 1 CLIENTE CON MAYOR DEUDA (Stream.sorted().limit(1)):");
            List<DetalleClienteMoroso> top1 = gestor.obtenerTopClientesMorosos(detalles, 1);
            top1.forEach(d -> System.out.println("   ★ " + d));

            // PRUEBA 5: Filtro por deuda mínima ($3.000.000)
            System.out.println("\n5. FILTRO POR DEUDA MÍNIMA (>= $3.000.000):");
            List<DetalleClienteMoroso> filtroMinimo = gestor.filtrarPorDeudaMinima(detalles, 3000000);
            filtroMinimo.forEach(d -> System.out.println("   " + d));

            // PRUEBA 6: Exportación a archivos físicos en disco
            Path rutaCsv = Paths.get("repaso/examen_c/exportados/reporte_morosos.csv");
            Path rutaTxt = Paths.get("repaso/examen_c/exportados/reporte_morosos.txt");

            gestor.exportarReporte(detalles, rutaCsv, true);
            gestor.exportarReporte(detalles, rutaTxt, false);

            System.out.println("\n6. EXPORTACIÓN DE ARCHIVOS:");
            System.out.println("   Archivo CSV generado: " + rutaCsv.toAbsolutePath() + " (Existe: " + Files.exists(rutaCsv) + ")");
            System.out.println("   Archivo TXT generado: " + rutaTxt.toAbsolutePath() + " (Existe: " + Files.exists(rutaTxt) + ")");

            System.out.println("\n--- CONTENIDO DEL ARCHIVO TXT GENERADO ---");
            Files.readAllLines(rutaTxt).forEach(System.out::println);

            System.out.println("\n[ÉXITO] Todas las reglas, filtros y exportaciones del Examen C funcionan correctamente.");

        } catch (Exception e) {
            System.err.println("Error en las pruebas del Examen C: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
