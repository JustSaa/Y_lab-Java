package homework_1.ui;

import homework_1.domain.User;
import homework_1.services.AuthService;
import homework_1.services.TransactionInputPort;

import java.util.Scanner;

/**
 * Обработчик консольного взаимодействия с бюджетом пользователя.
 */
public class BudgetConsoleHandler {
    private final AuthService authService;
    private final TransactionInputPort transactionService;
    private final Scanner scanner;

    public BudgetConsoleHandler(AuthService authService, TransactionInputPort transactionService, Scanner scanner) {
        this.authService = authService;
        this.transactionService = transactionService;
        this.scanner = scanner;
    }

    /**
     * Установка месячного бюджета пользователя.
     *
     * @param user текущий пользователь
     */
    public void setBudget(User user) {

        System.out.println("Введите новый месячный бюджет:");
        try {
            double budget = Double.parseDouble(scanner.nextLine());
            authService.setUserBudget(user.getEmail(), budget);
            System.out.println("Бюджет установлен: " + budget);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Некорректный ввод числа.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Проверка превышения бюджета пользователя.
     *
     * @param user текущий пользователь
     */
    public void checkBudget(User user) {
        if (user == null) {
            System.out.println("Ошибка: пользователь не авторизован.");
            return;
        }

        boolean exceeded = transactionService.isBudgetExceeded(user.getEmail());

        if (exceeded) {
            System.out.println("Внимание! Ваши расходы превысили установленный бюджет.");
        } else {
            System.out.println("Бюджет не превышен.");
        }
    }
}