package homework_1.controller;

import audit.logger.AuditLogger;
import homework_1.dto.*;
import homework_1.mapper.AnalyticsMapper;
import homework_1.services.AnalyticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {
    @MockBean
    private AuditLogger auditLogger;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private AnalyticsMapper analyticsMapper;

    @Test
    @DisplayName("GET /api/analytics/{userId}/income - возвращает сумму доходов")
    void testGetIncome() throws Exception {
        when(analyticsService.getTotalIncome(Mockito.eq(1L), any(), any())).thenReturn(1000.0);
        when(analyticsMapper.toIncomeDto(1000.0)).thenReturn(new IncomeResponseDto(1000.0));

        mockMvc.perform(get("/api/analytics/1/income")
                        .param("start", "2024-01-01")
                        .param("end", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").value(1000.0));
    }

    @Test
    @DisplayName("GET /api/analytics/{userId}/expenses - возвращает сумму расходов")
    void testGetExpenses() throws Exception {
        when(analyticsService.getTotalExpenses(Mockito.eq(1L), any(), any())).thenReturn(500.0);
        when(analyticsMapper.toExpensesDto(500.0)).thenReturn(new ExpensesResponseDto(500.0));

        mockMvc.perform(get("/api/analytics/1/expenses")
                        .param("start", "2024-01-01")
                        .param("end", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenses").value(500.0));
    }

    @Test
    @DisplayName("GET /api/analytics/{userId}/categories - возвращает отчет по категориям")
    void testGetCategories() throws Exception {
        String report = "FOOD: 200.0\nENTERTAINMENT: 100.0";
        when(analyticsService.analyzeExpensesByCategory(1L)).thenReturn(report);
        when(analyticsMapper.toCategoryDto(report)).thenReturn(new CategoryResponseDto(report));

        mockMvc.perform(get("/api/analytics/1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryReport").value(report));
    }

    @Test
    @DisplayName("GET /api/analytics/{userId}/report - возвращает полный отчет")
    void testGetFullReport() throws Exception {
        String report = "Доход: 1000\nРасход: 500\nБаланс: 500";
        when(analyticsService.generateFinancialReport(1L)).thenReturn(report);
        when(analyticsMapper.toFullReportDto(report)).thenReturn(new FullReportResponseDto(report));

        mockMvc.perform(get("/api/analytics/1/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullReport").value(report));
    }
}