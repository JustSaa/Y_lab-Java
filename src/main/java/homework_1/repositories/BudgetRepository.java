package homework_1.repositories;

import homework_1.domain.Budget;

import java.util.Optional;

/**
 * Репозиторий для хранения бюджетов пользователей.
 */
public interface BudgetRepository {

    /**
     * Сохраняет или обновляет бюджет пользователя.
     *
     * @param budget объект бюджета
     */
    void save(Budget budget);

    /**
     * Получает бюджет пользователя.
     *
     * @param userEmail почта пользователя
     * @return объект бюджета (если есть)
     */
    Optional<Budget> findByUserEmail(String userEmail);
}