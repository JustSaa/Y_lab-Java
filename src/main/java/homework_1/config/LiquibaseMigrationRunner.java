package homework_1.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Класс для запуска миграции Liquibase
 */
public class LiquibaseMigrationRunner {

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
                System.out.println("Liquibase миграции успешно выполнены!");

            } catch (Exception e) {
                throw new RuntimeException("Ошибка выполнения миграций Liquibase", e);
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки config.properties", e);
        }
    }

    /**
     * Запускает миграции Liquibase для указанной базы (используется в Testcontainers).
     */
    public static void runMigrations(String url, String user, String password) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update();
            System.out.println("Liquibase миграции успешно применены для " + url);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения миграций Liquibase", e);
        }
    }
}