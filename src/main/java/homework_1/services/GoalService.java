package homework_1.services;

import homework_1.domain.Goal;

import java.sql.SQLException;
import java.util.List;

/**
 * Сервис управления финансовыми целями.
 */
public interface GoalService {

    /**
     * Создаёт новую цель.
     *
     * @param userId       Id пользователя
     * @param name         название цели
     * @param targetAmount сумма, которую нужно накопить
     */
    Goal createGoal(long userId, String name, double targetAmount);

    /**
     * Возвращает все цели пользователя.
     *
     * @param userId почта пользователя
     * @return список целей
     */
    List<Goal> getUserGoals(long userId) throws SQLException;

    /**
     * Пополняет сумму накопления цели.
     *
     * @param nameGoal название цели
     * @param amount   сумма для пополнения
     */
    void addToGoal(String nameGoal, double amount);

    /**
     * Удаляет цель.
     *
     * @param goalId идентификатор цели
     */
    void deleteGoal(long goalId);

    /**
     * Обновляет цель.
     *
     * @param goalId идентификатор цели
     */
    void updateGoal(long goalId);
}
