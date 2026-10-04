package com.crediya.archivo;

import com.crediya.modelo.Cliente;
import com.crediya.modelo.Empleado;
import com.crediya.modelo.Pago;
import com.crediya.modelo.Prestamo;
import com.crediya.modelo.Registrable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Guarda y lee la información en archivos de texto (carpeta "archivos").
 * Cada línea es un registro con los campos separados por ";".
 * La primera línea (que empieza con "#") indica el nombre de las columnas.
 */
public class GestorArchivos {

    private static final String CARPETA = "archivos";

    private static final String ARCHIVO_EMPLEADOS =
            CARPETA + File.separator + "empleados.txt";

    private static final String ARCHIVO_CLIENTES =
            CARPETA + File.separator + "clientes.txt";

    private static final String ARCHIVO_PRESTAMOS =
            CARPETA + File.separator + "prestamos.txt";

    private static final String ARCHIVO_PAGOS =
            CARPETA + File.separator + "pagos.txt";

    private static final String ENCABEZADO_EMPLEADOS =
            "# id;nombre;documento;rol;correo;salario";

    private static final String ENCABEZADO_CLIENTES =
            "# id;nombre;documento;correo;telefono";

    private static final String ENCABEZADO_PRESTAMOS =
            "# id;cliente_id;empleado_id;monto;interes;cuotas;cuota_mensual;"
            + "fecha_inicio;estado;monto_total;saldo_pendiente";

    private static final String ENCABEZADO_PAGOS =
            "# id;prestamo_id;fecha_pago;monto";

    public GestorArchivos() {
        crearCarpeta();
    }

    private void crearCarpeta() {

        File carpeta = new File(CARPETA);

        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }

    // =========================================================
    // GUARDAR
    // =========================================================

    public void guardarEmpleados(List<Empleado> empleados) throws IOException {
        guardar(empleados, ARCHIVO_EMPLEADOS, ENCABEZADO_EMPLEADOS);
    }

    public void guardarClientes(List<Cliente> clientes) throws IOException {
        guardar(clientes, ARCHIVO_CLIENTES, ENCABEZADO_CLIENTES);
    }

    public void guardarPrestamos(List<Prestamo> prestamos) throws IOException {
        guardar(prestamos, ARCHIVO_PRESTAMOS, ENCABEZADO_PRESTAMOS);
    }

    public void guardarPagos(List<Pago> pagos) throws IOException {
        guardar(pagos, ARCHIVO_PAGOS, ENCABEZADO_PAGOS);
    }

    /**
     * Método genérico: sirve para cualquier entidad Registrable
     * (polimorfismo: cada clase sabe cómo convertirse en línea).
     */
    private void guardar(List<? extends Registrable> registros,
                         String ruta,
                         String encabezado) throws IOException {

        crearCarpeta();

        try (BufferedWriter escritor =
                     Files.newBufferedWriter(Path.of(ruta), StandardCharsets.UTF_8)) {

            escritor.write(encabezado);
            escritor.newLine();

            for (Registrable registro : registros) {
                escritor.write(registro.aLineaArchivo());
                escritor.newLine();
            }
        }
    }

    // =========================================================
    // LEER
    // =========================================================

    public void leerArchivoEmpleados() {
        leerArchivo(ARCHIVO_EMPLEADOS);
    }

    public void leerArchivoClientes() {
        leerArchivo(ARCHIVO_CLIENTES);
    }

    public void leerArchivoPrestamos() {
        leerArchivo(ARCHIVO_PRESTAMOS);
    }

    public void leerArchivoPagos() {
        leerArchivo(ARCHIVO_PAGOS);
    }

    /** Devuelve las líneas de datos (sin encabezado ni líneas vacías). */
    public List<String> leerRegistros(String ruta) throws IOException {

        try (Stream<String> lineas = Files.lines(Path.of(ruta), StandardCharsets.UTF_8)) {

            return lineas
                    .filter(linea -> !linea.isBlank())
                    .filter(linea -> !linea.startsWith("#"))
                    .collect(Collectors.toList());
        }
    }

    private void leerArchivo(String nombreArchivo) {

        File archivo = new File(nombreArchivo);

        if (!archivo.exists()) {

            System.out.println(
                    "El archivo todavía no existe."
            );

            return;
        }

        try {
            List<String> registros = leerRegistros(nombreArchivo);

            if (registros.isEmpty()) {
                System.out.println("El archivo no tiene registros.");
                return;
            }

            registros.forEach(System.out::println);

        } catch (IOException e) {

            System.out.println(
                    "Error al leer archivo: "
                    + e.getMessage()
            );
        }
    }
}
