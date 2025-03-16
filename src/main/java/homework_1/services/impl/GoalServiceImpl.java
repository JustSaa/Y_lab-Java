package homework_1.services.impl;

import homework_1.domain.Goal;
import homework_1.repositories.GoalRepository;
import homework_1.services.GoalService;

import java.sql.SQLException;
import java.util.List;

/**
 * Сервис управления финансовыми целями.
 */
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;

    public GoalServiceImpl(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    /**
     * Создаёт новую цель.
     *
     * @param userEmail    почта пользователя
     * @param name         название цели
     * @param targetAmount сумма, которую нужно накопить
     */
    public void createGoal(String userEmail, String name, double targetAmount) {
        if (targetAmount <= 0) {
            throw new IllegalArgumentException("Цель должна быть положительной.");
        }
        Goal goal = new Goal(userEmail, name, targetAmount);
        goalRepository.save(goal);
    }

    /**
     * Возвращает все цели пользователя.
     *
     * @param userEmail почта пользователя
     * @return список целей
     */
    public List<Goal> getUserGoals(String userEmail) throws SQLException {
        return goalRepository.findByUserEmail(userEmail);
    }

    /**
     * Пополняет сумму накопления цели.
     *
     * @param goalName название цели
     * @param amount   сумма для пополнения
     */
    public void addToGoal(String goalName, double amount) {
        Goal goal = goalRepository.findByName(goalName)
                .orElseThrow(() -> new IllegalArgumentException("Цель не найдена."));

        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной.");
        }

        goal.addToGoal(amount);
        goalRepository.update(goal);
    }

    /**
     * Удаляет цель.
     *
     * @param goalId идентификатор цели
     */
    public void deleteGoal(long goalId) {
        goalRepository.delete(goalId);
    }

    @Override
    public void updateGoal(long goalId) {

    }
}