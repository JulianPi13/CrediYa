package com.crediya.excepciones;

/**
 * Error al leer o escribir en la base de datos.
 * Es una excepción no verificada para no obligar a declarar
 * "throws" en cada consulta; el menú principal la captura.
 */
public class PersistenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
