package homework_1.ui;

import homework_1.domain.User;
import homework_1.services.AuthService;
import homework_1.services.BudgetService;
import homework_1.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Обработчик консольного взаимодействия с бюджетом пользователя.
 */
public class BudgetConsoleHandler {
    private static final Logger logger = LoggerFactory.getLogger(BudgetConsoleHandler.class);

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
            logger.info("Бюджет пользователя {} установлен: {}", user.getEmail(), budget);
            System.out.println("Бюджет установлен: " + budget);
        } catch (NumberFormatException e) {
            logger.warn("Ошибка: некорректный ввод бюджета.");
            System.out.println("Ошибка: Некорректный ввод числа.");
        } catch (IllegalArgumentException e) {
            logger.warn("Ошибка установки бюджета: {}", e.getMessage());
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
            logger.warn("Ошибка: пользователь не авторизован.");
            System.out.println("Ошибка: пользователь не авторизован.");
            return;
        }

        boolean exceeded = budgetService.isBudgetExceeded(user.getId());

        if (exceeded) {
            logger.info("Внимание! Пользователь {} превысил бюджет.", user.getEmail());
            System.out.println("Внимание! Ваши расходы превысили установленный бюджет.");
        } else {
            logger.info("Бюджет пользователя {} не превышен.", user.getEmail());
            System.out.println("Бюджет не превышен.");
        }
    }
}