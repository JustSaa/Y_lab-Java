package homework_1.ui;

import homework_1.domain.User;
import homework_1.services.AuthService;
import homework_1.services.TransactionInputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class BudgetConsoleHandlerTest {

    private AuthService authService;
    private TransactionInputPort transactionService;
    private BudgetConsoleHandler budgetConsoleHandler;
    private User user;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        transactionService = mock(TransactionInputPort.class);
        budgetConsoleHandler = new BudgetConsoleHandler(authService, transactionService, new Scanner(System.in));
        user = new User("Тестовый Пользователь", "test@mail.com", "password123");
    }

    @Test
    void setBudget_ShouldSetBudget_WhenInputIsValid() {
        String input = "5000\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        budgetConsoleHandler = new BudgetConsoleHandler(authService, transactionService, new Scanner(System.in));
        budgetConsoleHandler.setBudget(user);

        verify(authService, times(1)).setUserBudget("test@mail.com", 5000);
    }

    @Test
    void setBudget_ShouldShowError_WhenInputIsInvalid() {
        String input = "invalid_number\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        budgetConsoleHandler = new BudgetConsoleHandler(authService, transactionService, new Scanner(System.in));
        budgetConsoleHandler.setBudget(user);

        verify(authService, never()).setUserBudget(anyString(), anyDouble());
    }

    @Test
    void checkBudget_ShouldNotify_WhenBudgetExceeded() {
        when(transactionService.isBudgetExceeded("test@mail.com")).thenReturn(true);

        budgetConsoleHandler.checkBudget(user);

        verify(transactionService, times(1)).isBudgetExceeded("test@mail.com");
    }

    @Test
    void checkBudget_ShouldNotify_WhenBudgetNotExceeded() {
        when(transactionService.isBudgetExceeded("test@mail.com")).thenReturn(false);

        budgetConsoleHandler.checkBudget(user);

        verify(transactionService, times(1)).isBudgetExceeded("test@mail.com");
    }

    @Test
    void checkBudget_ShouldShowError_WhenUserIsNull() {
        budgetConsoleHandler.checkBudget(null);

        verify(transactionService, never()).isBudgetExceeded(anyString());
    }
}