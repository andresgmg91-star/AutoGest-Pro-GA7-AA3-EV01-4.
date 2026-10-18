
package com.autogestpro;

import com.autogestpro.config.DatabaseConnection;
import com.autogestpro.ui.VentanaLogin;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.SQLException;

/** Punto de entrada de AutoGest Pro. */
public class Main {

    /**
     * Arranca la versión de escritorio: comprueba la conexión con la base de datos
     * y, si responde, muestra la ventana de inicio de sesión.
     * (La versión web se inicia desde AutoGestWebApplication.)
     */
    public static void main(String[] args) {
        try (Connection ignored = DatabaseConnection.getConnection()) {
            // Comprueba la conexión antes de mostrar el inicio de sesión.
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo conectar con la base de datos de AutoGest Pro.\n\n"
                            + "1. Inicie MySQL/MariaDB.\n"
                            + "2. Ejecute database/autogest_pro.sql.\n"
                            + "3. Revise src/main/resources/db.properties o las variables de entorno.\n\n"
                            + "Detalle: " + exception.getMessage(),
                    "Error de conexión JDBC",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}
