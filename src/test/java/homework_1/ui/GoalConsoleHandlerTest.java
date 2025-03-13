package homework_1.ui;

import homework_1.domain.Goal;
import homework_1.domain.User;
import homework_1.services.impl.GoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
        testUser = new User("Иван Иванов", "test@example.com", "password123");
    }

    @Test
    void createGoal_ShouldCreateGoal_WhenInputIsValid() {
        when(scanner.nextLine()).thenReturn("Машина", "100000");

        goalConsoleHandler.createGoal(testUser);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> amountCaptor = ArgumentCaptor.forClass(Double.class);

        verify(goalServiceImpl, times(1)).createGoal(emailCaptor.capture(), nameCaptor.capture(), amountCaptor.capture());

        assertThat(emailCaptor.getValue()).isEqualTo("test@example.com");
        assertThat(nameCaptor.getValue()).isEqualTo("Машина");
        assertThat(amountCaptor.getValue()).isEqualTo(100000);
    }

    @Test
    void showGoals_ShouldDisplayGoals_WhenGoalsExist() {
        List<Goal> goals = List.of(
                new Goal("test@example.com", "Машина", 100000),
                new Goal("test@example.com", "Отпуск", 50000)
        );
        when(goalServiceImpl.getUserGoals(testUser.getEmail())).thenReturn(goals);

        goalConsoleHandler.showGoals(testUser);

        verify(goalServiceImpl, times(1)).getUserGoals(testUser.getEmail());
    }

    @Test
    void addToGoal_ShouldAddToGoal_WhenInputIsValid() {
        UUID goalId = UUID.randomUUID();
        when(scanner.nextLine()).thenReturn(goalId.toString(), "20000");

        goalConsoleHandler.addToGoal();

        verify(goalServiceImpl, times(1)).addToGoal(goalId, 20000);
    }

    @Test
    void deleteGoal_ShouldDeleteGoal_WhenInputIsValid() {
        UUID goalId = UUID.randomUUID();
        when(scanner.nextLine()).thenReturn(goalId.toString());

        goalConsoleHandler.deleteGoal();

        verify(goalServiceImpl, times(1)).deleteGoal(goalId);
    }
}