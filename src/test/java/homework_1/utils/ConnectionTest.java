package homework_1.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Класс для тестового подключением к базе данных PostgreSQL.
 */
public class ConnectionTest {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionTest.class);

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/finance_db";
        Properties props = new Properties();
        props.setProperty("user", "finance_user");
        props.setProperty("password", "finance_pass");

        try (Connection connection = DriverManager.getConnection(url, props)) {
            logger.info("Соединение с БД успешно!");
        } catch (SQLException e) {
            logger.warn("Ошибка подключения к БД:");
            e.printStackTrace();
        }
    }
}