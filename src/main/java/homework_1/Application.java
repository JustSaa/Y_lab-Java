package homework_1;

import homework_1.repositories.GoalRepository;
import homework_1.repositories.NotificationService;
import homework_1.repositories.in_memory.InMemoryGoalRepository;
import homework_1.repositories.in_memory.InMemoryTransactionRepository;
import homework_1.repositories.in_memory.InMemoryUserRepository;
import homework_1.repositories.TransactionRepository;
import homework_1.repositories.UserRepository;
import homework_1.services.*;
import homework_1.ui.ConsoleAdapter;

public class Application {
    public static void main(String[] args) {
        UserRepository userRepository = new InMemoryUserRepository();
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();
        GoalRepository goalRepository = new InMemoryGoalRepository();
        NotificationService notificationService = new NotificationServiceImplementation();

        AuthServiceImplementation authServiceImplementation = new AuthServiceImplementation(userRepository);
        TransactionService transactionService = new TransactionService(transactionRepository,
                userRepository, notificationService);
        GoalService goalService = new GoalService(goalRepository);
        AnalyticsService analyticsService = new AnalyticsServiceImplementation(transactionRepository);

        ConsoleAdapter consoleAdapter = new ConsoleAdapter(authServiceImplementation, transactionService,
                goalService, analyticsService);
        consoleAdapter.start();
    }
}