package homework_1.ui;

import homework_1.common.utils.Validator;
import homework_1.domain.*;
import homework_1.services.TransactionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class TransactionConsoleHandler {
    private final TransactionService transactionService;
    private final Scanner scanner;

    public TransactionConsoleHandler(TransactionService transactionService, Scanner scanner) {
        this.transactionService = transactionService;
        this.scanner = scanner;
    }

    public void createTransaction(User user) {
        try {
            System.out.println("Введите сумму:");
            double amount = Double.parseDouble(scanner.nextLine());
            if (!Validator.isValidAmount(amount)) {
                System.out.println("Ошибка: Сумма должна быть положительной.");
                return;
            }

            System.out.println("Выберите тип (1 - доход, 2 - расход):");
            int typeChoice = Integer.parseInt(scanner.nextLine());
            TransactionType type = Validator.getTransactionType(typeChoice);
            if (type == null) {
                System.out.println("Ошибка: Некорректный тип транзакции.");
                return;
            }

            System.out.println("Выберите категорию: (FOOD, HEALTH, ENTERTAINMENT, TRANSPORT, SALARY, OTHER)");
            String categoryInput = scanner.nextLine();
            Category category = Validator.getCategory(categoryInput);
            if (category == null) {
                System.out.println("Ошибка: Некорректная категория.");
                return;
            }

            System.out.println("Введите описание:");
            String description = scanner.nextLine();

            Transaction transaction = new Transaction(1,
                    user.getId(), amount, type, category, LocalDate.now(), description
            );

            transactionService.createTransaction(transaction);
            System.out.println("Транзакция добавлена.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Некорректный формат ввода.");
        }
    }

    public void showTransactions(User user) {
        List<Transaction> transactions = transactionService.getTransactions(user.getId());
        transactions.forEach(System.out::println);
    }

    public void showBalance(User user) {
        double balance = transactionService.calculateBalance(user.getId());
        System.out.println("Текущий баланс: " + balance);
    }
}