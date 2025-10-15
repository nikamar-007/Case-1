package ru.itevents.desktop.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Lightweight JDBC connection pool tailored for a desktop application.
 * It keeps a small pool of reusable connections and falls back to creating
 * a new connection when the pool is temporarily exhausted.
 */
public final class DatabaseManager {
    private static final Duration VALIDATION_TIMEOUT = Duration.ofSeconds(3);
    private final Deque<Connection> pool = new ArrayDeque<>();
    private final AppConfig config = AppConfig.getInstance();
    private final int poolSize = Math.max(1, config.getPoolSize());

    private static final class Holder {
        private static final DatabaseManager INSTANCE = new DatabaseManager();
    }

    private DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("MySQL JDBC driver was not found in the classpath", ex);
        }
    }

    public static DatabaseManager getInstance() {
        return Holder.INSTANCE;
    }

    public synchronized Connection getConnection() throws SQLException {
        while (!pool.isEmpty()) {
            Connection connection = pool.pop();
            if (isValid(connection)) {
                return connection;
            }
            closeQuietly(connection);
        }
        return DriverManager.getConnection(config.getJdbcUrl(), config.getJdbcUser(), config.getJdbcPassword());
    }

    public synchronized void release(Connection connection) {
        if (Objects.isNull(connection)) {
            return;
        }
        if (pool.size() >= poolSize) {
            closeQuietly(connection);
        } else {
            pool.push(connection);
        }
    }

    private boolean isValid(Connection connection) {
        try {
            return connection != null && !connection.isClosed() && connection.isValid((int) VALIDATION_TIMEOUT.toSeconds());
        } catch (SQLException ex) {
            return false;
        }
    }

    private void closeQuietly(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ignored) {
            // ignore
        }
    }
}
