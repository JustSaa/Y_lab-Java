package homework_1.repositories.jdbc;

import homework_1.domain.Goal;
import homework_1.repositories.GoalRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcGoalRepository implements GoalRepository {

    private final Connection connection;

    public JdbcGoalRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Goal goal) {
        String sql = """
                INSERT INTO finance.goals (id, user_email, name, target_amount, current_amount)
                VALUES (nextval('finance.goals_seq'), ?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, goal.getUserEmail());
            stmt.setString(2, goal.getName());
            stmt.setDouble(3, goal.getTargetAmount());
            stmt.setDouble(4, goal.getCurrentAmount());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении цели: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Goal> findById(long goalId) {
        String sql = "SELECT * FROM finance.goals WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, goalId);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Goal goal = new Goal(
                            rs.getLong("id"),
                            rs.getString("user_email"),
                            rs.getString("name"),
                            rs.getDouble("target_amount"),
                            rs.getDouble("current_amount")
                    );
                    return Optional.of(goal);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске цели по ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Goal> findByName(String name) {
        String sql = "SELECT id, user_email, name, target_amount, current_amount FROM finance.goals WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Goal goal = new Goal(
                            rs.getLong("id"),
                            rs.getString("user_email"),
                            rs.getString("name"),
                            rs.getDouble("target_amount"),
                            rs.getDouble("current_amount")
                    );
                    return Optional.of(goal);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске цели по имени", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Goal> findByUserEmail(String email) throws SQLException {
        String sql = """
                SELECT id, user_email, name, target_amount, current_amount
                FROM finance.goals
                WHERE user_email = ?
                """;

        List<Goal> goals = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Goal goal = new Goal(
                            rs.getLong("id"),
                            rs.getString("user_email"),
                            rs.getString("name"),
                            rs.getDouble("target_amount"),
                            rs.getDouble("current_amount")
                    );
                    goals.add(goal);
                }
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при получении списка целей: " + e.getMessage());
            }

            return goals;
        }
    }

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
}