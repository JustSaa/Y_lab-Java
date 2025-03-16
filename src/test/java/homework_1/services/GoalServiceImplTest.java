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

    private String userEmail;
    private Goal goal;

    @BeforeEach
    void setUp() {
        userEmail = "example@mail.com";
        goal = new Goal(userEmail, "Путешествие", 10000);
    }

    @Test
    void createGoal_ShouldSaveGoal() {
        goalServiceImpl.createGoal(userEmail, "Путешествие", 10000);

        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    void createGoal_InvalidAmount_ShouldThrowException() {
        assertThatThrownBy(() -> goalServiceImpl.createGoal(userEmail, "Путешествие", -100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Цель должна быть положительной.");
    }

    @Test
    void getUserGoals_ShouldReturnGoals() throws SQLException {
        when(goalRepository.findByUserEmail(userEmail)).thenReturn(List.of(goal));

        List<Goal> goals = goalServiceImpl.getUserGoals(userEmail);

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
    void addToGoal_InvalidAmount_ShouldThrowException() {
        String goalName = goal.getName();
        when(goalRepository.findByName(goalName)).thenReturn(Optional.of(goal));

        assertThatThrownBy(() -> goalServiceImpl.addToGoal(goalName, -100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Сумма должна быть положительной.");
    }

    @Test
    void deleteGoal_ShouldCallRepositoryDelete() {
        long goalId = goal.getId();
        goalServiceImpl.deleteGoal(goalId);

        verify(goalRepository, times(1)).delete(goalId);
    }
}