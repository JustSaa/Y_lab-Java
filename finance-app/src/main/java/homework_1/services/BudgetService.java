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
     * @param userId Id пользователя
     * @param limit  сумма бюджета
     */
    Budget createUserBudget(long userId, double limit);

    /**
     * Получает текущий бюджет пользователя.
     *
     * @param userId Id пользователя
     * @return объект бюджета (если установлен)
     */
    Budget getUserBudget(long userId);

    /**
     * Проверяет, превышен ли бюджет пользователя.
     *
     * @param userId Id пользователя
     * @return true, если расходы превышают бюджет, иначе false
     */
    boolean isBudgetExceeded(long userId);
}