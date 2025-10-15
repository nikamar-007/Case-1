package ru.itevents.desktop.util;

import ru.itevents.desktop.config.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class JdbcUtils {
    private JdbcUtils() {
    }

    public static <T> List<T> query(String sql, PreparedStatementSetter setter, Function<ResultSet, T> mapper) {
        List<T> result = new ArrayList<>();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Connection connection = null;
        try {
            connection = databaseManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                if (setter != null) {
                    setter.accept(statement);
                }
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        result.add(mapper.apply(rs));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to execute query: " + sql, ex);
        } finally {
            databaseManager.release(connection);
        }
        return result;
    }

    public static <T> T queryOne(String sql, PreparedStatementSetter setter, Function<ResultSet, T> mapper) {
        List<T> result = query(sql, setter, mapper);
        return result.isEmpty() ? null : result.get(0);
    }

    public static long insert(String sql, PreparedStatementSetter setter) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Connection connection = null;
        try {
            connection = databaseManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                if (setter != null) {
                    setter.accept(statement);
                }
                statement.executeUpdate();
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to execute insert: " + sql, ex);
        } finally {
            databaseManager.release(connection);
        }
        return -1L;
    }

    public static int update(String sql, PreparedStatementSetter setter) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Connection connection = null;
        try {
            connection = databaseManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                if (setter != null) {
                    setter.accept(statement);
                }
                return statement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to execute update: " + sql, ex);
        } finally {
            databaseManager.release(connection);
        }
    }

    @FunctionalInterface
    public interface PreparedStatementSetter {
        void accept(PreparedStatement statement) throws SQLException;
    }
}
