package homework_1.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Слушатель инициализации приложения — выполняет запуск Liquibase миграций.
 */
@WebListener
public class AppInitListener implements ServletContextListener {
    private final static Logger logger = LoggerFactory.getLogger(AppInitListener.class);

    @Override

    public void contextInitialized(ServletContextEvent sce) {
        logger.info("▶ Инициализация приложения: запуск Liquibase...");
        try {
            new LiquibaseMigrationRunner().runMigrations();
        } catch (Exception e) {
            logger.warn("❌ Ошибка запуска Liquibase миграций: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("⛔ Завершение приложения");
    }
}