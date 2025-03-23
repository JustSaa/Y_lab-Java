package homework_1.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Слушатель инициализации приложения — выполняет запуск Liquibase миграций.
 */
@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("▶ Инициализация приложения: запуск Liquibase...");
        try {
            new LiquibaseMigrationRunner().runMigrations();
        } catch (Exception e) {
            System.err.println("❌ Ошибка запуска Liquibase миграций: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("⛔ Завершение приложения");
    }
}