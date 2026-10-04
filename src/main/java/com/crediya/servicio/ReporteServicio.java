package com.crediya.servicio;

import com.crediya.dao.PrestamoDAO;
import com.crediya.modelo.Cliente;
import com.crediya.modelo.Prestamo;
import com.crediya.util.Constantes;
import com.crediya.util.Formato;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ReporteServicio {

    private final PrestamoDAO prestamoDAO;

    public ReporteServicio() {
        this(new PrestamoDAO());
    }

    /** Permite inyectar otro DAO (por ejemplo, en memoria para pruebas). */
    public ReporteServicio(PrestamoDAO prestamoDAO) {
        this.prestamoDAO = prestamoDAO;
    }

    /** Préstamos vencidos: pendientes con saldo y fecha de vencimiento ya pasada. */
    public List<Prestamo> obtenerPrestamosVencidos() {

        LocalDate hoy = LocalDate.now();

        return prestamoDAO.listar().stream()
                .filter(p -> p.estaVencido(hoy))
                .collect(Collectors.toList());
    }

    // =========================================================
    // TODOS LOS PRÉSTAMOS
    // =========================================================

    public void mostrarTodosLosPrestamos() {

        List<Prestamo> prestamos =
                prestamoDAO.listar();

        if (prestamos.isEmpty()) {

            System.out.println(
                    "No existen préstamos registrados."
            );

            return;
        }

        prestamos.forEach(System.out::println);
    }

    // =========================================================
    // PRÉSTAMOS PENDIENTES
    // =========================================================

    public void mostrarPrestamosPendientes() {

        List<Prestamo> prestamos =
                prestamoDAO.listar();

        List<Prestamo> pendientes =
                prestamos.stream()
                        .filter(p ->
                                Constantes.ESTADO_PENDIENTE.equals(
                                        p.getEstado()
                                ))
                        .collect(Collectors.toList());

        if (pendientes.isEmpty()) {

            System.out.println(
                    "No existen préstamos pendientes."
            );

            return;
        }

        System.out.println(
                "\n===== PRÉSTAMOS PENDIENTES ====="
        );

        pendientes.forEach(
                System.out::println
        );
    }

    // =========================================================
    // PRÉSTAMOS PAGADOS
    // =========================================================

    public void mostrarPrestamosPagados() {

        List<Prestamo> prestamos =
                prestamoDAO.listar();

        List<Prestamo> pagados =
                prestamos.stream()
                        .filter(p ->
                                Constantes.ESTADO_PAGADO.equals(
                                        p.getEstado()
                                ))
                        .collect(Collectors.toList());

        if (pagados.isEmpty()) {

            System.out.println(
                    "No existen préstamos pagados."
            );

            return;
        }

        System.out.println(
                "\n===== PRÉSTAMOS PAGADOS ====="
        );

        pagados.forEach(
                System.out::println
        );
    }

    // =========================================================
    // PRÉSTAMOS ACTIVOS
    // =========================================================

    public void mostrarPrestamosActivos() {

        List<Prestamo> prestamos =
                prestamoDAO.listar();

        List<Prestamo> activos =
                prestamos.stream()
                        .filter(p ->
                                Constantes.ESTADO_PENDIENTE.equals(
                                        p.getEstado()
                                )
                                &&
                                p.getSaldoPendiente() > 0
                        )
                        .collect(Collectors.toList());

        if (activos.isEmpty()) {

            System.out.println(
                    "No existen préstamos activos."
            );

            return;
        }

        System.out.println(
                "\n===== PRÉSTAMOS ACTIVOS ====="
        );

        activos.forEach(p -> {

            System.out.println(
                    "ID: " + p.getId()
            );

            System.out.println(
                    "Cliente: "
                    + p.getCliente().getNombre()
            );

            System.out.println(
                    "Monto total: "
                    + Formato.moneda(p.getMontoTotal())
            );

            System.out.println(
                    "Saldo pendiente: "
                    + Formato.moneda(p.getSaldoPendiente())
            );

            System.out.println(
                    "Estado: "
                    + p.getEstado()
            );

            System.out.println(
                    "--------------------------------"
            );
        });
    }

    // =========================================================
    // PRÉSTAMOS VENCIDOS
    // =========================================================

    public void mostrarPrestamosVencidos() {

        List<Prestamo> vencidos =
                obtenerPrestamosVencidos();

        if (vencidos.isEmpty()) {

            System.out.println(
                    "No existen préstamos vencidos."
            );

            return;
        }

        System.out.println(
                "\n===== PRÉSTAMOS VENCIDOS ====="
        );

        vencidos.forEach(p -> {

            LocalDate fechaVencimiento =
                    p.getFechaVencimiento();

            System.out.println(
                    "ID: " + p.getId()
            );

            System.out.println(
                    "Cliente: "
                    + p.getCliente().getNombre()
            );

            System.out.println(
                    "Fecha de inicio: "
                    + p.getFechaInicio()
            );

            System.out.println(
                    "Fecha de vencimiento: "
                    + fechaVencimiento
            );

            System.out.println(
                    "Saldo pendiente: "
                    + Formato.moneda(p.getSaldoPendiente())
            );

            System.out.println(
                    "--------------------------------"
            );
        });
    }

    // =========================================================
    // CLIENTES MOROSOS
    // =========================================================

    public void mostrarClientesMorosos() {

        List<Prestamo> vencidos =
                obtenerPrestamosVencidos();

        if (vencidos.isEmpty()) {

            System.out.println(
                    "No existen clientes morosos."
            );

            return;
        }

        Map<Integer, List<Prestamo>> porCliente =
                vencidos.stream()
                        .collect(
                                Collectors.groupingBy(
                                        p -> p.getCliente().getId(),
                                        TreeMap::new,
                                        Collectors.toList()
                                )
                        );

        System.out.println(
                "\n===== CLIENTES MOROSOS ====="
        );

        porCliente.forEach(
                (clienteId, listaPrestamos) -> {

                    Cliente cliente =
                            listaPrestamos.get(0)
                                    .getCliente();

                    double deudaTotal =
                            listaPrestamos.stream()
                                    .mapToDouble(
                                            Prestamo::getSaldoPendiente
                                    )
                                    .sum();

                    System.out.println(
                            "Cliente: "
                            + cliente.getNombre()
                    );

                    System.out.println(
                            "Documento: "
                            + cliente.getDocumento()
                    );

                    System.out.println(
                            "Correo: "
                            + cliente.getCorreo()
                    );

                    System.out.println(
                            "Préstamos vencidos: "
                            + listaPrestamos.size()
                    );

                    System.out.println(
                            "Deuda total: "
                            + Formato.moneda(deudaTotal)
                    );

                    System.out.println(
                            "--------------------------------"
                    );
                }
        );
    }

    // =========================================================
    // TOTAL PRESTADO
    // =========================================================

    public void mostrarTotalPrestado() {

        List<Prestamo> prestamos =
                prestamoDAO.listar();

        double total =
                prestamos.stream()
                        .mapToDouble(
                                Prestamo::getMonto
                        )
                        .sum();

        System.out.println(
                "\n===== TOTAL PRESTADO ====="
        );

        System.out.println(
                "Total: " + Formato.moneda(total)
        );
    }

    // =========================================================
    // SALDO PENDIENTE TOTAL
    // =========================================================

    public void mostrarSaldoPendienteTotal() {

        List<Prestamo> prestamos =
                prestamoDAO.listar();

        double total =
                prestamos.stream()
                        .mapToDouble(
                                Prestamo::getSaldoPendiente
                        )
                        .sum();

        System.out.println(
                "\n===== SALDO PENDIENTE TOTAL ====="
        );

        System.out.println(
                "Saldo pendiente: " + Formato.moneda(total)
        );
    }

    // =========================================================
    // PRÉSTAMOS POR CLIENTE
    // =========================================================

    public void mostrarPrestamosCliente(
            int clienteId) {

        List<Prestamo> prestamos =
                prestamoDAO.listar();

        List<Prestamo> resultados =
                prestamos.stream()
                        .filter(p ->
                                p.getCliente().getId()
                                        == clienteId
                        )
                        .collect(Collectors.toList());

        if (resultados.isEmpty()) {

            System.out.println(
                    "El cliente no tiene préstamos."
            );

            return;
        }

        System.out.println(
                "\n===== PRÉSTAMOS DEL CLIENTE ====="
        );

        resultados.forEach(
                System.out::println
        );
    }

    // =========================================================
    // PRÉSTAMOS POR EMPLEADO
    // =========================================================

    public void mostrarPrestamosEmpleado(
            int empleadoId) {

        List<Prestamo> prestamos =
                prestamoDAO.listar();

        List<Prestamo> resultados =
                prestamos.stream()
                        .filter(p ->
                                p.getEmpleado().getId()
                                        == empleadoId
                        )
                        .collect(Collectors.toList());

        if (resultados.isEmpty()) {

            System.out.println(
                    "El empleado no tiene préstamos asignados."
            );

            return;
        }

        System.out.println(
                "\n===== PRÉSTAMOS DEL EMPLEADO ====="
        );

        resultados.forEach(
                System.out::println
        );
    }
}