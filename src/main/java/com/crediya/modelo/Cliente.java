package com.crediya.modelo;

public class Cliente extends Persona {

    private String telefono;

    public Cliente() {
    }

    public Cliente(int id, String nombre, String documento, String correo, String telefono) {
        super(id, nombre, documento, correo);
        this.telefono = telefono;
    }

    @Override
    public String getTipo() {
        return "Cliente";
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String aLineaArchivo() {
        return getId() + SEPARADOR
                + getNombre() + SEPARADOR
                + getDocumento() + SEPARADOR
                + getCorreo() + SEPARADOR
                + telefono;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", documento='" + getDocumento() + '\'' +
                ", correo='" + getCorreo() + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
