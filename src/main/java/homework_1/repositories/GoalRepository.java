package homework_1.repositories;

import homework_1.domain.Goal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для управления финансовыми целями пользователей.
 */
public interface GoalRepository {
    /**
     * Сохраняет новую цель.
     *
     * @param goal цель для сохранения
     */
    void save(Goal goal);

    /**
     * Ищет цель по её идентификатору.
     *
     * @param goalId идентификатор цели
     * @return {@link Optional}, содержащий цель, если она найдена
     */
    Optional<Goal> findById(UUID goalId);

    /**
     * Возвращает список всех целей пользователя.
     *
     * @param email почта пользователя
     * @return список целей пользователя
     */
    List<Goal> findByUserEmail(String email);

    /**
     * Обновляет данные существующей цели.
     *
     * @param goal обновлённые данные цели
     */
    void update(Goal goal);

    /**
     * Удаляет цель по её идентификатору.
     *
     * @param goalId идентификатор цели
     */
    void delete(UUID goalId);
}