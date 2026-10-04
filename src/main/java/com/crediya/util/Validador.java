package com.crediya.util;

import java.util.regex.Pattern;

/**
 * Reglas de validación de los campos del sistema.
 * Todas las validaciones están centralizadas aquí (principio de
 * responsabilidad única) y se usan tanto en la consola como en los servicios.
 */
public class Validador {

    // Letras (con tildes y ñ) separadas por un solo espacio. Sin números ni símbolos.
    private static final Pattern PATRON_NOMBRE =
            Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü]+( [A-Za-zÁÉÍÓÚáéíóúÑñÜü]+)*$");

    // Solo dígitos, entre 6 y 10 (cédula colombiana).
    private static final Pattern PATRON_DOCUMENTO =
            Pattern.compile("^\\d{" + Constantes.DOCUMENTO_MIN + "," + Constantes.DOCUMENTO_MAX + "}$");

    // usuario@dominio.ext  (acepta .com, .co, .com.co, .edu.co, etc.)
    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[A-Za-z0-9]+([._%+-][A-Za-z0-9]+)*@[A-Za-z0-9]+(-[A-Za-z0-9]+)*(\\.[A-Za-z0-9]+(-[A-Za-z0-9]+)*)*\\.[A-Za-z]{2,6}$");

    // 10 dígitos: celular (empieza por 3) o fijo nacional (empieza por 6).
    private static final Pattern PATRON_TELEFONO =
            Pattern.compile("^[36]\\d{9}$");

    // Número entero sin signos ni letras.
    private static final Pattern PATRON_ENTERO =
            Pattern.compile("^\\d{1,9}$");

    // Número con máximo 2 decimales, separador "." o ",". Sin puntos de miles.
    private static final Pattern PATRON_DECIMAL =
            Pattern.compile("^\\d{1,13}([.,]\\d{1,2})?$");

    private Validador() {
    }

    // ==============================
    // VALIDAR TEXTO
    // ==============================

    public static boolean textoValido(String texto) {

        return texto != null && !texto.isBlank();
    }

    // ==============================
    // VALIDAR NOMBRE
    // ==============================

    public static boolean nombreValido(String nombre) {

        if (!textoValido(nombre)) {
            return false;
        }

        String limpio = normalizarEspacios(nombre);

        return limpio.length() >= Constantes.NOMBRE_MIN
                && limpio.length() <= Constantes.NOMBRE_MAX
                && PATRON_NOMBRE.matcher(limpio).matches();
    }

    // ==============================
    // VALIDAR DOCUMENTO
    // ==============================

    public static boolean documentoValido(String documento) {

        return documento != null
                && PATRON_DOCUMENTO.matcher(documento.trim()).matches();
    }

    // ==============================
    // VALIDAR CORREO
    // ==============================

    public static boolean correoValido(String correo) {

        if (correo == null || correo.isBlank()) {
            return false;
        }

        String limpio = correo.trim();

        return limpio.length() <= Constantes.CORREO_MAX
                && PATRON_CORREO.matcher(limpio).matches();
    }

    // ==============================
    // VALIDAR NÚMERO ENTERO
    // ==============================

    public static boolean enteroPositivo(int numero) {

        return numero > 0;
    }

    /** Verifica que el texto sea un número entero (sin letras, signos ni decimales). */
    public static boolean formatoEntero(String texto) {

        return texto != null && PATRON_ENTERO.matcher(texto.trim()).matches();
    }

    // ==============================
    // VALIDAR NÚMERO DECIMAL
    // ==============================

    public static boolean decimalPositivo(double numero) {

        return numero > 0 && !Double.isInfinite(numero) && !Double.isNaN(numero);
    }

    /** Verifica que el texto sea un número con máximo 2 decimales. */
    public static boolean formatoDecimal(String texto) {

        return texto != null && PATRON_DECIMAL.matcher(texto.trim()).matches();
    }

    public static boolean enRango(double numero, double minimo, double maximo) {

        return numero >= minimo && numero <= maximo;
    }

    // ==============================
    // VALIDAR INTERÉS
    // ==============================

    public static boolean interesValido(double interes) {

        return interes >= 0 && interes <= Constantes.INTERES_MAX;
    }

    // ==============================
    // VALIDAR CUOTAS
    // ==============================

    public static boolean cuotasValidas(int cuotas) {

        return cuotas >= 1 && cuotas <= Constantes.CUOTAS_MAX;
    }

    // ==============================
    // VALIDAR SALARIO
    // ==============================

    public static boolean salarioValido(double salario) {

        return decimalPositivo(salario) && salario <= Constantes.SALARIO_MAX;
    }

    // ==============================
    // VALIDAR ROL
    // ==============================

    public static boolean rolValido(String rol) {

        return rol != null && Constantes.ROLES.contains(rol.trim().toUpperCase());
    }

    // ==============================
    // VALIDAR TELÉFONO
    // ==============================

    public static boolean telefonoValido(String telefono) {

        if (telefono == null || telefono.isBlank()) {
            return false;
        }

        return PATRON_TELEFONO.matcher(telefono.trim()).matches();
    }

    // ==============================
    // NORMALIZAR
    // ==============================

    /** Quita espacios al inicio/final y deja un solo espacio entre palabras. */
    public static String normalizarEspacios(String texto) {

        return texto == null ? null : texto.trim().replaceAll("\\s+", " ");
    }

    /** Convierte "jUAN  pérez" en "Juan Pérez". */
    public static String formatearNombre(String nombre) {

        String limpio = normalizarEspacios(nombre);

        if (limpio == null || limpio.isEmpty()) {
            return limpio;
        }

        StringBuilder resultado = new StringBuilder();

        for (String palabra : limpio.split(" ")) {

            if (resultado.length() > 0) {
                resultado.append(' ');
            }

            resultado.append(Character.toUpperCase(palabra.charAt(0)))
                     .append(palabra.substring(1).toLowerCase());
        }

        return resultado.toString();
    }

    /** Convierte texto numérico ("1500,5") a double. */
    public static double convertirDecimal(String texto) {

        return Double.parseDouble(texto.trim().replace(',', '.'));
    }
}
