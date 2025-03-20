package homework_1.utils;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Утилитарный класс для запуска миграций Liquibase в тестах.
 */
public class TestDatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(TestDatabaseUtil.class);

    /**
     * Запускает миграции Liquibase для тестовой базы данных.
     */
    public static void runTestMigrations(String url, String user, String password) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update();
            logger.info("Liquibase миграции успешно применены для тестовой БД {}", url);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения тестовых миграций Liquibase", e);
        }
    }
}