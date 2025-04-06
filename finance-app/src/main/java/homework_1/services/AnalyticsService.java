package homework_1.services;

import java.time.LocalDate;

/**
 * Интерфейс сервиса для финансовой аналитики.
 */
public interface AnalyticsService {

    /**
     * Возвращает сумму доходов пользователя за указанный период.
     *
     * @param userId    Id пользователя
     * @param start начальная дата
     * @param end   конечная дата
     * @return сумма доходов
     */
    double getTotalIncome(long userId, LocalDate start, LocalDate end);

    /**
     * Возвращает сумму расходов пользователя за указанный период.
     *
     * @param userId    Id пользователя
     * @param start начальная дата
     * @param end   конечная дата
     * @return сумма расходов
     */
    double getTotalExpenses(long userId, LocalDate start, LocalDate end);

    /**
     * Анализ расходов по категориям.
     *
     * @param userId Id пользователя
     * @return строка с аналитикой по категориям
     */
    String analyzeExpensesByCategory(long userId);

    /**
     * Генерирует отчёт о текущем финансовом состоянии.
     *
     * @param userId Id пользователя
     * @return текстовый отчёт
     */
    String generateFinancialReport(long userId);
}