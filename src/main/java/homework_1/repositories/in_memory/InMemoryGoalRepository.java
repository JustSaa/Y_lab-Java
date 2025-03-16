package homework_1.repositories.in_memory;

import homework_1.domain.Goal;
import homework_1.repositories.GoalRepository;

import java.util.*;

/**
 * Реализация GoalRepository на основе HashMap (хранение в памяти).
 */
public class InMemoryGoalRepository implements GoalRepository {

    /**
     * Хранит цели пользователей, привязанные к их уникальному идентификатору.
     */
    private final Map<Long, Goal> goals = new HashMap<>();

    /**
     * Сохраняет новую цель.
     *
     * @param goal цель для сохранения
     */
    @Override
    public void save(Goal goal) {
        goals.put(goal.getId(), goal);
    }

    /**
     * Ищет цель по её идентификатору.
     *
     * @param goalId идентификатор цели
     * @return {@link Optional}, содержащий цель, если она найдена
     */
    @Override
    public Optional<Goal> findById(long goalId) {
        return Optional.ofNullable(goals.get(goalId));
    }

    /**
     * Ищет цель по её имени.
     *
     * @param name идентификатор цели
     * @return {@link Optional}, содержащий цель, если она найдена
     */
    @Override
    public Optional<Goal> findByName(String name) {
        return goals.values().stream()
                .filter(goal -> goal.getName().equals(name))
                .findFirst();
    }

    /**
     * Возвращает список всех целей пользователя.
     *
     * @param email почта пользователя
     * @return список целей пользователя
     */
    @Override
    public List<Goal> findByUserEmail(String email) {
        return goals.values().stream()
                .filter(goal -> goal.getUserEmail().equals(email))
                .toList();
    }

    /**
     * Обновляет данные существующей цели.
     *
     * @param goal обновлённые данные цели
     */
    @Override
    public void update(Goal goal) {
        goals.put(goal.getId(), goal);
    }

    /**
     * Удаляет цель по её идентификатору.
     *
     * @param goalId идентификатор цели
     */
    @Override
    public void delete(long goalId) {
        goals.remove(goalId);
    }
}