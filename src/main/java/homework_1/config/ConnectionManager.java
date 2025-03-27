package homework_1.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Класс для управления подключением к базе данных PostgreSQL.
 */
public class ConnectionManager {
    private static final String PROPERTIES_FILE = "config.properties";
    private static HikariDataSource dataSource;

    static {
        loadDataSource();
    }

    /**
     * Загружает параметры подключения к базе данных из конфигурационного файла
     * и настраивает пул соединений HikariCP.
     */
    private static void loadDataSource() {
        try (InputStream input = ConnectionManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            Properties properties = new Properties();
            properties.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.username"));
            config.setPassword(properties.getProperty("db.password"));
            config.setMaximumPoolSize(6);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(10000);

            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки конфигурации", e);
        }
    }

    /**
     * Получает соединение с базы данных из пула.
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Закрывает пул соединений.
     */
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}