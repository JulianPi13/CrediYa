package com.crediya.dao;

import com.crediya.modelo.Empleado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpleadoDAO implements CrudDAO<Empleado> {

    @Override
    public int guardar(Empleado empleado) {

        String sql = "INSERT INTO empleados "
                + "(nombre, documento, rol, correo, salario) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement =
                     conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, empleado.getNombre());
            statement.setString(2, empleado.getDocumento());
            statement.setString(3, empleado.getRol());
            statement.setString(4, empleado.getCorreo());
            statement.setDouble(5, empleado.getSalario());

            statement.executeUpdate();

            try (ResultSet llaves = statement.getGeneratedKeys()) {
                if (llaves.next()) {
                    empleado.setId(llaves.getInt(1));
                }
            }

            return empleado.getId();

        } catch (SQLException e) {
            throw ErroresBD.traducir("guardar el empleado", e);
        }
    }

    @Override
    public List<Empleado> listar() {

        List<Empleado> empleados = new ArrayList<>();

        String sql = "SELECT * FROM empleados ORDER BY id";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                empleados.add(mapear(resultado));
            }

        } catch (SQLException e) {
            throw ErroresBD.traducir("listar los empleados", e);
        }

        return empleados;
    }

    @Override
    public Optional<Empleado> buscarPorId(int id) {

        String sql = "SELECT * FROM empleados WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw ErroresBD.traducir("buscar el empleado", e);
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
        String sql = "SELECT 1 FROM empleados WHERE " + columna + " = ? LIMIT 1";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, valor);

            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next();
            }

        } catch (SQLException e) {
            throw ErroresBD.traducir("verificar el empleado", e);
        }
    }

    private Empleado mapear(ResultSet resultado) throws SQLException {

        Empleado empleado = new Empleado();

        empleado.setId(resultado.getInt("id"));
        empleado.setNombre(resultado.getString("nombre"));
        empleado.setDocumento(resultado.getString("documento"));
        empleado.setRol(resultado.getString("rol"));
        empleado.setCorreo(resultado.getString("correo"));
        empleado.setSalario(resultado.getDouble("salario"));

        return empleado;
    }
}
