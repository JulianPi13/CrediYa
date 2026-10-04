package com.crediya;

import com.crediya.archivo.GestorArchivos;
import com.crediya.excepciones.CrediyaException;
import com.crediya.excepciones.PersistenciaException;
import com.crediya.excepciones.PrestamoException;
import com.crediya.modelo.Cliente;
import com.crediya.modelo.Empleado;
import com.crediya.modelo.Pago;
import com.crediya.modelo.Persona;
import com.crediya.modelo.Prestamo;
import com.crediya.servicio.ClienteServicio;
import com.crediya.servicio.EmpleadoServicio;
import com.crediya.servicio.PagoServicio;
import com.crediya.servicio.PrestamoServicio;
import com.crediya.servicio.ReporteServicio;
import com.crediya.util.Constantes;
import com.crediya.util.EntradaConsola;
import com.crediya.util.Formato;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final int OPCION_MAXIMA = 15;

    private static final Scanner scanner = new Scanner(System.in);

    private static final EntradaConsola entrada =
            new EntradaConsola(scanner);

    private static final EmpleadoServicio empleadoServicio =
            new EmpleadoServicio();

    private static final ClienteServicio clienteServicio =
            new ClienteServicio();

    private static final PrestamoServicio prestamoServicio =
            new PrestamoServicio();

    private static final PagoServicio pagoServicio =
            new PagoServicio();

    private static final ReporteServicio reporteServicio =
            new ReporteServicio();

    private static final GestorArchivos gestorArchivos =
            new GestorArchivos();

    public static void main(String[] args) {

        int opcion = -1;

        do {

            try {

                mostrarMenu();

                opcion = entrada.leerEntero(
                        "Seleccione una opción: ", 0, OPCION_MAXIMA
                );

                switch (opcion) {

                    case 1:
                        registrarEmpleado();
                        break;

                    case 2:
                        listarEmpleados();
                        break;

                    case 3:
                        registrarCliente();
                        break;

                    case 4:
                        listarClientes();
                        break;

                    case 5:
                        crearPrestamo();
                        break;

                    case 6:
                        listarPrestamos();
                        break;

                    case 7:
                        registrarPago();
                        break;

                    case 8:
                        listarPagos();
                        break;

                    case 9:
                        mostrarReportes();
                        break;

                    case 10:
                        guardarArchivos();
                        break;

                    case 11:
                        leerArchivos();
                        break;

                    case 12:
                        refinanciarPrestamo();
                        break;

                    case 13:
                        historialPagos();
                        break;

                    case 14:
                        consultarPrestamosCliente();
                        break;

                    case 15:
                        cambiarEstadoPrestamo();
                        break;

                    case 0:
                        System.out.println(
                                "\nGracias por utilizar CrediYa."
                        );
                        break;

                    default:
                        System.out.println(
                                "\nOpción no válida."
                        );
                }

            } catch (CrediyaException e) {

                // Errores de negocio: préstamos, pagos, validaciones
                System.out.println(
                        "\nError: " + e.getMessage()
                );

            } catch (PersistenciaException e) {

                System.out.println(
                        "\nError de base de datos: " + e.getMessage()
                );

            } catch (IllegalStateException e) {

                // La entrada estándar se cerró: salimos ordenadamente.
                System.out.println("\n" + e.getMessage() + " Saliendo...");
                opcion = 0;

            } catch (Exception e) {

                System.out.println(
                        "\nHa ocurrido un error: "
                        + e.getMessage()
                );
            }

        } while (opcion != 0);

        scanner.close();
    }

    // =========================================================
    // MENÚ PRINCIPAL
    // =========================================================

    private static void mostrarMenu() {

        System.out.println("\n================================");
        System.out.println("          CREDIYA S.A.S.");
        System.out.println("       SISTEMA DE CRÉDITOS");
        System.out.println("================================");

        System.out.println("1. Registrar empleado");
        System.out.println("2. Listar empleados");
        System.out.println("3. Registrar cliente");
        System.out.println("4. Listar clientes");
        System.out.println("5. Crear préstamo");
        System.out.println("6. Listar préstamos");
        System.out.println("7. Registrar pago");
        System.out.println("8. Listar pagos");
        System.out.println("9. Ver reportes");
        System.out.println("10. Guardar datos en archivos");
        System.out.println("11. Leer archivos");
        System.out.println("12. Refinanciar préstamo");
        System.out.println("13. Historial de pagos por préstamo");
        System.out.println("14. Consultar préstamos de un cliente");
        System.out.println("15. Cambiar estado de un préstamo");
        System.out.println("0. Salir");

        System.out.println("================================");
    }

    // =========================================================
    // EMPLEADOS
    // =========================================================

    private static void registrarEmpleado() throws CrediyaException {

        System.out.println(
                "\n===== REGISTRAR EMPLEADO ====="
        );

        String nombre = entrada.leerNombre(
                "Nombre: "
        );

        String documento = entrada.leerDocumento(
                "Documento: ",
                doc -> !empleadoServicio.existeDocumento(doc),
                "Ya existe un empleado con ese documento."
        );

        String rol = entrada.elegirDeLista(
                "Rol:", Constantes.ROLES
        );

        String correo = entrada.leerCorreo(
                "Correo: ",
                c -> !empleadoServicio.existeCorreo(c),
                "Ya existe un empleado con ese correo."
        );

        double salario = entrada.leerDecimal(
                "Salario: ", 0.01, Constantes.SALARIO_MAX
        );

        Empleado empleado = new Empleado();

        empleado.setNombre(nombre);
        empleado.setDocumento(documento);
        empleado.setRol(rol);
        empleado.setCorreo(correo);
        empleado.setSalario(salario);

        empleadoServicio.registrarEmpleado(
                empleado
        );

        sincronizarArchivos();
    }

    private static void listarEmpleados() {

        System.out.println(
                "\n===== LISTA DE EMPLEADOS ====="
        );

        empleadoServicio.listarEmpleados();
    }

    // =========================================================
    // CLIENTES
    // =========================================================

    private static void registrarCliente() throws CrediyaException {

        System.out.println(
                "\n===== REGISTRAR CLIENTE ====="
        );

        String nombre = entrada.leerNombre(
                "Nombre: "
        );

        String documento = entrada.leerDocumento(
                "Documento: ",
                doc -> !clienteServicio.existeDocumento(doc),
                "Ya existe un cliente con ese documento."
        );

        String correo = entrada.leerCorreo(
                "Correo: ",
                c -> !clienteServicio.existeCorreo(c),
                "Ya existe un cliente con ese correo."
        );

        String telefono = entrada.leerTelefono(
                "Teléfono: "
        );

        Cliente cliente = new Cliente();

        cliente.setNombre(nombre);
        cliente.setDocumento(documento);
        cliente.setCorreo(correo);
        cliente.setTelefono(telefono);

        clienteServicio.registrarCliente(
                cliente
        );

        sincronizarArchivos();
    }

    private static void listarClientes() {

        System.out.println(
                "\n===== LISTA DE CLIENTES ====="
        );

        clienteServicio.listarClientes();
    }

    private static void consultarPrestamosCliente() {

        System.out.println(
                "\n===== PRÉSTAMOS DE UN CLIENTE ====="
        );

        List<Cliente> clientes = clienteServicio.obtenerClientes();

        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }

        mostrarPersonas(clientes);

        Cliente cliente = leerClienteExistente();

        List<Prestamo> prestamos =
                prestamoServicio.prestamosDeCliente(cliente.getId());

        if (prestamos.isEmpty()) {
            System.out.println(cliente.getNombre() + " no tiene préstamos.");
            return;
        }

        System.out.println("\nPréstamos de " + cliente.getNombre() + ":");

        prestamos.forEach(p -> System.out.println(
                "  #" + p.getId()
                + " | Monto: " + Formato.moneda(p.getMonto())
                + " | Total: " + Formato.moneda(p.getMontoTotal())
                + " | Saldo: " + Formato.moneda(p.getSaldoPendiente())
                + " | Estado: " + p.getEstado()
        ));

        double deuda = prestamos.stream()
                .mapToDouble(Prestamo::getSaldoPendiente)
                .sum();

        System.out.println("Deuda total del cliente: " + Formato.moneda(deuda));
    }

    // =========================================================
    // PRÉSTAMOS
    // =========================================================

    private static void crearPrestamo()
            throws PrestamoException {

        System.out.println(
                "\n===== CREAR PRÉSTAMO ====="
        );

        List<Cliente> clientes = clienteServicio.obtenerClientes();
        List<Empleado> empleados = empleadoServicio.obtenerEmpleados();

        if (clientes.isEmpty()) {
            throw new PrestamoException(
                    "Primero debe registrar al menos un cliente."
            );
        }

        if (empleados.isEmpty()) {
            throw new PrestamoException(
                    "Primero debe registrar al menos un empleado."
            );
        }

        System.out.println("\nClientes:");
        mostrarPersonas(clientes);

        Cliente cliente = leerClienteExistente();

        System.out.println("\nEmpleados:");
        mostrarPersonas(empleados);

        Empleado empleado = leerEmpleadoExistente();

        double monto = entrada.leerDecimal(
                "Monto del préstamo: ", 1, Constantes.MONTO_PRESTAMO_MAX
        );

        double interes = entrada.leerDecimal(
                "Interés (%): ", 0, Constantes.INTERES_MAX
        );

        int cuotas = entrada.leerEntero(
                "Número de cuotas (1 a " + Constantes.CUOTAS_MAX + "): ",
                1, Constantes.CUOTAS_MAX
        );

        Prestamo prestamo = new Prestamo();

        prestamo.setCliente(cliente);
        prestamo.setEmpleado(empleado);
        prestamo.setMonto(monto);
        prestamo.setInteres(interes);
        prestamo.setCuotas(cuotas);
        prestamo.setFechaInicio(
                LocalDate.now()
        );

        prestamoServicio.crearPrestamo(
                prestamo
        );

        System.out.println(
                "\n===== PRÉSTAMO CREADO ====="
        );

        System.out.println(
                "Cliente: " + cliente.getNombre()
        );

        System.out.println(
                "Monto: " + Formato.moneda(prestamo.getMonto())
        );

        System.out.println(
                "Interés: "
                + prestamo.getInteres()
                + "%"
        );

        System.out.println(
                "Monto total: "
                + Formato.moneda(prestamo.getMontoTotal())
        );

        System.out.println(
                "Cuota mensual: "
                + Formato.moneda(prestamo.getCuotaMensual())
        );

        System.out.println(
                "Saldo pendiente: "
                + Formato.moneda(prestamo.getSaldoPendiente())
        );

        System.out.println(
                "Fecha de vencimiento: "
                + prestamo.getFechaVencimiento()
        );

        System.out.println(
                "Estado: "
                + prestamo.getEstado()
        );

        sincronizarArchivos();
    }

    private static void listarPrestamos() {

        System.out.println(
                "\n===== LISTA DE PRÉSTAMOS ====="
        );

        prestamoServicio.listarPrestamos();
    }

    private static void cambiarEstadoPrestamo()
            throws CrediyaException {

        System.out.println(
                "\n===== CAMBIAR ESTADO DE PRÉSTAMO ====="
        );

        Prestamo prestamo = leerPrestamoExistente();

        System.out.println(
                "Estado actual: " + prestamo.getEstado()
                + " | Saldo: " + Formato.moneda(prestamo.getSaldoPendiente())
        );

        String nuevoEstado = entrada.elegirDeLista(
                "Nuevo estado:",
                List.of(Constantes.ESTADO_PENDIENTE, Constantes.ESTADO_PAGADO)
        );

        // Si quiere marcarlo como pagado pero aún debe, se ofrece
        // registrar el pago del saldo para que todo quede cuadrado.
        if (Constantes.ESTADO_PAGADO.equals(nuevoEstado)
                && prestamo.getSaldoPendiente() > 0) {

            boolean pagar = entrada.leerConfirmacion(
                    "El préstamo aún debe "
                    + Formato.moneda(prestamo.getSaldoPendiente())
                    + ". ¿Registrar un pago por el saldo total?"
            );

            if (!pagar) {
                System.out.println("No se realizaron cambios.");
                return;
            }

            Pago pago = new Pago();
            pago.setPrestamo(prestamo);
            pago.setFechaPago(LocalDate.now());
            pago.setMonto(prestamo.getSaldoPendiente());

            pagoServicio.registrarPago(pago);

        } else {

            prestamoServicio.cambiarEstado(prestamo.getId(), nuevoEstado);
        }

        sincronizarArchivos();
    }

    // =========================================================
    // PAGOS
    // =========================================================

    private static void registrarPago()
            throws PrestamoException {

        System.out.println(
                "\n===== REGISTRAR PAGO ====="
        );

        Prestamo prestamo = leerPrestamoExistente();

        if (Constantes.ESTADO_PAGADO.equals(prestamo.getEstado())) {
            throw new PrestamoException(
                    "El préstamo ya está completamente pagado."
            );
        }

        System.out.println(
                "Saldo pendiente: " + Formato.moneda(prestamo.getSaldoPendiente())
                + " | Cuota mensual: " + Formato.moneda(prestamo.getCuotaMensual())
        );

        double monto = entrada.leerDecimal(
                "Monto del pago: ", 0.01, prestamo.getSaldoPendiente()
        );

        Pago pago = new Pago();

        pago.setPrestamo(
                prestamo
        );

        pago.setFechaPago(
                LocalDate.now()
        );

        pago.setMonto(
                monto
        );

        pagoServicio.registrarPago(
                pago
        );

        sincronizarArchivos();
    }

    private static void listarPagos() {

        System.out.println(
                "\n===== HISTORIAL DE PAGOS ====="
        );

        pagoServicio.listarPagos();
    }

    // =========================================================
    // HISTORIAL DE PAGOS POR PRÉSTAMO
    // =========================================================

    private static void historialPagos() throws PrestamoException {

        System.out.println(
                "\n===== HISTORIAL DE PAGOS ====="
        );

        Prestamo prestamo = leerPrestamoExistente();

        pagoServicio.mostrarHistorialPorPrestamo(
                prestamo.getId()
        );
    }

    // =========================================================
    // REFINANCIAMIENTO
    // =========================================================

    private static void refinanciarPrestamo()
            throws PrestamoException {

        System.out.println(
                "\n===== REFINANCIAR PRÉSTAMO ====="
        );

        Prestamo prestamo = leerPrestamoExistente();

        if (Constantes.ESTADO_PAGADO.equals(prestamo.getEstado())) {
            throw new PrestamoException(
                    "No se puede refinanciar un préstamo que ya está pagado."
            );
        }

        if (prestamo.getCuotas() >= Constantes.CUOTAS_MAX) {
            throw new PrestamoException(
                    "El préstamo ya tiene el máximo de cuotas permitido."
            );
        }

        System.out.println("Cuotas actuales: " + prestamo.getCuotas());

        int nuevasCuotas = entrada.leerEntero(
                "Nueva cantidad de cuotas: ",
                prestamo.getCuotas() + 1,
                Constantes.CUOTAS_MAX
        );

        prestamoServicio.refinanciarPrestamo(
                prestamo.getId(),
                nuevasCuotas
        );

        sincronizarArchivos();
    }

    // =========================================================
    // REPORTES
    // =========================================================

    private static void mostrarReportes() {

        int opcion;

        do {

            System.out.println(
                    "\n===== REPORTES ====="
            );

            System.out.println(
                    "1. Todos los préstamos"
            );

            System.out.println(
                    "2. Préstamos pendientes"
            );

            System.out.println(
                    "3. Préstamos pagados"
            );

            System.out.println(
                    "4. Préstamos activos"
            );

            System.out.println(
                    "5. Préstamos vencidos"
            );

            System.out.println(
                    "6. Clientes morosos"
            );

            System.out.println(
                    "7. Total prestado"
            );

            System.out.println(
                    "8. Saldo pendiente total"
            );

            System.out.println(
                    "9. Préstamos por cliente"
            );

            System.out.println(
                    "10. Préstamos por empleado"
            );

            System.out.println(
                    "0. Volver"
            );

            opcion = entrada.leerEntero(
                    "Seleccione una opción: ", 0, 10
            );

            switch (opcion) {

                case 1:

                    reporteServicio
                            .mostrarTodosLosPrestamos();

                    break;

                case 2:

                    reporteServicio
                            .mostrarPrestamosPendientes();

                    break;

                case 3:

                    reporteServicio
                            .mostrarPrestamosPagados();

                    break;

                case 4:

                    reporteServicio
                            .mostrarPrestamosActivos();

                    break;

                case 5:

                    reporteServicio
                            .mostrarPrestamosVencidos();

                    break;

                case 6:

                    reporteServicio
                            .mostrarClientesMorosos();

                    break;

                case 7:

                    reporteServicio
                            .mostrarTotalPrestado();

                    break;

                case 8:

                    reporteServicio
                            .mostrarSaldoPendienteTotal();

                    break;

                case 9:

                    int clienteId = entrada.leerEnteroPositivo(
                            "ID del cliente: "
                    );

                    reporteServicio
                            .mostrarPrestamosCliente(
                                    clienteId
                            );

                    break;

                case 10:

                    int empleadoId = entrada.leerEnteroPositivo(
                            "ID del empleado: "
                    );

                    reporteServicio
                            .mostrarPrestamosEmpleado(
                                    empleadoId
                            );

                    break;

                case 0:

                    break;

                default:

                    System.out.println(
                            "Opción no válida."
                    );
            }

        } while (opcion != 0);
    }

    // =========================================================
    // ARCHIVOS
    // =========================================================

    private static void guardarArchivos() {

        System.out.println(
                "\n===== GUARDANDO INFORMACIÓN ====="
        );

        try {
            escribirArchivos();

            System.out.println(
                    "Información guardada correctamente en la carpeta \"archivos\" "
                    + "(empleados.txt, clientes.txt, prestamos.txt, pagos.txt)."
            );

        } catch (IOException e) {

            System.out.println(
                    "Error al guardar archivos: " + e.getMessage()
            );
        }
    }

    /**
     * Después de cada registro se actualizan los archivos de texto
     * automáticamente, así la información queda en archivo y en MySQL.
     */
    private static void sincronizarArchivos() {

        try {
            escribirArchivos();

        } catch (IOException | PersistenciaException e) {

            System.out.println(
                    "Aviso: no se pudieron actualizar los archivos de texto: "
                    + e.getMessage()
            );
        }
    }

    private static void escribirArchivos() throws IOException {

        gestorArchivos.guardarEmpleados(
                empleadoServicio.obtenerEmpleados()
        );

        gestorArchivos.guardarClientes(
                clienteServicio.obtenerClientes()
        );

        gestorArchivos.guardarPrestamos(
                prestamoServicio.obtenerPrestamos()
        );

        gestorArchivos.guardarPagos(
                pagoServicio.obtenerPagos()
        );
    }

    private static void leerArchivos() {

        System.out.println(
                "\n===== EMPLEADOS ====="
        );

        gestorArchivos.leerArchivoEmpleados();

        System.out.println(
                "\n===== CLIENTES ====="
        );

        gestorArchivos.leerArchivoClientes();

        System.out.println(
                "\n===== PRÉSTAMOS ====="
        );

        gestorArchivos.leerArchivoPrestamos();

        System.out.println(
                "\n===== PAGOS ====="
        );

        gestorArchivos.leerArchivoPagos();
    }

    // =========================================================
    // AYUDAS DE SELECCIÓN
    // =========================================================

    /**
     * Muestra clientes o empleados. Funciona con cualquier subclase
     * de Persona gracias al polimorfismo.
     */
    private static void mostrarPersonas(List<? extends Persona> personas) {

        personas.forEach(p -> System.out.println("  " + p.descripcionCorta()));
    }

    /** Pide un ID de cliente hasta que exista. */
    private static Cliente leerClienteExistente() {

        while (true) {

            int id = entrada.leerEnteroPositivo("Ingrese el ID del cliente: ");

            var cliente = clienteServicio.buscarPorId(id);

            if (cliente.isPresent()) {
                return cliente.get();
            }

            System.out.println("No existe un cliente con ID " + id + ". Intente de nuevo.");
        }
    }

    /** Pide un ID de empleado hasta que exista. */
    private static Empleado leerEmpleadoExistente() {

        while (true) {

            int id = entrada.leerEnteroPositivo("Ingrese el ID del empleado: ");

            var empleado = empleadoServicio.buscarPorId(id);

            if (empleado.isPresent()) {
                return empleado.get();
            }

            System.out.println("No existe un empleado con ID " + id + ". Intente de nuevo.");
        }
    }

    /** Muestra los préstamos y pide un ID hasta que exista. */
    private static Prestamo leerPrestamoExistente() throws PrestamoException {

        List<Prestamo> prestamos = prestamoServicio.obtenerPrestamos();

        if (prestamos.isEmpty()) {
            throw new PrestamoException("No hay préstamos registrados.");
        }

        prestamos.forEach(p -> System.out.println(
                "  #" + p.getId()
                + " | " + p.getCliente().getNombre()
                + " | Saldo: " + Formato.moneda(p.getSaldoPendiente())
                + " | " + p.getEstado()
        ));

        while (true) {

            int id = entrada.leerEnteroPositivo("ID del préstamo: ");

            var prestamo = prestamoServicio.buscarPorId(id);

            if (prestamo.isPresent()) {
                return prestamo.get();
            }

            System.out.println("No existe un préstamo con ID " + id + ". Intente de nuevo.");
        }
    }
}
