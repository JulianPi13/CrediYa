package com.crediya.modelo;

/**
 * Contrato para las entidades que se pueden guardar en un archivo de texto.
 * Cada clase decide cómo se convierte en una línea (polimorfismo).
 */
public interface Registrable {

    String SEPARADOR = ";";

    /** Devuelve la entidad como una línea separada por ";". */
    String aLineaArchivo();
}
