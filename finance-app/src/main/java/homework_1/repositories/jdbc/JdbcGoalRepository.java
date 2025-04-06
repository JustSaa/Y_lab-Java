package homework_1.repositories.jdbc;

import homework_1.domain.Goal;
import homework_1.repositories.GoalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория {@link GoalRepository} для работы с финансовыми целями пользователей
 * с использованием JDBC и базы данных PostgreSQL.
 */
@Repository
public class JdbcGoalRepository implements GoalRepository {
    private static final Logger log = LoggerFactory.getLogger(JdbcGoalRepository.class);

    private static final String SQL_INSERT = """
                INSERT INTO finance.goals (id, user_id, name, target_amount, current_amount)
                VALUES (nextval('finance.goals_seq'), ?, ?, ?, ?)
            """;
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM finance.goals WHERE id = ?";
    private static final String SQL_SELECT_BY_NAME = "SELECT * FROM finance.goals WHERE name = ?";
    private static final String SQL_SELECT_BY_USER = "SELECT * FROM finance.goals WHERE user_id = ?";
    private static final String SQL_UPDATE = """
                UPDATE finance.goals
                SET target_amount = ?, current_amount = ?
                WHERE name = ?
            """;
    private static final String SQL_DELETE = "DELETE FROM finance.goals WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    public JdbcGoalRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Сохраняет новую финансовую цель пользователя в базе данных.
     *
     * @param goal объект {@link Goal}, содержащий данные о цели.
     * @throws RuntimeException если произошла ошибка при сохранении в БД.
     */
    @Override
    public void save(Goal goal) {
        int rows = jdbcTemplate.update(SQL_INSERT,
                goal.getUserId(), goal.getName(), goal.getTargetAmount(), goal.getCurrentAmount());

        if (rows == 0) {
            log.warn("Не удалось сохранить цель: {}", goal);
            throw new RuntimeException("Ошибка сохранения цели");
        }

        log.info("Цель сохранена: {}", goal);
    }

    /**
     * Ищет финансовую цель по её идентификатору.
     *
     * @param goalId уникальный идентификатор цели.
     * @return {@link Optional} с объектом {@link Goal}, если цель найдена, иначе пустой {@link Optional}.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public Optional<Goal> findById(long goalId) {
        try {
            Goal goal = jdbcTemplate.queryForObject(SQL_SELECT_BY_ID, this::mapGoal, goalId);
            return Optional.ofNullable(goal);
        } catch (EmptyResultDataAccessException ex) {
            log.info("Цель не найдена по ID={}", goalId);
            return Optional.empty();
        }
    }

    /**
     * Ищет финансовую цель по её названию.
     *
     * @param name название цели.
     * @return {@link Optional} с объектом {@link Goal}, если цель найдена, иначе пустой {@link Optional}.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public Optional<Goal> findByName(String name) {
        try {
            Goal goal = jdbcTemplate.queryForObject(SQL_SELECT_BY_NAME, this::mapGoal, name);
            return Optional.ofNullable(goal);
        } catch (EmptyResultDataAccessException ex) {
            log.info("Цель не найдена по имени={}", name);
            return Optional.empty();
        }
    }

    /**
     * Получает список всех финансовых целей пользователя.
     *
     * @param userId Id пользователя.
     * @return список целей {@link Goal}.
     * @throws SQLException если произошла ошибка при выполнении запроса.
     */
    @Override
    public List<Goal> findByUserId(long userId) {
        List<Goal> goals = jdbcTemplate.query(SQL_SELECT_BY_USER, this::mapGoal, userId);
        log.debug("Найдено {} целей для userId={}", goals.size(), userId);
        return goals;
    }

    /**
     * Обновляет данные финансовой цели.
     *
     * @param goal объект {@link Goal} с новыми значениями.
     * @throws RuntimeException если произошла ошибка при обновлении в БД.
     */
    @Override
    public void update(Goal goal) {
        int rows = jdbcTemplate.update(SQL_UPDATE,
                goal.getTargetAmount(), goal.getCurrentAmount(), goal.getName());

        if (rows == 0) {
            log.warn("Цель не найдена для обновления: {}", goal);
            throw new RuntimeException("Цель не найдена для обновления");
        }

        log.info("Цель обновлена: {}", goal);
    }

    /**
     * Удаляет финансовую цель по её идентификатору.
     */
    @Override
    public void delete(long goalId) {
        int rows = jdbcTemplate.update(SQL_DELETE, goalId);

        if (rows == 0) {
            log.warn("Цель не найдена для удаления. goalId={}", goalId);
        } else {
            log.info("Цель удалена: goalId={}", goalId);
        }
    }

    /**
     * Метод для маппинга ResultSet на сущность Goal.
     */
    private Goal mapGoal(ResultSet rs, int rowNum) throws SQLException {
        return new Goal(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getDouble("target_amount"),
                rs.getDouble("current_amount")
        );
    }
}