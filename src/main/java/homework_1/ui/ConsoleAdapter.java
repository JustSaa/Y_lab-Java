package homework_1.ui;

import homework_1.services.AnalyticsService;
import homework_1.services.AuthService;
import homework_1.services.GoalService;
import homework_1.services.TransactionInputPort;

import java.util.Scanner;

/**
 * Консольный интерфейс пользователя.
 */
public class ConsoleAdapter {
    private final AuthConsoleHandler authHandler;
    private final TransactionConsoleHandler transactionHandler;
    private final GoalConsoleHandler goalHandler;
    private final AdminConsoleHandler adminHandler;
    private final AnalyticsConsoleHandler analyticsHandler;
    private final BudgetConsoleHandler budgetConsoleHandler;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleAdapter(AuthService authService, TransactionInputPort transactionService,
                          GoalService goalService, AnalyticsService analyticsService) {
        this.authHandler = new AuthConsoleHandler(authService, scanner);
        this.transactionHandler = new TransactionConsoleHandler(transactionService, scanner);
        this.goalHandler = new GoalConsoleHandler(goalService, scanner);
        this.adminHandler = new AdminConsoleHandler(authService, scanner);
        this.analyticsHandler = new AnalyticsConsoleHandler(analyticsService, scanner);
        this.budgetConsoleHandler = new BudgetConsoleHandler(authService, transactionService, scanner);
    }

    public void start() {
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
        System.out.println("1 - Регистрация, 2 - Вход, 0 - Выход");
        int command = Integer.parseInt(scanner.nextLine());
        switch (command) {
            case 1 -> authHandler.register();
            case 2 -> authHandler.login();
            case 0 -> {
                System.out.println("Выход...");
                return false;
            }
            default -> System.out.println("Некорректный ввод");
        }
        return true;
    }

    private boolean mainMenu() {
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

        int choice = Integer.parseInt(scanner.nextLine());
        switch (choice) {
            case 1 -> transactionHandler.createTransaction(authHandler.getCurrentUser());
            case 2 -> transactionHandler.showTransactions(authHandler.getCurrentUser());
            case 3 -> transactionHandler.showBalance(authHandler.getCurrentUser());
            case 4 -> authHandler.editProfile();
            case 5 -> authHandler.deleteAccount();
            case 6 -> goalHandler.createGoal(authHandler.getCurrentUser());
            case 7 -> goalHandler.showGoals(authHandler.getCurrentUser());
            case 8 -> goalHandler.addToGoal();
            case 9 -> goalHandler.deleteGoal();
            case 10 -> budgetConsoleHandler.setBudget(authHandler.getCurrentUser());
            case 11 -> budgetConsoleHandler.checkBudget(authHandler.getCurrentUser());
            case 12 -> {
                if (authHandler.getCurrentUser().isAdmin()) {
                    adminHandler.showAllUsers();
                } else {
                    System.out.println("Ошибка: У вас нет прав администратора.");
                }
            }
            case 13 -> {
                if (authHandler.getCurrentUser().isAdmin()) {
                    adminHandler.blockUser(authHandler.getCurrentUser());
                } else {
                    System.out.println("Ошибка: У вас нет прав администратора.");
                }
            }
            case 14 -> {
                if (authHandler.getCurrentUser().isAdmin()) {
                    adminHandler.deleteUser(authHandler.getCurrentUser());
                } else {
                    System.out.println("Ошибка: У вас нет прав администратора.");
                }
            }
            case 15 -> analyticsHandler.showFinancialReport(authHandler.getCurrentUser());
            case 16 -> analyticsHandler.showCategoryAnalysis(authHandler.getCurrentUser());
            case 17 -> analyticsHandler.showIncomeAndExpensesForPeriod(authHandler.getCurrentUser());
            case 0 -> {
                System.out.println("Выход...");
                return false;
            }
            default -> System.out.println("Некорректный ввод");
        }
        return true;
    }
}