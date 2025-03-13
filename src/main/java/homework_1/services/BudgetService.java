package homework_1.services;

import homework_1.domain.Budget;

import java.util.Optional;

/**
 * Сервис управления бюджетом пользователя.
 */
public interface BudgetService {

    /**
     * Устанавливает бюджет для пользователя.
     *
     * @param userEmail почта пользователя
     * @param limit     сумма бюджета
     */
    void setUserBudget(String userEmail, double limit);

    /**
     * Получает текущий бюджет пользователя.
     *
     * @param userEmail почта пользователя
     * @return объект бюджета (если установлен)
     */
    Optional<Budget> getUserBudget(String userEmail);

    /**
     * Проверяет, превышен ли бюджет пользователя.
     *
     * @param userEmail почта пользователя
     * @return true, если расходы превышают бюджет, иначе false
     */
    boolean isBudgetExceeded(String userEmail);
}