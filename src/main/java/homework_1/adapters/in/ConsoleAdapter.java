package homework_1.adapters.in;

import homework_1.application.ports.in.AuthInputPort;
import homework_1.application.ports.in.TransactionInputPort;
import homework_1.common.exceptions.AuthenticationException;
import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Консольный интерфейс пользователя.
 */
public class ConsoleAdapter {

    private final AuthInputPort authService;
    private final TransactionInputPort transactionService;
    private final Scanner scanner = new Scanner(System.in);
    private User currentUser;

    public ConsoleAdapter(AuthInputPort authService, TransactionInputPort transactionService) {
        this.authService = authService;
        this.transactionService = transactionService;
    }

    public void start() {
        while (true) {
            if (currentUser == null) {
                authMenu();
            } else {
                mainMenu();
            }
        }
    }

    private void authMenu() {
        System.out.println("1 - Регистрация, 2 - Вход, 0 - Выход");
        int command = Integer.parseInt(scanner.nextLine());
        switch (command) {
            case 1 -> register();
            case 2 -> login();
            case 0 -> {
                System.out.println("Выход...");
                System.exit(0);
            }
            default -> System.out.println("Некорректный ввод");
        }
    }

    private void register() {
        System.out.println("Введите имя:");
        String name = scanner.nextLine();
        System.out.println("Введите email:");
        String email = scanner.nextLine();
        System.out.println("Введите пароль:");
        String password = scanner.nextLine();

        try {
            currentUser = authService.register(name, email, password);
            System.out.println("Вы успешно зарегистрированы: " + currentUser.getEmail());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void login() {
        System.out.println("Введите email:");
        String email = scanner.nextLine();
        System.out.println("Введите пароль:");
        String password = scanner.nextLine();

        try {
            currentUser = authService.login(email, password);
            System.out.println("Добро пожаловать, " + currentUser.getName());
        } catch (AuthenticationException e) {
            System.out.println("Ошибка входа: " + e.getMessage());
        }
    }

    private void mainMenu() {
        System.out.println("1. Добавить транзакцию");
        System.out.println("2. Просмотреть транзакции");
        System.out.println("3. Показать баланс");
        System.out.println("0. Выход");

        int choice = Integer.parseInt(scanner.nextLine());
        switch (choice) {
            case 1 -> createTransaction();
            case 2 -> showTransactions();
            case 3 -> showBalance();
            case 0 -> currentUser = null;
            default -> System.out.println("Некорректный ввод");
        }
    }

    private void createTransaction() {
        System.out.println("Введите сумму:");
        double amount = Double.parseDouble(scanner.nextLine());

        System.out.println("Выберите тип (1 - доход, 2 - расход):");
        int typeChoice = Integer.parseInt(scanner.nextLine());
        TransactionType type = (typeChoice == 1) ? TransactionType.INCOME : TransactionType.EXPENSE;

        System.out.println("Выберите категорию (FOOD, HEALTH, ENTERTAINMENT, TRANSPORT, SALARY, OTHER):");
        Category category = Category.valueOf(scanner.nextLine().toUpperCase());

        System.out.println("Введите описание:");
        String description = scanner.nextLine();

        Transaction transaction = new Transaction(
                currentUser.getId(), amount, type, category, LocalDate.now(), description);

        transactionService.createTransaction(transaction);
        System.out.println("Транзакция добавлена.");
    }

    private void showTransactions() {
        List<Transaction> transactions = transactionService.getTransactions(currentUser.getId());
        transactions.forEach(System.out::println);
    }

    private void showBalance() {
        double balance = transactionService.calculateBalance(currentUser.getId());
        System.out.println("Текущий баланс: " + balance);
    }
}