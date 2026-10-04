package com.crediya.dao;

import com.crediya.modelo.Pago;
import com.crediya.modelo.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PagoDAO implements CrudDAO<Pago> {

    private static final String SQL_INSERTAR =
            "INSERT INTO pagos (prestamo_id, fecha_pago, monto) VALUES (?, ?, ?)";

    private static final String SQL_ACTUALIZAR_PRESTAMO =
            "UPDATE prestamos SET saldo_pendiente = ?, estado = ? WHERE id = ?";

    /** Guarda solo el pago (sin tocar el préstamo). */
    @Override
    public int guardar(Pago pago) {

        try (Connection conexion = ConexionBD.conectar()) {

            return insertar(conexion, pago);

        } catch (SQLException e) {
            throw ErroresBD.traducir("guardar el pago", e);
        }
    }

    /**
     * Guarda el pago y actualiza el saldo/estado del préstamo en una
     * sola transacción: o se guardan las dos cosas o ninguna.
     */
    public int guardarYActualizarPrestamo(Pago pago, double nuevoSaldo, String nuevoEstado) {

        try (Connection conexion = ConexionBD.conectar()) {

            conexion.setAutoCommit(false);

            try {
                int id = insertar(conexion, pago);

                try (PreparedStatement statement =
                             conexion.prepareStatement(SQL_ACTUALIZAR_PRESTAMO)) {

                    statement.setDouble(1, nuevoSaldo);
                    statement.setString(2, nuevoEstado);
                    statement.setInt(3, pago.getPrestamo().getId());
                    statement.executeUpdate();
                }

                conexion.commit();
                return id;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw ErroresBD.traducir("registrar el pago", e);
        }
    }

    private int insertar(Connection conexion, Pago pago) throws SQLException {

        try (PreparedStatement statement =
                     conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, pago.getPrestamo().getId());
            statement.setDate(2, java.sql.Date.valueOf(pago.getFechaPago()));
            statement.setDouble(3, pago.getMonto());

            statement.executeUpdate();

            try (ResultSet llaves = statement.getGeneratedKeys()) {
                if (llaves.next()) {
                    pago.setId(llaves.getInt(1));
                }
            }

            return pago.getId();
        }
    }

    @Override
    public List<Pago> listar() {

        String sql = """
                SELECT
                    p.id,
                    p.prestamo_id,
                    p.fecha_pago,
                    p.monto
                FROM pagos p
                ORDER BY p.fecha_pago, p.id
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            List<Pago> pagos = new ArrayList<>();

            while (resultado.next()) {
                pagos.add(mapear(resultado));
            }

            return pagos;

        } catch (SQLException e) {
            throw ErroresBD.traducir("listar los pagos", e);
        }
    }

    @Override
    public Optional<Pago> buscarPorId(int id) {

        String sql = "SELECT id, prestamo_id, fecha_pago, monto FROM pagos WHERE id = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw ErroresBD.traducir("buscar el pago", e);
        }
    }

    public List<Pago> listarPorPrestamo(int prestamoId) {

        String sql =
                "SELECT id, prestamo_id, fecha_pago, monto "
                + "FROM pagos "
                + "WHERE prestamo_id = ? "
                + "ORDER BY fecha_pago ASC, id ASC";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, prestamoId);

            try (ResultSet resultado = statement.executeQuery()) {

                List<Pago> pagos = new ArrayList<>();

                while (resultado.next()) {
                    pagos.add(mapear(resultado));
                }

                return pagos;
            }

        } catch (SQLException e) {
            throw ErroresBD.traducir("consultar los pagos", e);
        }
    }

    private Pago mapear(ResultSet resultado) throws SQLException {

        Prestamo prestamo = new Prestamo();
        prestamo.setId(resultado.getInt("prestamo_id"));

        return new Pago(
                resultado.getInt("id"),
                prestamo,
                resultado.getDate("fecha_pago").toLocalDate(),
                resultado.getDouble("monto")
        );
    }
}
