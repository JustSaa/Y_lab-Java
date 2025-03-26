package homework_1.config;

import homework_1.repositories.BudgetRepository;
import homework_1.repositories.TransactionRepository;
import homework_1.repositories.jdbc.JdbcBudgetRepository;
import homework_1.repositories.jdbc.JdbcTransactionRepository;
import homework_1.services.AnalyticsService;
import homework_1.services.BudgetService;
import homework_1.services.impl.AnalyticsServiceImpl;
import homework_1.services.impl.BudgetServiceImpl;

import java.sql.Connection;

/**
 * Класс для конфигурации приложения и создания бинов.
 */
public class ApplicationConfig {

    private static final ApplicationConfig INSTANCE = new ApplicationConfig();
    private final AnalyticsService analyticsService;
    private final BudgetService budgetService;

    private ApplicationConfig() {
        try {
            Connection connection = ConnectionManager.getConnection();
            TransactionRepository transactionRepository = new JdbcTransactionRepository(connection);
            this.analyticsService = new AnalyticsServiceImpl(transactionRepository);
            BudgetRepository budgetRepository = new JdbcBudgetRepository(connection);
            this.budgetService = new BudgetServiceImpl(budgetRepository, transactionRepository);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации ApplicationConfig", e);
        }
    }

    public static ApplicationConfig getInstance() {
        return INSTANCE;
    }

    public AnalyticsService getAnalyticsService() {
        return analyticsService;
    }

    public BudgetService getBudgetService() {
        return budgetService;
    }
}