package homework_1.repositories.jdbc;

import homework_1.domain.Goal;
import homework_1.repositories.GoalRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория {@link GoalRepository} для работы с финансовыми целями пользователей
 * с использованием JDBC и базы данных PostgreSQL.
 */
public class JdbcGoalRepository implements GoalRepository {
    private static final String SAVE_GOAL_INTO = """
            INSERT INTO finance.goals (id, user_id, name, target_amount, current_amount)
            VALUES (nextval('finance.goals_seq'), ?, ?, ?, ?)
            """;
    private static final String SELECT_GOALS_BY_ID = "SELECT * FROM finance.goals WHERE id = ?";
    private static final String FIND_BY_NAME = "SELECT id, user_id, name, target_amount, current_amount FROM finance.goals WHERE name = ?";
    private static final String FIND_BY_USERID = """
            SELECT id, user_id, name, target_amount, current_amount
            FROM finance.goals
            WHERE user_id = ?
            """;
    private static final String UPDATE = """
            UPDATE finance.goals
            SET target_amount = ?, current_amount = ?
            WHERE name = ?
            """;
    private static final String DELETE = "DELETE FROM finance.goals WHERE id = ?";

    private final Connection connection;

    /**
     * Конструктор репозитория для работы с финансовыми целями.
     *
     * @param connection объект {@link Connection} для работы с базой данных.
     */
    public JdbcGoalRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Сохраняет новую финансовую цель пользователя в базе данных.
     *
     * @param goal объект {@link Goal}, содержащий данные о цели.
     * @throws RuntimeException если произошла ошибка при сохранении в БД.
     */
    @Override
    public void save(Goal goal) {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(SAVE_GOAL_INTO)) {
                stmt.setLong(1, goal.getUserId());
                stmt.setString(2, goal.getName());
                stmt.setDouble(3, goal.getTargetAmount());
                stmt.setDouble(4, goal.getCurrentAmount());
                stmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                throw new RuntimeException("Ошибка при сохранении цели. Транзакция откатилась.", e);
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Ошибка при откате транзакции", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка при включении autoCommit", e);
            }
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
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_GOALS_BY_ID)) {
            stmt.setLong(1, goalId);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapGoal(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске цели по ID: " + e.getMessage(), e);
        }
        return Optional.empty();
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
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_NAME)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapGoal(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске цели по имени", e);
        }

        return Optional.empty();
    }

    /**
     * Получает список всех финансовых целей пользователя.
     *
     * @param userId Id пользователя.
     * @return список целей {@link Goal}.
     * @throws SQLException если произошла ошибка при выполнении запроса.
     */
    @Override
    public List<Goal> findByUserId(long userId) throws SQLException {
        List<Goal> goals = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_USERID)) {
            stmt.setLong(1, userId);

            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapGoal(rs));
                }
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при получении списка целей: " + e.getMessage());
            }

            return goals;
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
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(UPDATE)) {
                stmt.setDouble(1, goal.getTargetAmount());
                stmt.setDouble(2, goal.getCurrentAmount());
                stmt.setString(3, goal.getName());
                stmt.executeUpdate();
            }

            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
                throw new RuntimeException("Ошибка при обновлении цели. Транзакция откатилась.", e);
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Ошибка при откате транзакции", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка при включении autoCommit", e);
            }
        }
    }

    /**
     * Удаляет финансовую цель по её идентификатору.
     */
    @Override
    public void delete(long goalId) {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(DELETE)) {
                stmt.setLong(1, goalId);
                stmt.executeUpdate();
            }

            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
                throw new RuntimeException("Ошибка при удалении цели. Транзакция откатилась.", e);
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Ошибка при откате транзакции", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка при включении autoCommit", e);
            }
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