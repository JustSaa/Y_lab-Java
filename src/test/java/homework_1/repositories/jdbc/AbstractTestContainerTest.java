package homework_1.repositories.jdbc;

import homework_1.config.LiquibaseMigrationRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Testcontainers
public abstract class AbstractTestContainerTest {

    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("test_pass");

    protected static Connection connection;

    @BeforeAll
    static void startContainerAndSetUpDatabase() throws SQLException {
        POSTGRESQL_CONTAINER.start();

        String url = POSTGRESQL_CONTAINER.getJdbcUrl();
        String username = POSTGRESQL_CONTAINER.getUsername();
        String password = POSTGRESQL_CONTAINER.getPassword();

        connection = DriverManager.getConnection(url, username, password);
        LiquibaseMigrationRunner.runMigrations(url, username, password);
    }

    @AfterAll
    static void stopContainer() {
        POSTGRESQL_CONTAINER.stop();
    }
}