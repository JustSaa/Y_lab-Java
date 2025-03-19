package homework_1.ui;

import homework_1.services.AnalyticsService;
import homework_1.domain.User;

import java.util.Scanner;

/**
 * Обработчик консольных команд для аналитики и отчетов.
 */
public class AnalyticsConsoleHandler {

    private final AnalyticsService analyticsService;
    private final Scanner scanner;

    public AnalyticsConsoleHandler(AnalyticsService analyticsService, Scanner scanner) {
        this.analyticsService = analyticsService;
        this.scanner = scanner;
    }

    /**
     * Отображает финансовый отчёт пользователя.
     *
     * @param currentUser текущий авторизованный пользователь
     */
    public void showFinancialReport(User currentUser) {
        if (currentUser == null) {
            System.out.println("Ошибка: Необходимо войти в систему.");
            return;
        }

        String report = analyticsService.generateFinancialReport(currentUser.getId());
        System.out.println(report);
    }

    /**
     * Позволяет пользователю выбрать период для просмотра доходов и расходов.
     *
     * @param currentUser текущий авторизованный пользователь
     */
    public void showIncomeAndExpensesForPeriod(User currentUser) {
        if (currentUser == null) {
            System.out.println("Ошибка: Необходимо войти в систему.");
            return;
        }

        System.out.println("Введите начальную дату (ГГГГ-ММ-ДД):");
        String startDate = scanner.nextLine();
        System.out.println("Введите конечную дату (ГГГГ-ММ-ДД):");
        String endDate = scanner.nextLine();

        double income = analyticsService.getTotalIncome(currentUser.getId(), startDate, endDate);
        double expenses = analyticsService.getTotalExpenses(currentUser.getId(), startDate, endDate);

        System.out.println("Доход за выбранный период: " + income);
        System.out.println("Расход за выбранный период: " + expenses);
    }

    /**
     * Показывает анализ расходов по категориям.
     *
     * @param currentUser текущий авторизованный пользователь
     */
    public void showCategoryAnalysis(User currentUser) {
        if (currentUser == null) {
            System.out.println("Ошибка: Необходимо войти в систему.");
            return;
        }

        String report = analyticsService.analyzeExpensesByCategory(currentUser.getId());
        System.out.println(report);
    }
}