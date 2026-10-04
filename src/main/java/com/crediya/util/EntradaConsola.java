package com.crediya.util;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Lee datos desde la consola y repite la pregunta hasta que el usuario
 * escriba un valor válido. Así ningún campo acepta datos que no corresponden
 * (letras en números, números en nombres, correos mal escritos, etc.).
 */
public class EntradaConsola {

    private final Scanner scanner;

    public EntradaConsola(Scanner scanner) {
        this.scanner = scanner;
    }

    // =========================================================
    // LECTURA BÁSICA
    // =========================================================

    private String leerLinea(String mensaje) {

        System.out.print(mensaje);

        try {
            return scanner.nextLine().trim();
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("Se cerró la entrada de datos.");
        }
    }

    // =========================================================
    // TEXTO
    // =========================================================

    public String leerNombre(String mensaje) {

        while (true) {

            String nombre = leerLinea(mensaje);

            if (Validador.nombreValido(nombre)) {
                return Validador.formatearNombre(nombre);
            }

            System.out.println(
                    "Nombre inválido. Use solo letras y espacios (entre "
                    + Constantes.NOMBRE_MIN + " y " + Constantes.NOMBRE_MAX
                    + " caracteres), sin números ni símbolos."
            );
        }
    }

    /**
     * Lee un documento y además verifica una condición extra
     * (por ejemplo que no exista ya en la base de datos).
     */
    public String leerDocumento(String mensaje,
                                Predicate<String> condicionExtra,
                                String mensajeCondicion) {

        while (true) {

            String documento = leerLinea(mensaje);

            if (!Validador.documentoValido(documento)) {

                System.out.println(
                        "Documento inválido. Debe tener solo números, entre "
                        + Constantes.DOCUMENTO_MIN + " y " + Constantes.DOCUMENTO_MAX
                        + " dígitos, sin puntos ni espacios."
                );

                continue;
            }

            if (condicionExtra != null && !condicionExtra.test(documento)) {

                System.out.println(mensajeCondicion);
                continue;
            }

            return documento;
        }
    }

    public String leerCorreo(String mensaje,
                             Predicate<String> condicionExtra,
                             String mensajeCondicion) {

        while (true) {

            String correo = leerLinea(mensaje).toLowerCase();

            if (!Validador.correoValido(correo)) {

                System.out.println(
                        "Correo inválido. Debe tener formato "
                        + "usuario@dominio.com (máx. " + Constantes.CORREO_MAX
                        + " caracteres, sin espacios)."
                );

                continue;
            }

            if (condicionExtra != null && !condicionExtra.test(correo)) {

                System.out.println(mensajeCondicion);
                continue;
            }

            return correo;
        }
    }

    public String leerTelefono(String mensaje) {

        while (true) {

            String telefono = leerLinea(mensaje);

            if (Validador.telefonoValido(telefono)) {
                return telefono;
            }

            System.out.println(
                    "Teléfono inválido. Debe tener exactamente 10 números "
                    + "y empezar por 3 (celular) o 6 (fijo). Ej: 3001234567"
            );
        }
    }

    /** Muestra una lista numerada y devuelve la opción elegida. */
    public String elegirDeLista(String titulo, List<String> opciones) {

        System.out.println(titulo);

        for (int i = 0; i < opciones.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + opciones.get(i));
        }

        int opcion = leerEntero("Seleccione una opción: ", 1, opciones.size());

        return opciones.get(opcion - 1);
    }

    // =========================================================
    // NÚMEROS
    // =========================================================

    /** Lee un entero entre minimo y maximo (ambos incluidos). */
    public int leerEntero(String mensaje, int minimo, int maximo) {

        while (true) {

            String texto = leerLinea(mensaje);

            if (!Validador.formatoEntero(texto)) {

                System.out.println(
                        "Ingrese un número entero válido (solo dígitos, sin letras, "
                        + "signos ni decimales)."
                );

                continue;
            }

            int numero = Integer.parseInt(texto);

            if (numero < minimo || numero > maximo) {

                System.out.println(
                        "El número debe estar entre " + minimo + " y " + maximo + "."
                );

                continue;
            }

            return numero;
        }
    }

    /** Lee un entero mayor que cero (para IDs). */
    public int leerEnteroPositivo(String mensaje) {

        return leerEntero(mensaje, 1, Integer.MAX_VALUE);
    }

    /** Lee un decimal (máx. 2 decimales) entre minimo y maximo. */
    public double leerDecimal(String mensaje, double minimo, double maximo) {

        while (true) {

            String texto = leerLinea(mensaje);

            if (!Validador.formatoDecimal(texto)) {

                System.out.println(
                        "Valor inválido. Escriba solo números, sin puntos de miles "
                        + "ni símbolos (ej: 1500000 o 1500000.50)."
                );

                continue;
            }

            double numero = Validador.convertirDecimal(texto);

            if (!Validador.enRango(numero, minimo, maximo)) {

                System.out.println(
                        "El valor debe estar entre "
                        + Formato.moneda(minimo).substring(1) + " y "
                        + Formato.moneda(maximo).substring(1) + "."
                );

                continue;
            }

            return numero;
        }
    }

    /** Lee "S" o "N". */
    public boolean leerConfirmacion(String mensaje) {

        while (true) {

            String respuesta = leerLinea(mensaje + " (S/N): ").toUpperCase();

            if (respuesta.equals("S") || respuesta.equals("SI") || respuesta.equals("SÍ")) {
                return true;
            }

            if (respuesta.equals("N") || respuesta.equals("NO")) {
                return false;
            }

            System.out.println("Responda S (sí) o N (no).");
        }
    }
}
