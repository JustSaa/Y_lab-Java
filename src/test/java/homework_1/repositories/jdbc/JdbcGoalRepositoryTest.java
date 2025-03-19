package homework_1.repositories.jdbc;

import homework_1.domain.Goal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcGoalRepositoryTest extends AbstractTestContainerTest {

    private JdbcGoalRepository goalRepository;

    @BeforeEach
    void setUp() {
        goalRepository = new JdbcGoalRepository(connection);
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM finance.goals")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM finance.users WHERE id = ?")) {
            stmt.setLong(1, 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String userSql = "INSERT INTO finance.users (id, name, email, password, is_admin, is_blocked) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(userSql)) {
            stmt.setLong(1, 0);
            stmt.setString(2, "Test User");
            stmt.setString(3, "test@example.com");
            stmt.setString(4, "password");
            stmt.setBoolean(5, false);
            stmt.setBoolean(6, false);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании тестового пользователя", e);
        }
    }

    @Test
    void shouldSaveAndFindGoalByUserId() {
        long userId = 0;
        Goal goal = new Goal(userId, "Buy Car", 10000.0);
        goalRepository.save(goal);

        Optional<Goal> foundGoal = goalRepository.findByName("Buy Car");

        assertThat(foundGoal).isPresent();
        assertThat(foundGoal.get().getName()).isEqualTo("Buy Car");
        assertThat(foundGoal.get().getTargetAmount()).isEqualTo(10000.0);
    }

    @Test
    void shouldFindGoalsByUserId() throws SQLException {
        long userId = 0;
        goalRepository.save(new Goal(userId, "Trip to Japan", 5000.0));
        goalRepository.save(new Goal(userId, "MacBook", 3000.0));

        List<Goal> goals = goalRepository.findByUserId(userId);

        assertThat(goals).hasSize(2);
        assertThat(goals).extracting(Goal::getName).contains("Trip to Japan", "MacBook");
    }

    @Test
    void shouldUpdateGoal() {
        long userId = 0;
        Goal goal = new Goal(userId, "New Goal", 4000.0);
        goalRepository.save(goal);

        goal.setCurrentAmount(2000.0);
        goalRepository.update(goal);

        Optional<Goal> updatedGoal = goalRepository.findByName("New Goal");

        assertThat(updatedGoal).isPresent();
        assertThat(updatedGoal.get().getName()).isEqualTo("New Goal");
        assertThat(updatedGoal.get().getCurrentAmount()).isEqualTo(2000.0);
    }

    @Test
    void shouldDeleteGoal() {
        long userId = 0;
        Goal goal = new Goal(userId, "Delete Me", 3000.0);
        goalRepository.save(goal);

        goalRepository.delete(goal.getId());

        Optional<Goal> deletedGoal = goalRepository.findById(goal.getId());
        assertThat(deletedGoal).isEmpty();
    }
}