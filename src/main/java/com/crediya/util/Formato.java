package com.crediya.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Utilidades para mostrar y redondear valores de dinero.
 * Evita que en pantalla salgan cosas como "1.0E7".
 */
public final class Formato {

    private static final DecimalFormat FORMATO_MONEDA;

    static {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        FORMATO_MONEDA = new DecimalFormat("$#,##0.00", simbolos);
    }

    private Formato() {
    }

    /** Ej: 1500000.5 -> "$1.500.000,50" */
    public static String moneda(double valor) {
        return FORMATO_MONEDA.format(valor);
    }

    /** Número sin notación científica para archivos: 10000000 -> "10000000.00" */
    public static String plano(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    /** Redondea a 2 decimales (centavos). */
    public static double redondear(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
