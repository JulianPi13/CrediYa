package com.crediya.servicio;

import com.crediya.dao.PagoDAO;
import com.crediya.dao.PrestamoDAO;
import com.crediya.excepciones.PrestamoException;
import com.crediya.modelo.Pago;
import com.crediya.modelo.Prestamo;
import com.crediya.util.Constantes;
import com.crediya.util.Formato;

import java.time.LocalDate;
import java.util.List;

public class PagoServicio {

    private final PagoDAO pagoDAO;
    private final PrestamoDAO prestamoDAO;

    public PagoServicio() {
        this(new PagoDAO(), new PrestamoDAO());
    }

    /** Permite inyectar otros DAO (por ejemplo, en memoria para pruebas). */
    public PagoServicio(PagoDAO pagoDAO, PrestamoDAO prestamoDAO) {
        this.pagoDAO = pagoDAO;
        this.prestamoDAO = prestamoDAO;
    }

    public void registrarPago(Pago pago)
            throws PrestamoException {

        // ==========================================
        // VALIDACIONES BÁSICAS
        // ==========================================

        if (pago == null) {
            throw new PrestamoException(
                    "El pago no puede ser nulo."
            );
        }

        if (pago.getPrestamo() == null) {
            throw new PrestamoException(
                    "El pago debe estar asociado a un préstamo."
            );
        }

        if (pago.getMonto() <= 0
                || Double.isNaN(pago.getMonto())
                || Double.isInfinite(pago.getMonto())) {
            throw new PrestamoException(
                    "El monto del pago debe ser mayor que cero."
            );
        }

        if (pago.getFechaPago() == null) {
            throw new PrestamoException(
                    "La fecha del pago es obligatoria."
            );
        }

        if (pago.getFechaPago().isAfter(LocalDate.now())) {
            throw new PrestamoException(
                    "La fecha del pago no puede ser futura."
            );
        }

        // ==========================================
        // BUSCAR PRÉSTAMO
        // ==========================================

        Prestamo prestamo = buscarPrestamo(
                pago.getPrestamo().getId()
        );

        if (prestamo == null) {
            throw new PrestamoException(
                    "El préstamo no existe."
            );
        }

        // ==========================================
        // VERIFICAR SI YA ESTÁ PAGADO
        // ==========================================

        if (prestamo.getEstado()
                .equalsIgnoreCase(Constantes.ESTADO_PAGADO)) {

            throw new PrestamoException(
                    "El préstamo ya está completamente pagado."
            );
        }

        double saldoPendiente =
                Formato.redondear(prestamo.getSaldoPendiente());

        double cuotaMensual =
                Formato.redondear(prestamo.getCuotaMensual());

        double montoPago =
                Formato.redondear(pago.getMonto());

        pago.setMonto(montoPago);

        // ==========================================
        // NO PERMITIR PAGAR MÁS DEL SALDO
        // ==========================================

        if (montoPago > saldoPendiente) {

            throw new PrestamoException(
                    "El pago no puede ser mayor "
                    + "al saldo pendiente ("
                    + Formato.moneda(saldoPendiente) + ")."
            );
        }

        // ==========================================
        // VALIDAR PAGO MÍNIMO
        // ==========================================

        /*
         * Si todavía falta más de una cuota,
         * el pago debe ser como mínimo
         * el valor de la cuota mensual.
         *
         * Si el saldo pendiente es menor
         * que la cuota, significa que estamos
         * en el último pago y se permite pagar
         * exactamente el saldo restante.
         */

        if (saldoPendiente > cuotaMensual
                && montoPago < cuotaMensual) {

            throw new PrestamoException(
                    "El pago mínimo es de "
                    + Formato.moneda(cuotaMensual)
            );
        }

        if (saldoPendiente <= cuotaMensual
                && montoPago < saldoPendiente) {

            throw new PrestamoException(
                    "Es la última cuota: debe pagar el saldo completo de "
                    + Formato.moneda(saldoPendiente)
            );
        }

        // ==========================================
        // CALCULAR NUEVO SALDO
        // ==========================================

        double nuevoSaldo =
                Formato.redondear(saldoPendiente - montoPago);

        // Evitamos pequeños errores decimales.

        if (nuevoSaldo < 0.01) {
            nuevoSaldo = 0;
        }

        // ==========================================
        // ACTUALIZAR ESTADO
        // ==========================================

        if (nuevoSaldo == 0) {

            prestamo.setEstado(Constantes.ESTADO_PAGADO);

        } else {

            prestamo.setEstado(Constantes.ESTADO_PENDIENTE);
        }

        prestamo.setSaldoPendiente(nuevoSaldo);

        // ==========================================
        // GUARDAR PAGO Y ACTUALIZAR PRÉSTAMO
        // (en una sola transacción)
        // ==========================================

        pagoDAO.guardarYActualizarPrestamo(
                pago,
                prestamo.getSaldoPendiente(),
                prestamo.getEstado()
        );

        // ==========================================
        // MOSTRAR RESULTADO
        // ==========================================

        System.out.println(
                "Pago registrado correctamente (ID " + pago.getId() + ")."
        );

        System.out.println(
                "Pago realizado: " + Formato.moneda(montoPago)
        );

        System.out.println(
                "Nuevo saldo: " + Formato.moneda(nuevoSaldo)
        );

        System.out.println(
                "Estado: " + prestamo.getEstado()
        );
    }

    // ==============================================
    // BUSCAR PRÉSTAMO
    // ==============================================

    private Prestamo buscarPrestamo(int id) {

        return prestamoDAO.buscarPorId(id).orElse(null);
    }

    // ==============================================
    // LISTAR PAGOS
    // ==============================================

    public void listarPagos() {

        List<Pago> pagos =
                pagoDAO.listar();

        if (pagos.isEmpty()) {

            System.out.println(
                    "No hay pagos registrados."
            );

            return;
        }

        for (Pago pago : pagos) {

            System.out.println(pago);
        }
    }

    public List<Pago> obtenerPagos() {
        return pagoDAO.listar();
    }

    // ==============================================
    // HISTORIAL POR PRÉSTAMO
    // ==============================================

    public void mostrarHistorialPorPrestamo(
            int prestamoId) throws PrestamoException {

        Prestamo prestamo = buscarPrestamo(prestamoId);

        if (prestamo == null) {
            throw new PrestamoException("El préstamo no existe.");
        }

        List<Pago> pagos =
                pagoDAO.listarPorPrestamo(
                        prestamoId
                );

        if (pagos.isEmpty()) {

            System.out.println(
                    "Este préstamo no tiene pagos registrados."
            );

            return;
        }

        System.out.println(
                "\n===== HISTORIAL DEL PRÉSTAMO #" + prestamoId + " ====="
        );

        System.out.println(
                "Cliente: " + prestamo.getCliente().getNombre()
        );

        System.out.println(
                "--------------------------------"
        );

        for (Pago pago : pagos) {

            System.out.println(
                    "ID pago: "
                    + pago.getId()
            );

            System.out.println(
                    "Fecha: "
                    + pago.getFechaPago()
            );

            System.out.println(
                    "Monto: "
                    + Formato.moneda(pago.getMonto())
            );

            System.out.println(
                    "--------------------------------"
            );
        }

        double totalPagado = pagos.stream()
                .mapToDouble(Pago::getMonto)
                .sum();

        System.out.println(
                "TOTAL PAGADO: " + Formato.moneda(totalPagado)
        );

        System.out.println(
                "SALDO PENDIENTE: " + Formato.moneda(prestamo.getSaldoPendiente())
        );
    }
}
