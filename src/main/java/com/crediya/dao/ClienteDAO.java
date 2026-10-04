package com.crediya.dao;

import com.crediya.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDAO implements CrudDAO<Cliente> {

    @Override
    public int guardar(Cliente cliente) {

        String sql = "INSERT INTO clientes "
                + "(nombre, documento, correo, telefono) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement =
                     conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, cliente.getNombre());
            statement.setString(2, cliente.getDocumento());
            statement.setString(3, cliente.getCorreo());
            statement.setString(4, cliente.getTelefono());

            statement.executeUpdate();

            try (ResultSet llaves = statement.getGeneratedKeys()) {
                if (llaves.next()) {
                    cliente.setId(llaves.getInt(1));
                }
            }

            return cliente.getId();

        } catch (SQLException e) {
            throw ErroresBD.traducir("guardar el cliente", e);
        }
    }

    @Override
    public List<Cliente> listar() {

        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT * FROM clientes ORDER BY id";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                clientes.add(mapear(resultado));
            }

        } catch (SQLException e) {
            throw ErroresBD.traducir("listar los clientes", e);
        }

        return clientes;
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {

        String sql = "SELECT * FROM clientes WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw ErroresBD.traducir("buscar el cliente", e);
        }
    }

    public boolean existeDocumento(String documento) {
        return existe("documento", documento);
    }

    public boolean existeCorreo(String correo) {
        return existe("correo", correo);
    }

    private boolean existe(String columna, String valor) {

        // "columna" nunca viene del usuario, solo de los métodos de arriba.
        String sql = "SELECT 1 FROM clientes WHERE " + columna + " = ? LIMIT 1";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, valor);

            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next();
            }

        } catch (SQLException e) {
            throw ErroresBD.traducir("verificar el cliente", e);
        }
    }

    private Cliente mapear(ResultSet resultado) throws SQLException {

        Cliente cliente = new Cliente();

        cliente.setId(resultado.getInt("id"));
        cliente.setNombre(resultado.getString("nombre"));
        cliente.setDocumento(resultado.getString("documento"));
        cliente.setCorreo(resultado.getString("correo"));
        cliente.setTelefono(resultado.getString("telefono"));

        return cliente;
    }
}
