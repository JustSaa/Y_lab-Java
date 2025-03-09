package homework_1.repositories.in_memory;

import homework_1.domain.Goal;
import homework_1.repositories.GoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class InMemoryGoalRepositoryTest {

    private GoalRepository goalRepository;
    private Goal goal1;
    private Goal goal2;
    private String userEmail;

    @BeforeEach
    void setUp() {
        goalRepository = new InMemoryGoalRepository();
        userEmail = "test@mail.com";

        goal1 = new Goal(userEmail, "Накопить на ноутбук", 1000.0);
        goal2 = new Goal(userEmail, "Отпуск", 5000.0);

        goalRepository.save(goal1);
        goalRepository.save(goal2);
    }

    @Test
    void findById_ShouldReturnCorrectGoal() {
        Optional<Goal> foundGoal = goalRepository.findById(goal1.getId());

        assertThat(foundGoal).isPresent();
        assertThat(foundGoal.get().getName()).isEqualTo("Накопить на ноутбук");
    }

    @Test
    void findById_ShouldReturnEmptyIfNotExists() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<Goal> foundGoal = goalRepository.findById(nonExistentId);

        assertThat(foundGoal).isEmpty();
    }

    @Test
    void findByUserEmail_ShouldReturnUserGoals() {
        List<Goal> goals = goalRepository.findByUserEmail(userEmail);

        assertThat(goals).hasSize(2);
        assertThat(goals).extracting(Goal::getName).containsExactlyInAnyOrder("Накопить на ноутбук", "Отпуск");
    }

    @Test
    void delete_ShouldRemoveGoal() {
        goalRepository.delete(goal1.getId());

        Optional<Goal> deletedGoal = goalRepository.findById(goal1.getId());

        assertThat(deletedGoal).isEmpty();
    }
}