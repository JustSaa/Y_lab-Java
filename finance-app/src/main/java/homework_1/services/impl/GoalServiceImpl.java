package homework_1.services.impl;

import audit.aspect.annotation.Audit;
import audit.aspect.annotation.LogExecutionTime;
import homework_1.domain.Goal;
import homework_1.repositories.GoalRepository;
import homework_1.services.GoalService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Сервис управления финансовыми целями.
 */
@Service
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;

    public GoalServiceImpl(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    /**
     * Создаёт новую цель.
     *
     * @param userId       Id пользователя
     * @param name         название цели
     * @param targetAmount сумма, которую нужно накопить
     */
    @Audit(action = "Создать цель")
    @LogExecutionTime
    public Goal createGoal(long userId, String name, double targetAmount) {
        if (targetAmount <= 0) {
            throw new IllegalArgumentException("Цель должна быть положительной.");
        }
        Goal goal = new Goal(userId, name, targetAmount);
        goalRepository.save(goal);
        return goal;
    }

    /**
     * Возвращает все цели пользователя.
     *
     * @param userId Id пользователя
     * @return список целей
     */
    @Audit(action = "Получить цель по ID пользователя")
    @LogExecutionTime
    public List<Goal> getUserGoals(long userId) throws SQLException {
        return goalRepository.findByUserId(userId);
    }

    /**
     * Пополняет сумму накопления цели.
     *
     * @param goalName название цели
     * @param amount   сумма для пополнения
     */
    @Audit(action = "Обновить цель")
    @LogExecutionTime
    public void addToGoal(String goalName, double amount) {
        goalRepository.findByName(goalName)
                .ifPresentOrElse(goal -> {
                    if (amount > 0) {
                        goal.addToGoal(amount);
                        goalRepository.update(goal);
                    } else {
                        System.out.println("⚠️ Ошибка: сумма должна быть положительной.");
                    }
                }, () -> System.out.println("⚠️ Ошибка: цель не найдена."));
    }

    /**
     * Удаляет цель.
     *
     * @param goalId идентификатор цели
     */
    @Audit(action = "Удалить цель по ID")
    @LogExecutionTime
    public void deleteGoal(long goalId) {
        goalRepository.delete(goalId);
    }

    @Override
    public void updateGoal(long goalId) {

    }
}