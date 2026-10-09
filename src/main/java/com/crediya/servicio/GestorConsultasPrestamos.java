package com.crediya.servicio;

import com.crediya.dao.ClienteDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.excepciones.ClienteNoEncontradoException;
import com.crediya.excepciones.CrediYaException;
import com.crediya.excepciones.DatosInvalidosException;
import com.crediya.excepciones.ErrorPersistenciaException;
import com.crediya.modelo.EstadoPrestamo;
import com.crediya.modelo.Prestamo;
import com.crediya.util.Formato;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class GestorConsultasPrestamos {

    private final PrestamoDAO prestamoDAO;
    private final ClienteDAO clienteDAO;

    public GestorConsultasPrestamos() {
        this(new PrestamoDAO(), new ClienteDAO());
    }

    public GestorConsultasPrestamos(PrestamoDAO prestamoDAO, ClienteDAO clienteDAO) {
        this.prestamoDAO = prestamoDAO;
        this.clienteDAO = clienteDAO;
    }

   
    public List<Prestamo> consultarTodos() throws ErrorPersistenciaException {
        try {
            List<Prestamo> lista = prestamoDAO.listar();
            if (lista.isEmpty()) {
                System.out.println("No se encontraron préstamos registrados en el sistema.");
            }
            return lista;
        } catch (ErrorPersistenciaException e) {
            System.err.println("Error al consultar préstamos: " + e.getMessage());
            throw e;
        }
    }

    
    public Optional<Prestamo> buscarPorId(int id) throws ErrorPersistenciaException {
        if (id <= 0) {
            System.out.println("El identificador del préstamo debe ser mayor que cero.");
            return Optional.empty();
        }
        try {
            Optional<Prestamo> prestamo = prestamoDAO.buscarPorId(id);
            if (prestamo.isEmpty()) {
                System.out.println("No se encontró ningún préstamo con el ID: " + id);
            }
            return prestamo;
        } catch (ErrorPersistenciaException e) {
            System.err.println("Error al buscar préstamo por ID " + id + ": " + e.getMessage());
            throw e;
        }
    }

   
    public List<Prestamo> consultarPorCliente(int clienteId) throws CrediYaException {
        if (clienteId <= 0) {
            throw new DatosInvalidosException("El ID del cliente debe ser mayor a cero.");
        }
       
        clienteDAO.buscarPorId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException("No existe un cliente con ID " + clienteId));

        List<Prestamo> prestamos = prestamoDAO.listarPorCliente(clienteId);
        if (prestamos.isEmpty()) {
            System.out.println("El cliente con ID " + clienteId + " no tiene préstamos asociados.");
        }
        return prestamos;
    }

    
     
    public List<Prestamo> buscarPrestamosPorMonto(double minimo, double maximo)
            throws DatosInvalidosException, ErrorPersistenciaException {
        if (minimo < 0 || maximo < 0) {
            throw new DatosInvalidosException("Los montos de búsqueda deben ser mayores o iguales a cero.");
        }
        if (minimo > maximo) {
            throw new DatosInvalidosException(
                    "El monto mínimo (" + Formato.moneda(minimo) + ") no puede ser superior al máximo ("
                            + Formato.moneda(maximo) + ").");
        }

        List<Prestamo> todos = prestamoDAO.listar();

 
        List<Prestamo> filtrados = todos.stream()
                .filter(p -> p.getMonto() >= minimo && p.getMonto() <= maximo)
                .collect(Collectors.toList());

        mostrarResultadosConsulta(filtrados, "Rango de montos [" + Formato.moneda(minimo) + " - " + Formato.moneda(maximo) + "]");
        return filtrados;
    }

    
    public List<Prestamo> buscarPrestamosPorEstado(String estado)
            throws DatosInvalidosException, ErrorPersistenciaException {
        if (estado == null || estado.trim().isEmpty()) {
            throw new DatosInvalidosException("Debe ingresar un estado para la búsqueda.");
        }

        String estadoNormalizado = estado.trim().toUpperCase();
        boolean estadoValido = Arrays.stream(EstadoPrestamo.values())
                .anyMatch(e -> e.name().equals(estadoNormalizado));

        if (!estadoValido) {
            String estadosPermitidos = Arrays.stream(EstadoPrestamo.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new DatosInvalidosException("Estado inválido: '" + estado
                    + "'. Estados válidos permitidos: [" + estadosPermitidos + "].");
        }

        EstadoPrestamo estadoEnum = EstadoPrestamo.valueOf(estadoNormalizado);
        List<Prestamo> todos = prestamoDAO.listar();

    
        List<Prestamo> filtrados = todos.stream()
                .filter(p -> p.getEstado() == estadoEnum)
                .collect(Collectors.toList());

        mostrarResultadosConsulta(filtrados, "Estado: " + estadoEnum);
        return filtrados;
    }

    
    public List<Prestamo> ordenarPorMonto(List<Prestamo> prestamos, boolean ascendente) {
        if (prestamos == null || prestamos.isEmpty()) {
            return List.of();
        }
        Comparator<Prestamo> comparador = Comparator.comparingDouble(Prestamo::getMonto);
        if (!ascendente) {
            comparador = comparador.reversed();
        }
        return prestamos.stream()
                .sorted(comparador)
                .collect(Collectors.toList());
    }

    
    public List<Prestamo> ordenarPorFecha(List<Prestamo> prestamos, boolean ascendente) {
        if (prestamos == null || prestamos.isEmpty()) {
            return List.of();
        }
        Comparator<Prestamo> comparador = Comparator.comparing(Prestamo::getFechaInicio);
        if (!ascendente) {
            comparador = comparador.reversed();
        }
        return prestamos.stream()
                .sorted(comparador)
                .collect(Collectors.toList());
    }

   
    private void mostrarResultadosConsulta(List<Prestamo> resultados, String criterio) {
        System.out.println("\n=== RESULTADOS (" + criterio + ") ===");
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron préstamos que coincidan con el criterio especificado.");
        } else {
            for (Prestamo p : resultados) {
                System.out.printf("Préstamo ID: %d - Monto: %s - Estado: %s - Cliente: %s%n",
                        p.getId(),
                        Formato.moneda(p.getMonto()),
                        p.getEstado(),
                        p.getCliente() != null ? p.getCliente().getNombre() : "N/A");
            }
        }
        System.out.println("Total de préstamos encontrados: " + resultados.size());
    }
}
