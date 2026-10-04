package com.crediya.excepciones;

public class CrediyaException extends Exception {

    private static final long serialVersionUID = 1L;

    public CrediyaException(String mensaje) {
        super(mensaje);
    }
}