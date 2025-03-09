package homework_1.services;

/**
 * Интерфейс сервиса для финансовой аналитики.
 */
public interface AnalyticsService {

    /**
     * Возвращает сумму доходов пользователя за указанный период.
     *
     * @param userEmail email пользователя
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return сумма доходов
     */
    double getTotalIncome(String userEmail, String startDate, String endDate);

    /**
     * Возвращает сумму расходов пользователя за указанный период.
     *
     * @param userEmail email пользователя
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return сумма расходов
     */
    double getTotalExpenses(String userEmail, String startDate, String endDate);

    /**
     * Анализ расходов по категориям.
     *
     * @param userEmail email пользователя
     * @return строка с аналитикой по категориям
     */
    String analyzeExpensesByCategory(String userEmail);

    /**
     * Генерирует отчёт о текущем финансовом состоянии.
     *
     * @param userEmail email пользователя
     * @return текстовый отчёт
     */
    String generateFinancialReport(String userEmail);
}