package homework_1;

import homework_1.repositories.BudgetRepository;
import homework_1.repositories.GoalRepository;
import homework_1.repositories.in_memory.InMemoryBudgetRepository;
import homework_1.services.NotificationService;
import homework_1.repositories.in_memory.InMemoryGoalRepository;
import homework_1.repositories.in_memory.InMemoryTransactionRepository;
import homework_1.repositories.in_memory.InMemoryUserRepository;
import homework_1.repositories.TransactionRepository;
import homework_1.repositories.UserRepository;
import homework_1.services.*;
import homework_1.services.impl.*;
import homework_1.ui.ConsoleAdapter;

public class Application {
    public static void main(String[] args) {
        UserRepository userRepository = new InMemoryUserRepository();
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();
        GoalRepository goalRepository = new InMemoryGoalRepository();
        BudgetRepository budgetRepository = new InMemoryBudgetRepository();
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