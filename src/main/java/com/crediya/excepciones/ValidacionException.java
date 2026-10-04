package com.crediya.excepciones;

/**
 * Se lanza cuando un dato ingresado no cumple las reglas del negocio
 * (formato, rango, duplicados, etc.).
 */
public class ValidacionException extends CrediyaException {

    private static final long serialVersionUID = 1L;

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
