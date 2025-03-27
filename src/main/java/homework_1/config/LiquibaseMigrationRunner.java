package homework_1.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Класс для запуска миграции Liquibase
 */
public class LiquibaseMigrationRunner {
    private static final Logger logger = LoggerFactory.getLogger(LiquibaseMigrationRunner.class);

    /**
     * Запускает миграции Liquibase для основной базы (использует config.properties).
     */
    public static void runMigrations() {
        Properties properties = new Properties();
        try (var input = LiquibaseMigrationRunner.class.getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new IOException("Файл config.properties не найден!");
            }

            properties.load(input);

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            String changelogFile = properties.getProperty("db.changelog.file");

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(connection));

                Liquibase liquibase = new Liquibase(changelogFile, new ClassLoaderResourceAccessor(), database);
                liquibase.update();
                logger.info("Liquibase миграции успешно выполнены!");

            } catch (Exception e) {
                throw new RuntimeException("Ошибка выполнения миграций Liquibase", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки config.properties", e);
        }
    }
}