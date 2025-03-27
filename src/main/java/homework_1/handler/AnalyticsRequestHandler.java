package homework_1.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.services.AnalyticsService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class AnalyticsRequestHandler {
    private final AnalyticsService analyticsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalyticsRequestHandler(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    public void handleIncomeRequest(long userId, String start, String end, HttpServletResponse resp) throws IOException {
        double income = analyticsService.getTotalIncome(userId, start, end);
        sendSuccessResponse(resp, Map.of("income", income));
    }

    public void handleExpensesRequest(long userId, String start, String end, HttpServletResponse resp) throws IOException {
        double expenses = analyticsService.getTotalExpenses(userId, start, end);
        sendSuccessResponse(resp, Map.of("expenses", expenses));
    }

    public void handleCategoriesRequest(long userId, HttpServletResponse resp) throws IOException {
        String report = analyticsService.analyzeExpensesByCategory(userId);
        sendSuccessResponse(resp, Map.of("categoryReport", report));
    }

    public void handleReportRequest(long userId, HttpServletResponse resp) throws IOException {
        String fullReport = analyticsService.generateFinancialReport(userId);
        sendSuccessResponse(resp, Map.of("report", fullReport));
    }

    public void handleUnknownAction(String action, HttpServletResponse resp) throws IOException {
        sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Неизвестное действие: " + action);
    }

    private void sendSuccessResponse(HttpServletResponse resp, Map<String, ?> responseData) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getOutputStream(), responseData);
    }

    private void sendErrorResponse(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        objectMapper.writeValue(resp.getOutputStream(), Map.of("error", message));
    }
}