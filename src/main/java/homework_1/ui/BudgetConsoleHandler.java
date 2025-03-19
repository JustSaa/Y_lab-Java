package homework_1.ui;

import homework_1.domain.User;
import homework_1.services.AuthService;
import homework_1.services.BudgetService;
import homework_1.services.TransactionService;

import java.util.Scanner;

/**
 * Обработчик консольного взаимодействия с бюджетом пользователя.
 */
public class BudgetConsoleHandler {
    private final BudgetService budgetService;
    private final Scanner scanner;

    public BudgetConsoleHandler(BudgetService budgetService, Scanner scanner) {
        this.budgetService = budgetService;
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
            budgetService.setUserBudget(user.getId(), budget);
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

        boolean exceeded = budgetService.isBudgetExceeded(user.getId());

        if (exceeded) {
            System.out.println("Внимание! Ваши расходы превысили установленный бюджет.");
        } else {
            System.out.println("Бюджет не превышен.");
        }
    }
}