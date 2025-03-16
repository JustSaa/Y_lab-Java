package homework_1.repositories.jdbc;

import homework_1.domain.Goal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcGoalRepositoryTest extends AbstractTestContainerTest {

    private JdbcGoalRepository goalRepository;

    @BeforeEach
    void setUp() {
        goalRepository = new JdbcGoalRepository(connection);
    }

    @Test
    void shouldSaveAndFindGoalByName() {
        String email = "test1@user.com";
        Goal goal = new Goal(email, "Buy Car", 10000.0);
        goalRepository.save(goal);

        Optional<Goal> foundGoal = goalRepository.findByName("Buy Car");

        assertThat(foundGoal).isPresent();
        assertThat(foundGoal.get().getName()).isEqualTo("Buy Car");
        assertThat(foundGoal.get().getTargetAmount()).isEqualTo(10000.0);
    }

    @Test
    void shouldFindGoalsByUserEmail() throws SQLException {
        String email = "testFor@user.com";
        goalRepository.save(new Goal(email, "Trip to Japan", 5000.0));
        goalRepository.save(new Goal(email, "MacBook", 3000.0));

        List<Goal> goals = goalRepository.findByUserEmail(email);

        assertThat(goals).hasSize(2);
        assertThat(goals).extracting(Goal::getName).contains("Trip to Japan", "MacBook");
    }

    @Test
    void shouldUpdateGoal() {
        String email = "test@user.com";
        Goal goal = new Goal(email, "New Goal", 4000.0);
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
        String email = "test@user.com";
        Goal goal = new Goal(email, "Delete Me", 3000.0);
        goalRepository.save(goal);

        goalRepository.delete(goal.getId());

        Optional<Goal> deletedGoal = goalRepository.findById(goal.getId());
        assertThat(deletedGoal).isEmpty();
    }
}