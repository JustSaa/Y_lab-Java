package homework_1.ui;

import homework_1.domain.User;
import homework_1.services.AuthService;
import homework_1.services.BudgetService;
import homework_1.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class BudgetConsoleHandlerTest {

    private AuthService authService;
    private TransactionService transactionService;
    private BudgetService budgetService;
    private BudgetConsoleHandler budgetConsoleHandler;
    private User user;

    @BeforeEach
    void setUp() {
        //authService = mock(AuthService.class);
        //transactionService = mock(TransactionService.class);
        budgetService = mock(BudgetService.class);
        budgetConsoleHandler = new BudgetConsoleHandler(budgetService, new Scanner(System.in));
        user = new User("Тестовый Пользователь", "test@mail.com", "password123");
    }

    @Test
    void setBudget_ShouldSetBudget_WhenInputIsValid() {
        String input = "5000\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        BudgetConsoleHandler handler = new BudgetConsoleHandler(budgetService, scanner);

        handler.setBudget(user);

        verify(budgetService, times(1)).setUserBudget("test@mail.com", 5000);
    }

    @Test
    void setBudget_ShouldShowError_WhenInputIsInvalid() {
        String input = "invalid_number\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);
        BudgetConsoleHandler handler = new BudgetConsoleHandler(budgetService, scanner);

        handler.setBudget(user);

        verify(budgetService, never()).setUserBudget(anyString(), anyDouble());
    }

    @Test
    void checkBudget_ShouldNotify_WhenBudgetExceeded() {
        when(budgetService.isBudgetExceeded("test@mail.com")).thenReturn(true);

        budgetConsoleHandler.checkBudget(user);

        verify(budgetService, times(1)).isBudgetExceeded("test@mail.com");
    }

    @Test
    void checkBudget_ShouldNotify_WhenBudgetNotExceeded() {
        when(budgetService.isBudgetExceeded("test@mail.com")).thenReturn(false);

        budgetConsoleHandler.checkBudget(user);

        verify(budgetService, times(1)).isBudgetExceeded("test@mail.com");
    }

    @Test
    void checkBudget_ShouldShowError_WhenUserIsNull() {
        budgetConsoleHandler.checkBudget(null);

        verify(budgetService, never()).isBudgetExceeded(anyString());
    }
}