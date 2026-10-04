package com.crediya.modelo;

import com.crediya.util.Formato;

import java.time.LocalDate;

public class Prestamo implements Registrable {

    private int id;
    private Cliente cliente;
    private Empleado empleado;
    private double monto;
    private double interes;
    private int cuotas;
    private double cuotaMensual;
    private LocalDate fechaInicio;
    private String estado;
    private double montoTotal;
    private double saldoPendiente;

    public Prestamo() {
    }

    public Prestamo(int id, Cliente cliente, Empleado empleado,
                    double monto, double interes, int cuotas,
                    double cuotaMensual, LocalDate fechaInicio,
                    String estado, double montoTotal,
                    double saldoPendiente) {

        this.id = id;
        this.cliente = cliente;
        this.empleado = empleado;
        this.monto = monto;
        this.interes = interes;
        this.cuotas = cuotas;
        this.cuotaMensual = cuotaMensual;
        this.fechaInicio = fechaInicio;
        this.estado = estado;
        this.montoTotal = montoTotal;
        this.saldoPendiente = saldoPendiente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getInteres() {
        return interes;
    }

    public void setInteres(double interes) {
        this.interes = interes;
    }

    public int getCuotas() {
        return cuotas;
    }

    public void setCuotas(int cuotas) {
        this.cuotas = cuotas;
    }

    public double getCuotaMensual() {
        return cuotaMensual;
    }

    public void setCuotaMensual(double cuotaMensual) {
        this.cuotaMensual = cuotaMensual;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public double getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    /** Fecha en la que debería estar pagada la última cuota. */
    public LocalDate getFechaVencimiento() {
        return fechaInicio == null ? null : fechaInicio.plusMonths(cuotas);
    }

    /** Un préstamo está vencido si sigue con saldo después de su fecha de vencimiento. */
    public boolean estaVencido(LocalDate hoy) {
        return com.crediya.util.Constantes.ESTADO_PENDIENTE.equals(estado)
                && saldoPendiente > 0
                && fechaInicio != null
                && hoy.isAfter(getFechaVencimiento());
    }

    @Override
    public String aLineaArchivo() {
        return id + SEPARADOR
                + (cliente != null ? cliente.getId() : "") + SEPARADOR
                + (empleado != null ? empleado.getId() : "") + SEPARADOR
                + Formato.plano(monto) + SEPARADOR
                + Formato.plano(interes) + SEPARADOR
                + cuotas + SEPARADOR
                + Formato.plano(cuotaMensual) + SEPARADOR
                + fechaInicio + SEPARADOR
                + estado + SEPARADOR
                + Formato.plano(montoTotal) + SEPARADOR
                + Formato.plano(saldoPendiente);
    }

    @Override
    public String toString() {
        return "Prestamo{"
                + "id=" + id
                + ", cliente=" + (cliente != null ? cliente.getNombre() + " (#" + cliente.getId() + ")" : "-")
                + ", empleado=" + (empleado != null ? empleado.getNombre() + " (#" + empleado.getId() + ")" : "-")
                + ", monto=" + Formato.moneda(monto)
                + ", interes=" + interes + "%"
                + ", cuotas=" + cuotas
                + ", cuotaMensual=" + Formato.moneda(cuotaMensual)
                + ", fechaInicio=" + fechaInicio
                + ", estado='" + estado + '\''
                + ", montoTotal=" + Formato.moneda(montoTotal)
                + ", saldoPendiente=" + Formato.moneda(saldoPendiente)
                + '}';
    }
}
