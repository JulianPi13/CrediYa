package com.crediya.dao;

import com.crediya.modelo.Cliente;
import com.crediya.modelo.Empleado;
import com.crediya.modelo.Prestamo;
import com.crediya.util.Constantes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrestamoDAO implements CrudDAO<Prestamo> {

    private static final String SELECT_BASE = """
            SELECT
                p.id,
                p.monto,
                p.interes,
                p.cuotas,
                p.cuota_mensual,
                p.fecha_inicio,
                p.estado,
                p.monto_total,
                p.saldo_pendiente,

                c.id AS cliente_id,
                c.nombre AS cliente_nombre,
                c.documento AS cliente_documento,
                c.correo AS cliente_correo,
                c.telefono AS cliente_telefono,

                e.id AS empleado_id,
                e.nombre AS empleado_nombre,
                e.documento AS empleado_documento,
                e.rol AS empleado_rol,
                e.correo AS empleado_correo,
                e.salario AS empleado_salario

            FROM prestamos p
            INNER JOIN clientes c
                ON p.cliente_id = c.id
            INNER JOIN empleados e
                ON p.empleado_id = e.id
            """;

    @Override
    public int guardar(Prestamo prestamo) {

        String sql = "INSERT INTO prestamos "
                + "(cliente_id, empleado_id, monto, interes, cuotas, "
                + "cuota_mensual, fecha_inicio, estado, monto_total, "
                + "saldo_pendiente) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement =
                     conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, prestamo.getCliente().getId());
            statement.setInt(2, prestamo.getEmpleado().getId());
            statement.setDouble(3, prestamo.getMonto());
            statement.setDouble(4, prestamo.getInteres());
            statement.setInt(5, prestamo.getCuotas());
            statement.setDouble(6, prestamo.getCuotaMensual());

            statement.setDate(
                    7,
                    java.sql.Date.valueOf(prestamo.getFechaInicio())
            );

            statement.setString(8, prestamo.getEstado());
            statement.setDouble(9, prestamo.getMontoTotal());
            statement.setDouble(10, prestamo.getSaldoPendiente());

            statement.executeUpdate();

            try (ResultSet llaves = statement.getGeneratedKeys()) {
                if (llaves.next()) {
                    prestamo.setId(llaves.getInt(1));
                }
            }

            return prestamo.getId();

        } catch (SQLException e) {
            throw ErroresBD.traducir("guardar el préstamo", e);
        }
    }

    @Override
    public List<Prestamo> listar() {

        List<Prestamo> prestamos = new ArrayList<>();

        String sql = SELECT_BASE + " ORDER BY p.id";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                prestamos.add(mapear(resultado));
            }

        } catch (SQLException e) {
            throw ErroresBD.traducir("listar los préstamos", e);
        }

        return prestamos;
    }

    @Override
    public Optional<Prestamo> buscarPorId(int id) {

        String sql = SELECT_BASE + " WHERE p.id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw ErroresBD.traducir("buscar el préstamo", e);
        }
    }

    public void actualizarSaldoYEstado(
            int id,
            double saldoPendiente,
            String estado) {

        String sql = "UPDATE prestamos "
                + "SET saldo_pendiente = ?, estado = ? "
                + "WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setDouble(1, saldoPendiente);
            statement.setString(2, estado);
            statement.setInt(3, id);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw ErroresBD.traducir("actualizar el préstamo", e);
        }
    }

    public void refinanciar(
            int id,
            int nuevasCuotas,
            double nuevoMontoTotal,
            double nuevaCuotaMensual,
            double nuevoSaldoPendiente) {

        String sql = "UPDATE prestamos "
                + "SET cuotas = ?, "
                + "cuota_mensual = ?, "
                + "monto_total = ?, "
                + "saldo_pendiente = ?, "
                + "estado = ? "
                + "WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setInt(1, nuevasCuotas);
            statement.setDouble(2, nuevaCuotaMensual);
            statement.setDouble(3, nuevoMontoTotal);
            statement.setDouble(4, nuevoSaldoPendiente);
            statement.setString(5, Constantes.ESTADO_PENDIENTE);
            statement.setInt(6, id);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw ErroresBD.traducir("refinanciar el préstamo", e);
        }
    }

    private Prestamo mapear(ResultSet resultado) throws SQLException {

        Cliente cliente = new Cliente(
                resultado.getInt("cliente_id"),
                resultado.getString("cliente_nombre"),
                resultado.getString("cliente_documento"),
                resultado.getString("cliente_correo"),
                resultado.getString("cliente_telefono")
        );

        Empleado empleado = new Empleado(
                resultado.getInt("empleado_id"),
                resultado.getString("empleado_nombre"),
                resultado.getString("empleado_documento"),
                resultado.getString("empleado_rol"),
                resultado.getString("empleado_correo"),
                resultado.getDouble("empleado_salario")
        );

        Prestamo prestamo = new Prestamo();

        prestamo.setId(resultado.getInt("id"));
        prestamo.setCliente(cliente);
        prestamo.setEmpleado(empleado);
        prestamo.setMonto(resultado.getDouble("monto"));
        prestamo.setInteres(resultado.getDouble("interes"));
        prestamo.setCuotas(resultado.getInt("cuotas"));
        prestamo.setCuotaMensual(resultado.getDouble("cuota_mensual"));
        prestamo.setFechaInicio(resultado.getDate("fecha_inicio").toLocalDate());
        prestamo.setEstado(resultado.getString("estado"));
        prestamo.setMontoTotal(resultado.getDouble("monto_total"));
        prestamo.setSaldoPendiente(resultado.getDouble("saldo_pendiente"));

        return prestamo;
    }
}
