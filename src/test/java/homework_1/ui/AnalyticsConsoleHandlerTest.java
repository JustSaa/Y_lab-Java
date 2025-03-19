package homework_1.ui;

import homework_1.domain.User;
import homework_1.services.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class AnalyticsConsoleHandlerTest {

    private AnalyticsService analyticsService;
    private AnalyticsConsoleHandler analyticsConsoleHandler;
    private User currentUser;

    @BeforeEach
    void setUp() {
        analyticsService = mock(AnalyticsService.class);
        analyticsConsoleHandler = new AnalyticsConsoleHandler(analyticsService, new Scanner(System.in));
        currentUser = new User("User1", "user1@mail.com", "password", false);
    }

    @Test
    void showFinancialReport_ShouldPrintReport_WhenUserLoggedIn() {
        when(analyticsService.generateFinancialReport(currentUser.getId()))
                .thenReturn("Финансовый отчет: Доход 1000, Расход 500");

        analyticsConsoleHandler.showFinancialReport(currentUser);

        verify(analyticsService, times(1)).generateFinancialReport(currentUser.getId());
    }

    @Test
    void showFinancialReport_ShouldShowError_WhenUserNotLoggedIn() {
        analyticsConsoleHandler.showFinancialReport(null);

        verify(analyticsService, never()).generateFinancialReport(anyLong());
    }

    @Test
    void showIncomeAndExpensesForPeriod_ShouldPrintValues_WhenUserLoggedIn() {
        String input = "2024-01-01\n2024-12-31\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        analyticsConsoleHandler = new AnalyticsConsoleHandler(analyticsService, new Scanner(System.in));

        when(analyticsService.getTotalIncome(currentUser.getId(), "2024-01-01", "2024-12-31"))
                .thenReturn(1000.0);
        when(analyticsService.getTotalExpenses(currentUser.getId(), "2024-01-01", "2024-12-31"))
                .thenReturn(500.0);

        analyticsConsoleHandler.showIncomeAndExpensesForPeriod(currentUser);

        verify(analyticsService, times(1)).getTotalIncome(currentUser.getId(), "2024-01-01", "2024-12-31");
        verify(analyticsService, times(1)).getTotalExpenses(currentUser.getId(), "2024-01-01", "2024-12-31");
    }

    @Test
    void showIncomeAndExpensesForPeriod_ShouldShowError_WhenUserNotLoggedIn() {
        analyticsConsoleHandler.showIncomeAndExpensesForPeriod(null);

        verify(analyticsService, never()).getTotalIncome(anyLong(), anyString(), anyString());
        verify(analyticsService, never()).getTotalExpenses(anyLong(), anyString(), anyString());
    }

    @Test
    void showCategoryAnalysis_ShouldPrintReport_WhenUserLoggedIn() {
        when(analyticsService.analyzeExpensesByCategory(currentUser.getId()))
                .thenReturn("Расходы по категориям: Еда - 500, Транспорт - 200");

        analyticsConsoleHandler.showCategoryAnalysis(currentUser);

        verify(analyticsService, times(1)).analyzeExpensesByCategory(currentUser.getId());
    }

    @Test
    void showCategoryAnalysis_ShouldShowError_WhenUserNotLoggedIn() {
        analyticsConsoleHandler.showCategoryAnalysis(null);

        verify(analyticsService, never()).analyzeExpensesByCategory(anyLong());
    }
}