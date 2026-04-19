package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection helper.
 * Usage: Connection conn = DBConnection.getInstance().getConnection();
 */
public class DBConnection {

    // ── Change these to match your MySQL setup ──────────────────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/cinema_booking";
    private static final String USER     = "root";
    private static final String PASSWORD = "";
    // ────────────────────────────────────────────────────────────────────

    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found. Add mysql-connector-java.jar to your project.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            // Reconnect if connection was lost
            if (connection == null || connection.isClosed()) {
                instance = new DBConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
