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
        String sql = """
                INSERT INTO finance.goals (id, user_id, name, target_amount, current_amount)
                VALUES (nextval('finance.goals_seq'), ?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, goal.getUserId());
            stmt.setString(2, goal.getName());
            stmt.setDouble(3, goal.getTargetAmount());
            stmt.setDouble(4, goal.getCurrentAmount());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении цели: " + e.getMessage(), e);
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
        String sql = "SELECT * FROM finance.goals WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = "SELECT id, user_id, name, target_amount, current_amount FROM finance.goals WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = """
                SELECT id, user_id, name, target_amount, current_amount
                FROM finance.goals
                WHERE user_id = ?
                """;

        List<Goal> goals = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = """
                UPDATE finance.goals
                SET target_amount = ?, current_amount = ?
                WHERE name = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, goal.getTargetAmount());
            stmt.setDouble(2, goal.getCurrentAmount());
            stmt.setString(3, goal.getName());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении цели: " + e.getMessage());
        }
    }

    /**
     * Удаляет финансовую цель по её идентификатору.
     *
     * @param goalId уникальный идентификатор цели.
     * @throws RuntimeException если произошла ошибка при удалении из БД.
     */
    @Override
    public void delete(long goalId) {
        String sql = "DELETE FROM finance.goals WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, goalId);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении цели: " + e.getMessage(), e);
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