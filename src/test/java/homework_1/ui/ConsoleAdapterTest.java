package homework_1.ui;

import homework_1.services.*;
import homework_1.services.impl.GoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

class ConsoleAdapterTest {
    private ConsoleAdapter consoleAdapter;
    private AuthService authService;
    private TransactionService transactionService;
    private GoalService goalService;
    private AnalyticsService analyticsService;
    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        transactionService = mock(TransactionService.class);
        goalService = mock(GoalService.class);
        analyticsService = mock(AnalyticsService.class);
    }

    @Test
    void testAuthMenu_Exit() throws SQLException {
        provideInput("0\n");

        consoleAdapter.start();
    }

    @Test
    void testMainMenu_ShowBalance() throws SQLException {
        provideInput("2\ntest@example.com\npassword\n3\n0\n");

        consoleAdapter.start();
    }

    /**
     * Перенаправляет стандартный ввод на подставленные данные.
     */
    private void provideInput(String data) {
        InputStream backupInputStream = System.in;
        ByteArrayInputStream testInput = new ByteArrayInputStream(data.getBytes());
        System.setIn(testInput);

        consoleAdapter = new ConsoleAdapter(authService, transactionService, goalService, analyticsService, budgetService);

        System.setIn(backupInputStream);
    }
}