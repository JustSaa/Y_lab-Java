package homework_1.services;

import homework_1.domain.Goal;
import homework_1.repositories.GoalRepository;
import homework_1.services.impl.GoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalServiceImpl goalServiceImpl;

    private long userId;
    private Goal goal;

    @BeforeEach
    void setUp() {
        userId = 0;
        goal = new Goal(userId, "Путешествие", 10000);
    }

    @Test
    void createGoal_ShouldSaveGoal() {
        goalServiceImpl.createGoal(userId, "Путешествие", 10000);

        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    void createGoal_InvalidAmount_ShouldThrowException() {
        assertThatThrownBy(() -> goalServiceImpl.createGoal(userId, "Путешествие", -100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Цель должна быть положительной.");
    }

    @Test
    void getUserGoals_ShouldReturnGoals() throws SQLException {
        when(goalRepository.findByUserId(userId)).thenReturn(List.of(goal));

        List<Goal> goals = goalServiceImpl.getUserGoals(userId);

        assertThat(goals).containsExactly(goal);
    }

    @Test
    void addToGoal_ShouldIncreaseCurrentAmount() {
        String goalName = goal.getName();
        when(goalRepository.findByName(goalName)).thenReturn(Optional.of(goal));

        goalServiceImpl.addToGoal(goalName, 5000);

        assertThat(goal.getCurrentAmount()).isEqualTo(5000);
        verify(goalRepository, times(1)).update(goal);
    }

    @Test
    void addToGoal_InvalidAmount_ShouldPrintErrorMessage() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        GoalServiceImpl goalService = new GoalServiceImpl(goalRepository);
        String goalName = "TestGoal";

        when(goalRepository.findByName(goalName)).thenReturn(Optional.of(goal));

        goalService.addToGoal(goalName, -100);

        assertThat(outContent.toString()).contains("⚠️ Ошибка: сумма должна быть положительной.");

        System.setOut(System.out);
    }

    @Test
    void deleteGoal_ShouldCallRepositoryDelete() {
        long goalId = goal.getId();
        goalServiceImpl.deleteGoal(goalId);

        verify(goalRepository, times(1)).delete(goalId);
    }
}