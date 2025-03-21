package homework_1;

import homework_1.config.ConnectionManager;
import homework_1.config.LiquibaseMigrationRunner;
import homework_1.repositories.BudgetRepository;
import homework_1.repositories.GoalRepository;
import homework_1.repositories.TransactionRepository;
import homework_1.repositories.UserRepository;
import homework_1.repositories.in_memory.InMemoryBudgetRepository;
import homework_1.repositories.in_memory.InMemoryGoalRepository;
import homework_1.repositories.in_memory.InMemoryTransactionRepository;
import homework_1.repositories.in_memory.InMemoryUserRepository;
import homework_1.repositories.jdbc.JdbcBudgetRepository;
import homework_1.repositories.jdbc.JdbcGoalRepository;
import homework_1.repositories.jdbc.JdbcTransactionRepository;
import homework_1.repositories.jdbc.JdbcUserRepository;
import homework_1.services.*;
import homework_1.services.impl.*;
import homework_1.ui.ConsoleAdapter;

import java.sql.Connection;
import java.sql.SQLException;

public class ApplicationDB {
    public static void main(String[] args) throws SQLException {
        LiquibaseMigrationRunner.runMigrations();

        Connection connection = ConnectionManager.getConnection();
        UserRepository userRepository = new JdbcUserRepository(connection);
        TransactionRepository transactionRepository = new JdbcTransactionRepository(connection);
        GoalRepository goalRepository = new JdbcGoalRepository(connection);
        BudgetRepository budgetRepository = new JdbcBudgetRepository(connection);

        NotificationService notificationService = new NotificationServiceImpl();
        BudgetService budgetService = new BudgetServiceImpl(budgetRepository, transactionRepository);
        AuthService authService = new AuthServiceImpl(userRepository);
        TransactionService transactionService = new TransactionServiceImpl(transactionRepository,
                budgetService, notificationService);
        GoalService goalService = new GoalServiceImpl(goalRepository);
        AnalyticsService analyticsService = new AnalyticsServiceImpl(transactionRepository);

        ConsoleAdapter consoleAdapter = new ConsoleAdapter(authService, transactionService,
                goalService, analyticsService, budgetService);
        consoleAdapter.start();
    }
}
