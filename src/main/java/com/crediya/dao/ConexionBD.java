package com.crediya.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fábrica de conexiones JDBC a MySQL.
 * Los datos de conexión se leen de src/main/resources/db.properties;
 * si el archivo no existe se usan los valores por defecto.
 */
public class ConexionBD {

    private static final String URL_DEFECTO = "jdbc:mysql://localhost:3306/crediya_db";
    private static final String USUARIO_DEFECTO = "root";
    private static final String PASSWORD_DEFECTO = "123abcdE*";

    private static final String URL;
    private static final String USUARIO;
    private static final String PASSWORD;

    static {
        Properties propiedades = new Properties();

        try (InputStream entrada =
                     ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {

            if (entrada != null) {
                propiedades.load(entrada);
            }

        } catch (IOException e) {
            System.out.println("No se pudo leer db.properties, se usan valores por defecto.");
        }

        URL = propiedades.getProperty("db.url", URL_DEFECTO);
        USUARIO = propiedades.getProperty("db.usuario", USUARIO_DEFECTO);
        PASSWORD = propiedades.getProperty("db.password", PASSWORD_DEFECTO);
    }

    private ConexionBD() {
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
