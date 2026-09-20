package Infrastructure.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.URI;

/**
 * Conexion JDBC directa a PostgreSQL. Los datos salen de variables de entorno
 * (DB_URL, DB_USER, DB_PASSWORD); nunca se guardan credenciales en el codigo.
 * Tambien acepta DATABASE_URL con formato postgres://usuario:clave@host:puerto/db.
 */
public final class Conexion {
    private Conexion() { }

    private static String env(String nombre, String defecto) {
        String v = System.getenv(nombre);
        return (v == null || v.isBlank()) ? defecto : v;
    }

    public static Connection obtener() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de PostgreSQL no encontrado", e);
        }
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            try {
                URI u = new URI(databaseUrl);
                String[] ui = u.getUserInfo().split(":", 2);
                String jdbc = "jdbc:postgresql://" + u.getHost()
                        + (u.getPort() > 0 ? ":" + u.getPort() : "") + u.getPath()
                        + (u.getQuery() != null ? "?" + u.getQuery() : "?sslmode=require");
                return DriverManager.getConnection(jdbc, ui[0], ui.length > 1 ? ui[1] : "");
            } catch (java.net.URISyntaxException e) {
                throw new SQLException("DATABASE_URL invalida", e);
            }
        }
        return DriverManager.getConnection(
                env("DB_URL", "jdbc:postgresql://localhost:5432/edificios"),
                env("DB_USER", "postgres"),
                env("DB_PASSWORD", "postgres"));
    }
}
