package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.config.ConnectionManager;
import homework_1.repositories.TransactionRepository;
import homework_1.repositories.jdbc.JdbcTransactionRepository;
import homework_1.services.AnalyticsService;
import homework_1.services.impl.AnalyticsServiceImpl;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

@WebServlet("/api/analytics/*")
public class AnalyticsController extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private AnalyticsService analyticsService;

    @Override
    public void init() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = ConnectionManager.getConnection();
            TransactionRepository transactionRepository = new JdbcTransactionRepository(connection);
            this.analyticsService = new AnalyticsServiceImpl(transactionRepository);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации AnalyticsController", e);
        }
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
