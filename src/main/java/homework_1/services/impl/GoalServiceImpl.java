package homework_1.services.impl;

import homework_1.domain.Goal;
import homework_1.repositories.GoalRepository;
import homework_1.services.GoalService;

import java.util.List;
import java.util.UUID;

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
    public List<Goal> getUserGoals(String userEmail) {
        return goalRepository.findByUserEmail(userEmail);
    }

    /**
     * Пополняет сумму накопления цели.
     *
     * @param goalId идентификатор цели
     * @param amount сумма для пополнения
     */
    public void addToGoal(UUID goalId, double amount) {
        Goal goal = goalRepository.findById(goalId)
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
    public void deleteGoal(UUID goalId) {
        goalRepository.delete(goalId);
    }
}