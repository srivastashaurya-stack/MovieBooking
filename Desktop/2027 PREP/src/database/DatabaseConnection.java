package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/movie_booking_db");
        config.setUsername("root");
        config.setPassword("shaurya2701"); // Agar tumhara password alag hai toh yahan change kar lena

        // Production-grade Connection Pool Settings
        config.setMaximumPoolSize(10);        // Pool me max 10 active connections rahenge
        config.setMinimumIdle(5);             // 5 ready connections idle rahenge
        config.setConnectionTimeout(30000);   // 30 seconds wait timeout
        config.setIdleTimeout(600000);        // 10 minutes idle timeout

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}