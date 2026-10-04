package com.crediya.util;

import java.util.List;

public class Constantes {

    public static final String ESTADO_PENDIENTE = "PENDIENTE";

    public static final String ESTADO_PAGADO = "PAGADO";

    public static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";

    public static final String ROL_ASESOR = "ASESOR";

    public static final String ROL_COBRADOR = "COBRADOR";

    /** Roles permitidos para un empleado. */
    public static final List<String> ROLES =
            List.of(ROL_ADMINISTRADOR, ROL_ASESOR, ROL_COBRADOR);

    // ---------------- Límites de los campos ----------------
    // (coinciden con los tamaños de las columnas en MySQL)

    public static final int NOMBRE_MIN = 3;
    public static final int NOMBRE_MAX = 80;

    public static final int DOCUMENTO_MIN = 6;
    public static final int DOCUMENTO_MAX = 10;

    public static final int CORREO_MAX = 80;

    public static final double SALARIO_MAX = 99_999_999.99;          // DECIMAL(10,2)

    public static final double MONTO_PRESTAMO_MAX = 1_000_000_000;   // mil millones

    public static final double INTERES_MAX = 100;                    // porcentaje

    public static final int CUOTAS_MAX = 120;                        // 10 años

    private Constantes() {
    }
}
