package homework_1.ui;

import homework_1.domain.User;
import homework_1.services.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Консольный интерфейс пользователя.
 */
public class ConsoleAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleAdapter.class);
    private final AuthConsoleHandler authHandler;
    private final TransactionConsoleHandler transactionHandler;
    private final GoalConsoleHandler goalHandler;
    private final AdminConsoleHandler adminHandler;
    private final AnalyticsConsoleHandler analyticsHandler;
    private final BudgetConsoleHandler budgetConsoleHandler;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleAdapter(AuthService authService, TransactionService transactionService,
                          GoalService goalService, AnalyticsService analyticsService,
                          BudgetService budgetService) {
        this.authHandler = new AuthConsoleHandler(authService, scanner);
        this.transactionHandler = new TransactionConsoleHandler(transactionService, scanner);
        this.goalHandler = new GoalConsoleHandler(goalService, scanner);
        this.adminHandler = new AdminConsoleHandler(authService, scanner);
        this.analyticsHandler = new AnalyticsConsoleHandler(analyticsService, scanner);
        this.budgetConsoleHandler = new BudgetConsoleHandler(budgetService, scanner);
    }

    public void start() throws SQLException {
        boolean running = true;
        while (running) {
            if (authHandler.getCurrentUser() == null) {
                running = authMenu();
            } else {
                running = mainMenu();
            }
        }
    }

    private boolean authMenu() {
        while (true) {
            System.out.println("1 - Регистрация, 2 - Вход, 0 - Выход");
            int command = getIntInput();
            switch (command) {
                case 1 -> {
                    User newUser = authHandler.register();
                    if (newUser != null) {
                        logger.info("Пользователь {} успешно зарегистрирован", newUser.getEmail());
                        return true;
                    }
                }
                case 2 -> {
                    User loggedInUser = authHandler.login();
                    if (loggedInUser != null) {
                        logger.info("Пользователь {} вошел в систему", loggedInUser.getEmail());
                        return true;
                    }
                }
                case 0 -> {
                    logger.info("Выход из системы");
                    System.out.println("Выход...");
                    return false;
                }
                default -> {
                    logger.warn("Некорректный ввод в меню авторизации");
                    System.out.println("Некорректный ввод, попробуйте снова.");
                }
            }
        }
    }

    private boolean mainMenu() throws SQLException {
        while (true) {
            System.out.println("1. Добавить транзакцию");
            System.out.println("2. Просмотреть транзакции");
            System.out.println("3. Показать баланс");
            System.out.println("4. Редактировать профиль");
            System.out.println("5. Удалить аккаунт");
            System.out.println("6. Создать финансовую цель");
            System.out.println("7. Просмотреть цели");
            System.out.println("8. Пополнить цель");
            System.out.println("9. Удалить цель");
            System.out.println("10. Установить бюджет");
            System.out.println("11. Проверить превышение бюджета");

            if (authHandler.getCurrentUser().isAdmin()) {
                System.out.println("12. Просмотреть всех пользователей (админ)");
                System.out.println("13. Заблокировать пользователя (админ)");
                System.out.println("14. Удалить пользователя (админ)");
            }

            System.out.println("15. Просмотреть финансовый отчёт");
            System.out.println("16. Анализ расходов по категориям");
            System.out.println("17. Доходы и расходы за период");
            System.out.println("0. Выход");

            int choice = getIntInput();
            switch (choice) {
                case 1 -> {
                    transactionHandler.createTransaction(authHandler.getCurrentUser());
                    logger.info("Транзакция добавлена пользователем {}", authHandler.getCurrentUser().getEmail());
                }
                case 2 -> transactionHandler.showTransactions(authHandler.getCurrentUser());
                case 3 -> transactionHandler.showBalance(authHandler.getCurrentUser());
                case 4 -> {
                    authHandler.editProfile();
                    logger.info("Пользователь {} изменил профиль", authHandler.getCurrentUser().getEmail());
                }
                case 5 -> {
                    logger.warn("Пользователь {} удаляет аккаунт", authHandler.getCurrentUser().getEmail());
                    authHandler.deleteAccount();
                }
                case 6 -> goalHandler.createGoal(authHandler.getCurrentUser());
                case 7 -> goalHandler.showGoals(authHandler.getCurrentUser());
                case 8 -> goalHandler.addToGoal();
                case 9 -> goalHandler.deleteGoal();
                case 10 -> budgetConsoleHandler.setBudget(authHandler.getCurrentUser());
                case 11 -> budgetConsoleHandler.checkBudget(authHandler.getCurrentUser());
                case 12 -> {
                    if (authHandler.getCurrentUser().isAdmin()) {
                        adminHandler.showAllUsers();
                        logger.info("Администратор {} просмотрел список пользователей", authHandler.getCurrentUser().getEmail());
                    } else {
                        System.out.println("Ошибка: У вас нет прав администратора.");
                    }
                }
                case 13 -> {
                    if (authHandler.getCurrentUser().isAdmin()) {
                        adminHandler.blockUser(authHandler.getCurrentUser());
                        logger.warn("Администратор {} заблокировал пользователя", authHandler.getCurrentUser().getEmail());
                    } else {
                        System.out.println("Ошибка: У вас нет прав администратора.");
                    }
                }
                case 14 -> {
                    if (authHandler.getCurrentUser().isAdmin()) {
                        adminHandler.deleteUser(authHandler.getCurrentUser());
                        logger.warn("Администратор {} удалил пользователя", authHandler.getCurrentUser().getEmail());
                    } else {
                        System.out.println("Ошибка: У вас нет прав администратора.");
                    }
                }
                case 15 -> analyticsHandler.showFinancialReport(authHandler.getCurrentUser());
                case 16 -> analyticsHandler.showCategoryAnalysis(authHandler.getCurrentUser());
                case 17 -> analyticsHandler.showIncomeAndExpensesForPeriod(authHandler.getCurrentUser());
                case 0 -> {
                    logger.info("Выход из системы пользователем {}", authHandler.getCurrentUser().getEmail());
                    System.out.println("Выход...");
                    return false;
                }
                default -> {
                    logger.warn("Некорректный ввод в главном меню");
                    System.out.println("Некорректный ввод, попробуйте снова.");
                }
            }
        }
    }

    /**
     * Безопасный ввод чисел: если пользователь вводит не число, программа не падает.
     */
    private int getIntInput() {
        while (true) {
            try {
                System.out.print("Введите число: ");
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                logger.warn("Ошибка: пользователь ввел некорректное число.");
                System.out.println("Ошибка: введите корректное число.");
            }
        }
    }
}