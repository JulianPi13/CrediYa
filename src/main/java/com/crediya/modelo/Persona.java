package com.crediya.modelo;

/**
 * Clase base abstracta con los datos comunes de clientes y empleados.
 * Aplica herencia (Cliente y Empleado extienden de Persona),
 * encapsulamiento (atributos privados con getters/setters)
 * y polimorfismo (getTipo() y toString() se redefinen en cada subclase).
 */
public abstract class Persona implements Registrable {

    private int id;
    private String nombre;
    private String documento;
    private String correo;

    protected Persona() {
    }

    protected Persona(int id, String nombre, String documento, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.correo = correo;
    }

    /** Tipo de persona ("Cliente", "Empleado"...). */
    public abstract String getTipo();

    /** Descripción corta reutilizable en listados y reportes. */
    public String descripcionCorta() {
        return getTipo() + " #" + id + " - " + nombre + " (Doc. " + documento + ")";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
