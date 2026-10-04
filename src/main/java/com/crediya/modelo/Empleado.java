package com.crediya.modelo;

import com.crediya.util.Formato;

public class Empleado extends Persona {

    private String rol;
    private double salario;

    public Empleado() {
    }

    public Empleado(int id, String nombre, String documento, String rol, String correo, double salario) {
        super(id, nombre, documento, correo);
        this.rol = rol;
        this.salario = salario;
    }

    @Override
    public String getTipo() {
        return "Empleado";
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    @Override
    public String aLineaArchivo() {
        return getId() + SEPARADOR
                + getNombre() + SEPARADOR
                + getDocumento() + SEPARADOR
                + rol + SEPARADOR
                + getCorreo() + SEPARADOR
                + Formato.plano(salario);
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", documento='" + getDocumento() + '\'' +
                ", rol='" + rol + '\'' +
                ", correo='" + getCorreo() + '\'' +
                ", salario=" + Formato.moneda(salario) +
                '}';
    }
}
