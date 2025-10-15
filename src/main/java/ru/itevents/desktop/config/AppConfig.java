package ru.itevents.desktop.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/**
 * Centralized access to application properties. Configuration is stored in {@code application.properties} file
 * located in the classpath. Besides JDBC configuration the file also contains paths to source data that can be
 * loaded into the MySQL database with the importer utility.
 */
public final class AppConfig {
    private static final String PROPERTIES_FILE = "/application.properties";

    private final Properties properties = new Properties();

    private static final class Holder {
        private static final AppConfig INSTANCE = new AppConfig();
    }

    private AppConfig() {
        try (InputStream input = AppConfig.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Cannot find application.properties in the classpath");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load application.properties", ex);
        }
    }

    public static AppConfig getInstance() {
        return Holder.INSTANCE;
    }

    public String getJdbcUrl() {
        return require("jdbc.url");
    }

    public String getJdbcUser() {
        return require("jdbc.user");
    }

    public String getJdbcPassword() {
        return require("jdbc.password");
    }

    public int getPoolSize() {
        return Integer.parseInt(properties.getProperty("jdbc.pool.size", "5"));
    }

    public Path getDataPath(String key) {
        return Path.of(properties.getProperty(key, "")).normalize();
    }

    private String require(String key) {
        String value = properties.getProperty(key);
        if (Objects.isNull(value) || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return value;
    }
}
