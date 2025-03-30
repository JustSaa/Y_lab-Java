package homework_1.controller;

import homework_1.services.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = {AnalyticsController.class})
public class AnalyticsControllerTest {

    private MockMvc mockMvc;
    private AnalyticsService analyticsService;

    @BeforeEach
    void setup() {
        analyticsService = Mockito.mock(AnalyticsService.class);
        AnalyticsController controller = new AnalyticsController(analyticsService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetIncome() throws Exception {
        when(analyticsService.getTotalIncome(anyLong(), anyString(), anyString()))
                .thenReturn(1200.0);

        mockMvc.perform(get("/api/analytics/1/income")
                        .param("start", "2024-01-01")
                        .param("end", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.income").value(1200.0));
    }

    @Test
    void testGetExpenses() throws Exception {
        when(analyticsService.getTotalExpenses(anyLong(), anyString(), anyString()))
                .thenReturn(800.0);

        mockMvc.perform(get("/api/analytics/1/expenses")
                        .param("start", "2024-01-01")
                        .param("end", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenses").value(800.0));
    }

    @Test
    void testGetCategoryReport() throws Exception {
        when(analyticsService.analyzeExpensesByCategory(1L))
                .thenReturn("Food: 50%, Transport: 30%, Other: 20%");

        mockMvc.perform(get("/api/analytics/1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryReport").value("Food: 50%, Transport: 30%, Other: 20%"));
    }

    @Test
    void testGetFullReport() throws Exception {
        when(analyticsService.generateFinancialReport(1L))
                .thenReturn("Total income: 1200.0, Total expenses: 800.0, Balance: 400.0");

        mockMvc.perform(get("/api/analytics/1/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.report").value("Total income: 1200.0, Total expenses: 800.0, Balance: 400.0"));
    }
}