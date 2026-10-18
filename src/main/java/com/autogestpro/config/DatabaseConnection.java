package com.autogestpro.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Administra las conexiones JDBC de AutoGest Pro. */
public final class DatabaseConnection {
    // Valores por defecto pensados para un XAMPP local de práctica.
    // Se pueden reemplazar con db.properties o con variables de entorno.
    private static final String URL_PREDETERMINADA =
            "jdbc:mariadb://localhost:3306/autogest_pro";
    private static final String USUARIO_PREDETERMINADO = "root";
    private static final String CLAVE_PREDETERMINADA = "";

    private DatabaseConnection() {
        // Evita crear instancias de una clase de utilidad.
    }

    /**
     * Abre una conexión JDBC nueva con la base de datos.
     * Los datos de conexión se toman, en este orden de prioridad, de las
     * variables de entorno, del archivo db.properties y de los valores por defecto.
     *
     * @return conexión abierta (quien la usa debe cerrarla, por ejemplo con try-with-resources)
     * @throws SQLException si no se puede conectar con el servidor
     */
    public static Connection getConnection() throws SQLException {
        Properties properties = cargarPropiedades();

        String url = valorConfiguracion(
                "AUTOGEST_DB_URL", "db.url", URL_PREDETERMINADA, properties);
        String usuario = valorConfiguracion(
                "AUTOGEST_DB_USER", "db.username", USUARIO_PREDETERMINADO, properties);
        String clave = valorConfiguracion(
                "AUTOGEST_DB_PASSWORD", "db.password", CLAVE_PREDETERMINADA, properties);

        return DriverManager.getConnection(url, usuario, clave);
    }

    /**
     * Lee db.properties desde el classpath. Si el archivo no existe
     * devuelve propiedades vacías y se usan los valores por defecto.
     */
    private static Properties cargarPropiedades() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConnection.class
                .getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "No fue posible leer la configuración de la base de datos.", exception);
        }
        return properties;
    }

    /**
     * Devuelve el valor de un parámetro: primero la variable de entorno,
     * luego la propiedad del archivo y, por último, el valor por defecto.
     */
    private static String valorConfiguracion(
            String variableEntorno,
            String propiedad,
            String valorPredeterminado,
            Properties properties) {
        String valorEntorno = System.getenv(variableEntorno);
        if (valorEntorno != null && !valorEntorno.isBlank()) {
            return valorEntorno;
        }
        return properties.getProperty(propiedad, valorPredeterminado);
    }
}
