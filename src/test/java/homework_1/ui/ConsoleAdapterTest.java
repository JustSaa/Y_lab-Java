package homework_1.ui;

import homework_1.services.AnalyticsService;
import homework_1.services.AuthService;
import homework_1.services.GoalService;
import homework_1.services.TransactionInputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Mockito.*;

class ConsoleAdapterTest {
    private ConsoleAdapter consoleAdapter;
    private AuthService authService;
    private TransactionInputPort transactionService;
    private GoalService goalService;
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        transactionService = mock(TransactionInputPort.class);
        goalService = mock(GoalService.class);
        analyticsService = mock(AnalyticsService.class);
    }

    @Test
    void testAuthMenu_Exit() {
        provideInput("0\n");

        consoleAdapter.start();
    }

    @Test
    void testMainMenu_ShowBalance() {
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

        consoleAdapter = new ConsoleAdapter(authService, transactionService, goalService, analyticsService);

        System.setIn(backupInputStream);
    }
}