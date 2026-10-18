package com.autogestpro.dao;

import com.autogestpro.config.DatabaseConnection;
import com.autogestpro.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Contiene las operaciones SQL para la entidad Cliente. */
public class ClienteDAO {
    // Consulta de selección compartida; cada método le agrega su WHERE u ORDER BY.
    private static final String CONSULTA_BASE = """
            SELECT id, tipo_documento, numero_documento, nombres, apellidos,
                   telefono, correo, direccion
            FROM clientes
            """;

    /**
     * Registra un cliente nuevo en la base de datos.
     *
     * @param cliente datos del cliente (el id se ignora: lo genera AUTO_INCREMENT)
     * @return id generado para el nuevo registro, o -1 si no se obtuvo
     * @throws SQLException si falla la consulta o se repite el documento
     */
    public long insertar(Cliente cliente) throws SQLException {
        String sql = """
                INSERT INTO clientes
                    (tipo_documento, numero_documento, nombres, apellidos,
                     telefono, correo, direccion)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            asignarParametros(statement, cliente);
            // Ejecuta el INSERT y luego se lee la llave autogenerada.
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        }
        // Si la base de datos no devolvió llave, se informa con -1.
        return -1;
    }

    /**
     * Consulta todos los clientes, del más reciente al más antiguo.
     *
     * @return lista de clientes (vacía si no hay registros)
     * @throws SQLException si falla la consulta
     */
    public List<Cliente> consultarTodos() throws SQLException {
        String sql = CONSULTA_BASE + " ORDER BY id DESC";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                clientes.add(mapearCliente(resultSet));
            }
        }
        return clientes;
    }

    /**
     * Busca clientes por número de documento exacto.
     * El valor se envía como parámetro (?), nunca concatenado al SQL, para
     * evitar inyección SQL.
     *
     * @param numeroDocumento número de documento a buscar
     * @return coincidencias (lista vacía si no hay ninguna)
     * @throws SQLException si falla la consulta
     */
    public List<Cliente> buscarPorDocumento(String numeroDocumento) throws SQLException {
        String sql = CONSULTA_BASE + " WHERE numero_documento = ? ORDER BY id DESC";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, numeroDocumento);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    clientes.add(mapearCliente(resultSet));
                }
            }
        }
        return clientes;
    }

    /**
     * Actualiza todos los datos de un cliente existente.
     *
     * @param id identificador del cliente a modificar
     * @param cliente nuevos datos
     * @return true si se actualizó una fila; false si el id no existe
     * @throws SQLException si falla la consulta
     */
    public boolean actualizar(long id, Cliente cliente) throws SQLException {
        String sql = """
                UPDATE clientes
                SET tipo_documento = ?, numero_documento = ?, nombres = ?,
                    apellidos = ?, telefono = ?, correo = ?, direccion = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            asignarParametros(statement, cliente);
            // Los parámetros 1 a 7 los asigna asignarParametros; el 8 es el id del WHERE.
            statement.setLong(8, id);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un cliente por su identificador.
     *
     * @param id identificador del cliente
     * @return true si se eliminó una fila; false si el id no existe
     * @throws SQLException si falla la consulta
     */
    public boolean eliminar(long id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Pone los datos del cliente en los siete parámetros (?) del INSERT o UPDATE,
     * en el mismo orden en que aparecen las columnas.
     */
    private void asignarParametros(PreparedStatement statement, Cliente cliente)
            throws SQLException {
        statement.setString(1, cliente.getTipoDocumento());
        statement.setString(2, cliente.getNumeroDocumento());
        statement.setString(3, cliente.getNombres());
        statement.setString(4, cliente.getApellidos());
        statement.setString(5, cliente.getTelefono());
        statement.setString(6, cliente.getCorreo());
        statement.setString(7, cliente.getDireccion());
    }

    /** Convierte la fila actual del ResultSet en un objeto Cliente. */
    private Cliente mapearCliente(ResultSet resultSet) throws SQLException {
        return new Cliente(
                resultSet.getLong("id"),
                resultSet.getString("tipo_documento"),
                resultSet.getString("numero_documento"),
                resultSet.getString("nombres"),
                resultSet.getString("apellidos"),
                resultSet.getString("telefono"),
                resultSet.getString("correo"),
                resultSet.getString("direccion")
        );
    }
}
