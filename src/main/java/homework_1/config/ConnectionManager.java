package homework_1.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Класс для управления подключением к базе данных PostgreSQL.
 */
public class ConnectionManager {
    private static final String PROPERTIES_FILE = "config.properties";
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        loadProperties();
    }

    /**
     * Загружает параметры подключения к базе данных из конфигурационного файла.
     * В случае ошибки выбрасывает {@link RuntimeException}.
     */
    private static void loadProperties() {
        try (InputStream input = ConnectionManager.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            Properties properties = new Properties();
            properties.load(input);
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.username");
            PASSWORD = properties.getProperty("db.password");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки конфигурации", e);
        }
    }

    /**
     * Получает соединение с базой данных.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}