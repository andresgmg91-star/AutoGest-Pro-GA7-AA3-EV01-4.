
package com.autogestpro.dao;

import com.autogestpro.config.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Operaciones SQL para validar el acceso de los usuarios de AutoGest Pro.
 * Las contraseñas se guardan como hash BCrypt, nunca en texto plano.
 */
public class UsuarioDAO {

    /**
     * Verifica el usuario y la contraseña.
     *
     * @param nombreUsuario nombre con el que se inicia sesión
     * @param clave contraseña escrita (arreglo de caracteres para poder borrarlo de memoria)
     * @return true si el usuario existe, está activo y la contraseña coincide con su hash
     * @throws SQLException si falla la consulta
     */
    public boolean autenticar(String nombreUsuario, char[] clave)
            throws SQLException {

        if (nombreUsuario == null || nombreUsuario.isBlank()
                || clave == null || clave.length == 0) {
            return false;
        }

        String sql = """
                SELECT clave_hash
                FROM usuarios
                WHERE nombre_usuario = ?
                  AND activo = 1
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, nombreUsuario.trim());

            try (ResultSet resultado = consulta.executeQuery()) {
                if (!resultado.next()) {
                    return false;
                }

                // Se compara la contraseña escrita contra el hash guardado en la base de datos.
                String hashAlmacenado = resultado.getString("clave_hash");

                if (hashAlmacenado == null || hashAlmacenado.isBlank()) {
                    return false;
                }

                // Un hash mal formado lanza IllegalArgumentException; se trata como acceso denegado.
                try {
                    return BCrypt.checkpw(
                            new String(clave), hashAlmacenado);
                } catch (IllegalArgumentException excepcion) {
                    return false;
                }
            }
        }
    }

    /**
     * Indica si existe un usuario activo con ese nombre. Sirve para distinguir
     * entre "usuario no encontrado" y "contraseña incorrecta" en el login web.
     *
     * @param nombreUsuario nombre a comprobar
     * @return true si existe y está activo
     * @throws SQLException si falla la consulta
     */
    public boolean existeUsuarioActivo(String nombreUsuario)
            throws SQLException {

        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return false;
        }

        String sql = """
                SELECT 1
                FROM usuarios
                WHERE nombre_usuario = ?
                  AND activo = 1
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, nombreUsuario.trim());

            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }
        }
    }
}
