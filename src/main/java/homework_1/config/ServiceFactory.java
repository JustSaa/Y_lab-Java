package homework_1.config;

import homework_1.repositories.*;
import homework_1.repositories.jdbc.*;
import homework_1.services.*;
import homework_1.services.impl.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Фабрика для создания сервисов и репозиториев.
 */
public class ServiceFactory {

    private static ServiceFactory instance;
    private final Connection connection;

    private TransactionRepository transactionRepository;
    private BudgetRepository budgetRepository;
    private GoalRepository goalRepository;
    private NotificationRepository notificationRepository;
    private UserRepository userRepository;

    private AnalyticsService analyticsService;
    private AuthService authService;
    private BudgetService budgetService;
    private GoalService goalService;
    private NotificationService notificationService;
    private TransactionService transactionService;

    private ServiceFactory() {
        try {
            this.connection = ConnectionManager.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении соединения", e);
        }
    }

    /**
     * Получение единственного экземпляра фабрики (Singleton).
     */
    public static ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    /**
     * Репозитории
     */
    public TransactionRepository getTransactionRepository() throws SQLException {
        if (transactionRepository == null) {
            transactionRepository = new JdbcTransactionRepository(connection);
        }
        return transactionRepository;
    }

    public BudgetRepository getBudgetRepository() throws SQLException {
        if (budgetRepository == null) {
            budgetRepository = new JdbcBudgetRepository(connection);
        }
        return budgetRepository;
    }

    public GoalRepository getGoalRepository() throws SQLException {
        if (goalRepository == null) {
            goalRepository = new JdbcGoalRepository(connection);
        }
        return goalRepository;
    }

    public NotificationRepository getNotificationRepository() throws SQLException {
        if (notificationRepository == null) {
            notificationRepository = new JdbcNotificationRepository(connection);
        }
        return notificationRepository;
    }

    public UserRepository getUserRepository() throws SQLException {
        if (userRepository == null) {
            userRepository = new JdbcUserRepository(connection);
        }
        return userRepository;
    }

    /**
     * Сервисы
     */
    public AnalyticsService getAnalyticsService() throws SQLException {
        if (analyticsService == null) {
            analyticsService = new AnalyticsServiceImpl(getTransactionRepository());
        }
        return analyticsService;
    }

    public AuthService getAuthService() throws SQLException {
        if (authService == null) {
            authService = new AuthServiceImpl(getUserRepository());
        }
        return authService;
    }

    public BudgetService getBudgetService() throws SQLException {
        if (budgetService == null) {
            budgetService = new BudgetServiceImpl(getBudgetRepository(), getTransactionRepository());
        }
        return budgetService;
    }

    public GoalService getGoalService() throws SQLException {
        if (goalService == null) {
            goalService = new GoalServiceImpl(getGoalRepository());
        }
        return goalService;
    }

    public NotificationService getNotificationService() throws SQLException {
        if (notificationService == null) {
            notificationService = new NotificationServiceImpl(getNotificationRepository());
        }
        return notificationService;
    }

    public TransactionService getTransactionService() throws SQLException {
        if (transactionService == null) {
            transactionService = new TransactionServiceImpl(getTransactionRepository(), getBudgetService(), getNotificationService());
        }
        return transactionService;
    }
}