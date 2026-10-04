package com.crediya.servicio;

import com.crediya.dao.ClienteDAO;
import com.crediya.excepciones.ValidacionException;
import com.crediya.modelo.Cliente;
import com.crediya.util.Constantes;
import com.crediya.util.Validador;

import java.util.List;
import java.util.Optional;

public class ClienteServicio {

    private final ClienteDAO clienteDAO;

    public ClienteServicio() {
        this(new ClienteDAO());
    }

    /** Permite inyectar otro DAO (por ejemplo, uno en memoria para pruebas). */
    public ClienteServicio(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public void registrarCliente(Cliente cliente) throws ValidacionException {

        if (cliente == null) {
            throw new ValidacionException("El cliente no puede ser nulo.");
        }

        if (!Validador.nombreValido(cliente.getNombre())) {

            throw new ValidacionException(
                    "El nombre del cliente es obligatorio y solo puede tener letras y espacios."
            );
        }

        if (!Validador.documentoValido(cliente.getDocumento())) {

            throw new ValidacionException(
                    "El documento del cliente debe tener solo números ("
                    + Constantes.DOCUMENTO_MIN + " a " + Constantes.DOCUMENTO_MAX + " dígitos)."
            );
        }

        if (!Validador.correoValido(cliente.getCorreo())) {

            throw new ValidacionException(
                    "El correo del cliente no es válido."
            );
        }

        if (!Validador.telefonoValido(cliente.getTelefono())) {

            throw new ValidacionException(
                    "El teléfono del cliente debe tener 10 números y empezar por 3 o 6."
            );
        }

        // Normalizar datos antes de guardar
        cliente.setNombre(Validador.formatearNombre(cliente.getNombre()));
        cliente.setDocumento(cliente.getDocumento().trim());
        cliente.setCorreo(cliente.getCorreo().trim().toLowerCase());
        cliente.setTelefono(cliente.getTelefono().trim());

        if (clienteDAO.existeDocumento(cliente.getDocumento())) {

            throw new ValidacionException(
                    "Ya existe un cliente con el documento " + cliente.getDocumento() + "."
            );
        }

        if (clienteDAO.existeCorreo(cliente.getCorreo())) {

            throw new ValidacionException(
                    "Ya existe un cliente con el correo " + cliente.getCorreo() + "."
            );
        }

        clienteDAO.guardar(cliente);

        System.out.println("Cliente guardado correctamente con ID " + cliente.getId() + ".");
    }

    public void listarClientes() {

        List<Cliente> clientes = clienteDAO.listar();

        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }

        for (Cliente cliente : clientes) {
            System.out.println(cliente);
        }
    }

    public List<Cliente> obtenerClientes() {
        return clienteDAO.listar();
    }

    public Optional<Cliente> buscarPorId(int id) {
        return clienteDAO.buscarPorId(id);
    }

    public boolean existeDocumento(String documento) {
        return clienteDAO.existeDocumento(documento);
    }

    public boolean existeCorreo(String correo) {
        return clienteDAO.existeCorreo(correo);
    }
}
