package homework_1.ui;

import homework_1.services.AnalyticsService;
import homework_1.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Обработчик консольных команд для аналитики и отчетов.
 */
public class AnalyticsConsoleHandler {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsConsoleHandler.class);
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
            logger.warn("Попытка просмотра отчёта без авторизации.");
            System.out.println("Ошибка: Необходимо войти в систему.");
            return;
        }
        logger.info("Генерация финансового отчёта для пользователя ID {}", currentUser.getId());
        String report = analyticsService.generateFinancialReport(currentUser.getId());
        logger.info("Отчёт: \n{}", report);
        System.out.println(report);
    }

    /**
     * Позволяет пользователю выбрать период для просмотра доходов и расходов.
     *
     * @param currentUser текущий авторизованный пользователь
     */
    public void showIncomeAndExpensesForPeriod(User currentUser) {
        if (currentUser == null) {
            logger.warn("Попытка просмотра доходов и расходов без авторизации.");
            System.out.println("Ошибка: Необходимо войти в систему.");
            return;
        }

        System.out.println("Введите начальную дату (ГГГГ-ММ-ДД):");
        String startDate = scanner.nextLine();
        System.out.println("Введите конечную дату (ГГГГ-ММ-ДД):");
        String endDate = scanner.nextLine();

        logger.info("Запрос доходов и расходов для пользователя ID {} за период {} - {}",
                currentUser.getId(), startDate, endDate);
        double income = analyticsService.getTotalIncome(currentUser.getId(), startDate, endDate);
        double expenses = analyticsService.getTotalExpenses(currentUser.getId(), startDate, endDate);

        logger.info("Доход: {} | Расход: {}", income, expenses);
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
            logger.warn("Попытка просмотра анализа расходов без авторизации.");
            System.out.println("Ошибка: Необходимо войти в систему.");
            return;
        }

        logger.info("Анализ расходов по категориям для пользователя ID {}", currentUser.getId());
        String report = analyticsService.analyzeExpensesByCategory(currentUser.getId());

        logger.info("Анализ расходов:\n{}", report);
        System.out.println(report);
    }
}