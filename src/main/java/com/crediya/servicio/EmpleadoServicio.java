package com.crediya.servicio;

import com.crediya.dao.EmpleadoDAO;
import com.crediya.excepciones.ValidacionException;
import com.crediya.modelo.Empleado;
import com.crediya.util.Constantes;
import com.crediya.util.Formato;
import com.crediya.util.Validador;

import java.util.List;
import java.util.Optional;

public class EmpleadoServicio {

    private final EmpleadoDAO empleadoDAO;

    public EmpleadoServicio() {
        this(new EmpleadoDAO());
    }

    /** Permite inyectar otro DAO (por ejemplo, uno en memoria para pruebas). */
    public EmpleadoServicio(EmpleadoDAO empleadoDAO) {
        this.empleadoDAO = empleadoDAO;
    }

    public void registrarEmpleado(Empleado empleado) throws ValidacionException {

        if (empleado == null) {
            throw new ValidacionException("El empleado no puede ser nulo.");
        }

        if (!Validador.nombreValido(empleado.getNombre())) {

            throw new ValidacionException(
                    "El nombre del empleado es obligatorio y solo puede tener letras y espacios."
            );
        }

        if (!Validador.documentoValido(empleado.getDocumento())) {

            throw new ValidacionException(
                    "El documento del empleado debe tener solo números ("
                    + Constantes.DOCUMENTO_MIN + " a " + Constantes.DOCUMENTO_MAX + " dígitos)."
            );
        }

        if (!Validador.rolValido(empleado.getRol())) {

            throw new ValidacionException(
                    "El rol debe ser uno de: " + Constantes.ROLES
            );
        }

        if (!Validador.correoValido(empleado.getCorreo())) {

            throw new ValidacionException(
                    "El correo del empleado no es válido."
            );
        }

        if (!Validador.salarioValido(empleado.getSalario())) {

            throw new ValidacionException(
                    "El salario debe ser mayor que cero y menor o igual a "
                    + Formato.moneda(Constantes.SALARIO_MAX) + "."
            );
        }

        // Normalizar datos antes de guardar
        empleado.setNombre(Validador.formatearNombre(empleado.getNombre()));
        empleado.setDocumento(empleado.getDocumento().trim());
        empleado.setRol(empleado.getRol().trim().toUpperCase());
        empleado.setCorreo(empleado.getCorreo().trim().toLowerCase());

        if (empleadoDAO.existeDocumento(empleado.getDocumento())) {

            throw new ValidacionException(
                    "Ya existe un empleado con el documento " + empleado.getDocumento() + "."
            );
        }

        if (empleadoDAO.existeCorreo(empleado.getCorreo())) {

            throw new ValidacionException(
                    "Ya existe un empleado con el correo " + empleado.getCorreo() + "."
            );
        }

        empleadoDAO.guardar(empleado);

        System.out.println("Empleado guardado correctamente con ID " + empleado.getId() + ".");
    }

    public void listarEmpleados() {

        List<Empleado> empleados = empleadoDAO.listar();

        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados.");
            return;
        }

        for (Empleado empleado : empleados) {
            System.out.println(empleado);
        }
    }

    public List<Empleado> obtenerEmpleados() {
        return empleadoDAO.listar();
    }

    public Optional<Empleado> buscarPorId(int id) {
        return empleadoDAO.buscarPorId(id);
    }

    public boolean existeDocumento(String documento) {
        return empleadoDAO.existeDocumento(documento);
    }

    public boolean existeCorreo(String correo) {
        return empleadoDAO.existeCorreo(correo);
    }
}
