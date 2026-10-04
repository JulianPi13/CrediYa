package com.crediya.servicio;

import com.crediya.dao.ClienteDAO;
import com.crediya.dao.EmpleadoDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.excepciones.PrestamoException;
import com.crediya.modelo.Prestamo;
import com.crediya.util.Constantes;
import com.crediya.util.Formato;
import com.crediya.util.Validador;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PrestamoServicio {

    private final PrestamoDAO prestamoDAO;
    private final ClienteDAO clienteDAO;
    private final EmpleadoDAO empleadoDAO;

    public PrestamoServicio() {
        this(new PrestamoDAO(), new ClienteDAO(), new EmpleadoDAO());
    }

    /** Permite inyectar otros DAO (por ejemplo, en memoria para pruebas). */
    public PrestamoServicio(PrestamoDAO prestamoDAO,
                            ClienteDAO clienteDAO,
                            EmpleadoDAO empleadoDAO) {
        this.prestamoDAO = prestamoDAO;
        this.clienteDAO = clienteDAO;
        this.empleadoDAO = empleadoDAO;
    }

    // =========================================================
    // CREAR PRÉSTAMO
    // =========================================================

    public void crearPrestamo(Prestamo prestamo)
            throws PrestamoException {

        if (prestamo == null) {

            throw new PrestamoException(
                    "El préstamo no puede ser nulo."
            );
        }

        if (prestamo.getCliente() == null
                || clienteDAO.buscarPorId(prestamo.getCliente().getId()).isEmpty()) {

            throw new PrestamoException(
                    "El préstamo debe tener un cliente existente."
            );
        }

        if (prestamo.getEmpleado() == null
                || empleadoDAO.buscarPorId(prestamo.getEmpleado().getId()).isEmpty()) {

            throw new PrestamoException(
                    "El préstamo debe tener un empleado existente."
            );
        }

        if (!Validador.decimalPositivo(prestamo.getMonto())
                || prestamo.getMonto() > Constantes.MONTO_PRESTAMO_MAX) {

            throw new PrestamoException(
                    "El monto debe ser mayor que cero y no superar "
                    + Formato.moneda(Constantes.MONTO_PRESTAMO_MAX) + "."
            );
        }

        if (!Validador.interesValido(prestamo.getInteres())) {

            throw new PrestamoException(
                    "El interés debe estar entre 0% y " + (int) Constantes.INTERES_MAX + "%."
            );
        }

        if (!Validador.cuotasValidas(prestamo.getCuotas())) {

            throw new PrestamoException(
                    "Las cuotas deben estar entre 1 y " + Constantes.CUOTAS_MAX + "."
            );
        }

        if (prestamo.getFechaInicio() == null) {

            throw new PrestamoException(
                    "La fecha de inicio es obligatoria."
            );
        }

        double monto = Formato.redondear(prestamo.getMonto());

        double interes = prestamo.getInteres();

        int cuotas = prestamo.getCuotas();

        double valorInteres =
                monto * (interes / 100);

        double montoTotal =
                Formato.redondear(monto + valorInteres);

        double cuotaMensual =
                Formato.redondear(montoTotal / cuotas);

        prestamo.setMonto(monto);

        prestamo.setMontoTotal(montoTotal);

        prestamo.setCuotaMensual(cuotaMensual);

        prestamo.setSaldoPendiente(montoTotal);

        prestamo.setEstado(Constantes.ESTADO_PENDIENTE);

        prestamoDAO.guardar(prestamo);

        System.out.println("Préstamo guardado correctamente con ID " + prestamo.getId() + ".");
    }

    // =========================================================
    // LISTAR PRÉSTAMOS
    // =========================================================

    public void listarPrestamos() {

        List<Prestamo> prestamos = prestamoDAO.listar();

        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos registrados.");
            return;
        }

        for (Prestamo prestamo : prestamos) {

            System.out.println(prestamo);
        }
    }

    public List<Prestamo> obtenerPrestamos() {
        return prestamoDAO.listar();
    }

    public Optional<Prestamo> buscarPorId(int id) {
        return prestamoDAO.buscarPorId(id);
    }

    /** Préstamos asociados a un cliente (Stream API + lambda). */
    public List<Prestamo> prestamosDeCliente(int clienteId) {

        return prestamoDAO.listar().stream()
                .filter(p -> p.getCliente().getId() == clienteId)
                .collect(Collectors.toList());
    }

    // =========================================================
    // CAMBIAR ESTADO
    // =========================================================

    /**
     * Cambia el estado del préstamo validando que sea coherente con el saldo:
     * solo puede quedar PAGADO si no debe nada y PENDIENTE si todavía debe.
     */
    public void cambiarEstado(int prestamoId, String nuevoEstado)
            throws PrestamoException {

        Prestamo prestamo = buscarPrestamo(prestamoId);

        if (prestamo == null) {
            throw new PrestamoException("El préstamo no existe.");
        }

        if (!Constantes.ESTADO_PENDIENTE.equals(nuevoEstado)
                && !Constantes.ESTADO_PAGADO.equals(nuevoEstado)) {

            throw new PrestamoException(
                    "Estado inválido. Use PENDIENTE o PAGADO."
            );
        }

        if (nuevoEstado.equals(prestamo.getEstado())) {

            throw new PrestamoException(
                    "El préstamo ya está en estado " + nuevoEstado + "."
            );
        }

        if (Constantes.ESTADO_PAGADO.equals(nuevoEstado)
                && prestamo.getSaldoPendiente() > 0) {

            throw new PrestamoException(
                    "No se puede marcar como PAGADO: todavía debe "
                    + Formato.moneda(prestamo.getSaldoPendiente())
                    + ". Registre un pago por ese valor."
            );
        }

        if (Constantes.ESTADO_PENDIENTE.equals(nuevoEstado)
                && prestamo.getSaldoPendiente() <= 0) {

            throw new PrestamoException(
                    "No se puede marcar como PENDIENTE: el préstamo no tiene saldo."
            );
        }

        prestamoDAO.actualizarSaldoYEstado(
                prestamoId,
                prestamo.getSaldoPendiente(),
                nuevoEstado
        );

        System.out.println(
                "Estado del préstamo #" + prestamoId + " cambiado a " + nuevoEstado + "."
        );
    }

    // =========================================================
    // REFINANCIAR PRÉSTAMO
    // =========================================================

    public void refinanciarPrestamo(
            int prestamoId,
            int nuevasCuotas)
            throws PrestamoException {

        // Buscar el préstamo

        Prestamo prestamo =
                buscarPrestamo(prestamoId);

        if (prestamo == null) {

            throw new PrestamoException(
                    "El préstamo no existe."
            );
        }

        // Verificar si está pagado

        if (prestamo.getEstado()
                .equalsIgnoreCase(Constantes.ESTADO_PAGADO)) {

            throw new PrestamoException(
                    "No se puede refinanciar "
                    + "un préstamo que ya está pagado."
            );
        }

        // Validar nuevas cuotas

        if (nuevasCuotas <= 0) {

            throw new PrestamoException(
                    "Las nuevas cuotas deben "
                    + "ser mayores que cero."
            );
        }

        // Solo se permite ampliar el plazo

        if (nuevasCuotas <= prestamo.getCuotas()) {

            throw new PrestamoException(
                    "Las nuevas cuotas deben ser "
                    + "mayores que las cuotas actuales ("
                    + prestamo.getCuotas() + ")."
            );
        }

        if (nuevasCuotas > Constantes.CUOTAS_MAX) {

            throw new PrestamoException(
                    "Las cuotas no pueden superar " + Constantes.CUOTAS_MAX + "."
            );
        }

        // ==============================================
        // CALCULAR NUEVA DEUDA
        // ==============================================

        double saldoPendiente =
                prestamo.getSaldoPendiente();

        double interes =
                prestamo.getInteres();

        double valorInteres =
                saldoPendiente * (interes / 100);

        double nuevoMontoTotal =
                Formato.redondear(saldoPendiente + valorInteres);

        double nuevaCuotaMensual =
                Formato.redondear(nuevoMontoTotal / nuevasCuotas);

        // ==============================================
        // GUARDAR CAMBIOS
        // ==============================================

        prestamoDAO.refinanciar(
                prestamoId,
                nuevasCuotas,
                nuevoMontoTotal,
                nuevaCuotaMensual,
                nuevoMontoTotal
        );

        // ==============================================
        // MOSTRAR RESULTADO
        // ==============================================

        System.out.println(
                "\n===== REFINANCIAMIENTO ====="
        );

        System.out.println(
                "Préstamo: #" + prestamoId
        );

        System.out.println(
                "Saldo anterior: "
                + Formato.moneda(saldoPendiente)
        );

        System.out.println(
                "Interés aplicado: "
                + interes + "%"
        );

        System.out.println(
                "Nuevo monto total: "
                + Formato.moneda(nuevoMontoTotal)
        );

        System.out.println(
                "Nuevas cuotas: "
                + nuevasCuotas
        );

        System.out.println(
                "Nueva cuota mensual: "
                + Formato.moneda(nuevaCuotaMensual)
        );

        System.out.println(
                "Nuevo saldo pendiente: "
                + Formato.moneda(nuevoMontoTotal)
        );

        System.out.println(
                "Estado: " + Constantes.ESTADO_PENDIENTE
        );
    }

    // =========================================================
    // BUSCAR PRÉSTAMO
    // =========================================================

    private Prestamo buscarPrestamo(int id) {

        return prestamoDAO.buscarPorId(id).orElse(null);
    }
}
