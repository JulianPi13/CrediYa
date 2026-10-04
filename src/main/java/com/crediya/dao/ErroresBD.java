package com.crediya.dao;

import com.crediya.excepciones.PersistenciaException;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Traduce los errores técnicos de JDBC a mensajes entendibles.
 */
final class ErroresBD {

    private ErroresBD() {
    }

    static PersistenciaException traducir(String accion, SQLException e) {

        String mensaje;

        if (e instanceof SQLIntegrityConstraintViolationException) {

            mensaje = "No se pudo " + accion
                    + ": ya existe un registro con esos datos o hay una referencia inválida.";

        } else if (e.getSQLState() != null && e.getSQLState().startsWith("08")) {

            mensaje = "No se pudo conectar a MySQL. Verifique que el servidor esté encendido "
                    + "y que la base de datos crediya_db exista.";

        } else {

            mensaje = "Error al " + accion + ": " + e.getMessage();
        }

        return new PersistenciaException(mensaje, e);
    }
}
