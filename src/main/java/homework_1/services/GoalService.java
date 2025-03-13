package homework_1.services;

import homework_1.domain.Goal;

import java.util.List;
import java.util.UUID;

/**
 * Сервис управления финансовыми целями.
 */
public interface GoalService {

    /**
     * Создаёт новую цель.
     *
     * @param userEmail    почта пользователя
     * @param name         название цели
     * @param targetAmount сумма, которую нужно накопить
     */
    void createGoal(String userEmail, String name, double targetAmount);

    /**
     * Возвращает все цели пользователя.
     *
     * @param userEmail почта пользователя
     * @return список целей
     */
    List<Goal> getUserGoals(String userEmail);

    /**
     * Пополняет сумму накопления цели.
     *
     * @param goalId идентификатор цели
     * @param amount сумма для пополнения
     */
    void addToGoal(UUID goalId, double amount);

    /**
     * Удаляет цель.
     *
     * @param goalId идентификатор цели
     */
    void deleteGoal(UUID goalId);
}
