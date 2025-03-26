package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.config.ServiceFactory;
import homework_1.services.AnalyticsService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/analytics/*")
public class AnalyticsController extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AnalyticsService analyticsService;

    public AnalyticsController() {
        try {
            this.analyticsService = ServiceFactory.getInstance().getAnalyticsService();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании AnalyticsController: невозможно получить AnalyticsService", e);
        }
    }

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] parts = req.getPathInfo().split("/");
        if (parts.length < 3) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Неверный путь запроса"));
            return;
        }

        long userId;
        try {
            userId = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "userId должен быть числом"));
            return;
        }

        String action = parts[2];
        String start = req.getParameter("start");
        String end = req.getParameter("end");

        try {
            switch (action) {
                case "income" -> {
                    double income = analyticsService.getTotalIncome(userId, start, end);
                    objectMapper.writeValue(resp.getOutputStream(), Map.of("income", income));
                }
                case "expenses" -> {
                    double expenses = analyticsService.getTotalExpenses(userId, start, end);
                    objectMapper.writeValue(resp.getOutputStream(), Map.of("expenses", expenses));
                }
                case "categories" -> {
                    String report = analyticsService.analyzeExpensesByCategory(userId);
                    objectMapper.writeValue(resp.getOutputStream(), Map.of("categoryReport", report));
                }
                case "report" -> {
                    String fullReport = analyticsService.generateFinancialReport(userId);
                    objectMapper.writeValue(resp.getOutputStream(), Map.of("report", fullReport));
                }
                default -> {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Неизвестное действие: " + action));
                }
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", e.getMessage()));
        }
    }
}
