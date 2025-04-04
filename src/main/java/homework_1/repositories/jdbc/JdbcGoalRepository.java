package homework_1.repositories.jdbc;

import homework_1.domain.Goal;
import homework_1.repositories.GoalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
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

    private final DataSource dataSource;

    public JdbcGoalRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Сохраняет новую финансовую цель пользователя в базе данных.
     *
     * @param goal объект {@link Goal}, содержащий данные о цели.
     * @throws RuntimeException если произошла ошибка при сохранении в БД.
     */
    @Override
    public void save(Goal goal) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setLong(1, goal.getUserId());
            stmt.setString(2, goal.getName());
            stmt.setDouble(3, goal.getTargetAmount());
            stmt.setDouble(4, goal.getCurrentAmount());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                log.warn("Не удалось сохранить цель: {}", goal);
                throw new SQLException("Ошибка вставки цели");
            }

            log.info("Цель сохранена: {}", goal);
        } catch (SQLException e) {
            log.error("Ошибка сохранения цели: {}", goal, e);
            throw new RuntimeException("Ошибка сохранения цели", e);
        }
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            stmt.setLong(1, goalId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Goal goal = mapGoal(rs);
                    log.debug("Цель найдена по ID: {}", goal);
                    return Optional.of(goal);
                }
                log.info("Цель не найдена по ID={}", goalId);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("Ошибка поиска цели по ID={}", goalId, e);
            throw new RuntimeException("Ошибка поиска цели", e);
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_NAME)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Goal goal = mapGoal(rs);
                    log.debug("Цель найдена по имени: {}", goal);
                    return Optional.of(goal);
                }
                log.info("Цель не найдена по имени={}", name);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("Ошибка поиска цели по имени={}", name, e);
            throw new RuntimeException("Ошибка поиска цели", e);
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
        List<Goal> goals = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_USER)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapGoal(rs));
                }
            }
            log.debug("Найдено {} целей для userId={}", goals.size(), userId);
            return goals;
        } catch (SQLException e) {
            log.error("Ошибка получения целей для userId={}", userId, e);
            throw new RuntimeException("Ошибка получения целей", e);
        }
    }

    /**
     * Обновляет данные финансовой цели.
     *
     * @param goal объект {@link Goal} с новыми значениями.
     * @throws RuntimeException если произошла ошибка при обновлении в БД.
     */
    @Override
    public void update(Goal goal) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setDouble(1, goal.getTargetAmount());
            stmt.setDouble(2, goal.getCurrentAmount());
            stmt.setString(3, goal.getName());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                log.warn("Цель не обновлена: {}", goal);
                throw new SQLException("Цель не найдена для обновления");
            }

            log.info("Цель обновлена: {}", goal);
        } catch (SQLException e) {
            log.error("Ошибка при обновлении цели: {}", goal, e);
            throw new RuntimeException("Ошибка обновления цели", e);
        }
    }

    /**
     * Удаляет финансовую цель по её идентификатору.
     */
    @Override
    public void delete(long goalId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setLong(1, goalId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                log.warn("Цель не найдена для удаления. goalId={}", goalId);
            } else {
                log.info("Цель удалена: goalId={}", goalId);
            }
        } catch (SQLException e) {
            log.error("Ошибка при удалении цели goalId={}", goalId, e);
            throw new RuntimeException("Ошибка удаления цели", e);
        }
    }

    /**
     * Метод для маппинга ResultSet на сущность Goal.
     */
    private Goal mapGoal(ResultSet rs) throws SQLException {
        return new Goal(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getDouble("target_amount"),
                rs.getDouble("current_amount")
        );
    }
}