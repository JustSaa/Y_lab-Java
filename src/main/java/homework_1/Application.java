package homework_1;

import homework_1.adapters.out.InMemoryTransactionRepository;
import homework_1.adapters.out.InMemoryUserRepository;
import homework_1.application.ports.out.TransactionRepository;
import homework_1.application.ports.out.UserRepository;
import homework_1.application.services.AuthService;
import homework_1.application.services.TransactionService;
import homework_1.adapters.in.ConsoleAdapter;

public class Application {
    public static void main(String[] args) {
        UserRepository userRepository = new InMemoryUserRepository();
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();

        AuthService authService = new AuthService(userRepository);
        TransactionService transactionService = new TransactionService(transactionRepository);

        ConsoleAdapter consoleAdapter = new ConsoleAdapter(authService, transactionService);
        consoleAdapter.start();
    }
}