package homework_1.ui;

import homework_1.domain.Goal;
import homework_1.domain.User;
import homework_1.domain.UserRole;
import homework_1.services.impl.GoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GoalConsoleHandlerTest {
    private GoalServiceImpl goalServiceImpl;
    private Scanner scanner;
    private GoalConsoleHandler goalConsoleHandler;
    private User testUser;

    @BeforeEach
    void setUp() {
        goalServiceImpl = mock(GoalServiceImpl.class);
        scanner = mock(Scanner.class);
        goalConsoleHandler = new GoalConsoleHandler(goalServiceImpl, scanner);
        testUser = new User("Иван Иванов", "test@example.com", "password123", UserRole.USER);
    }

    @Test
    void createGoal_ShouldCreateGoal_WhenInputIsValid() {
        when(scanner.nextLine()).thenReturn("Машина", "100000");

        goalConsoleHandler.createGoal(testUser);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> amountCaptor = ArgumentCaptor.forClass(Double.class);

        verify(goalServiceImpl, times(1)).createGoal(idCaptor.capture(), nameCaptor.capture(), amountCaptor.capture());

        assertThat(idCaptor.getValue()).isEqualTo(0);
        assertThat(nameCaptor.getValue()).isEqualTo("Машина");
        assertThat(amountCaptor.getValue()).isEqualTo(100000);
    }

    @Test
    void showGoals_ShouldDisplayGoals_WhenGoalsExist() throws SQLException {
        List<Goal> goals = List.of(
                new Goal(testUser.getId(), "Машина", 100000),
                new Goal(testUser.getId(), "Отпуск", 50000)
        );
        when(goalServiceImpl.getUserGoals(testUser.getId())).thenReturn(goals);

        goalConsoleHandler.showGoals(testUser);

        verify(goalServiceImpl, times(1)).getUserGoals(testUser.getId());
    }

    @Test
    void addToGoal_ShouldAddToGoal_WhenInputIsValid() {
        String goalName = "Car";
        when(scanner.nextLine()).thenReturn(goalName, "20000");

        goalConsoleHandler.addToGoal();

        verify(goalServiceImpl, times(1)).addToGoal(goalName, 20000);
    }

    @Test
    void deleteGoal_ShouldDeleteGoal_WhenInputIsValid() {
        long goalId = 1L;
        when(scanner.nextLine()).thenReturn(String.valueOf(goalId));

        goalConsoleHandler.deleteGoal();

        verify(goalServiceImpl, times(1)).deleteGoal(goalId);
    }
}