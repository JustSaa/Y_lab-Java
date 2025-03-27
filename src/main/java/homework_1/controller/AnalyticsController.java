package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.config.ServiceFactory;
import homework_1.handler.AnalyticsRequestHandler;
import homework_1.services.AnalyticsService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/analytics/*")
public class AnalyticsController extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AnalyticsRequestHandler requestHandler;

    public AnalyticsController() {
        try {
            AnalyticsService analyticsService = ServiceFactory.getInstance().getAnalyticsService();
            this.requestHandler = new AnalyticsRequestHandler(analyticsService);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании AnalyticsController: невозможно получить AnalyticsService", e);
        }
    }

    public AnalyticsController(AnalyticsRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] parts = getPathParts(req);
        if (parts == null) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный путь запроса");
            return;
        }

        long userId = parseUserId(parts[1], resp);
        if (userId == -1) return;

        String action = parts[2];
        String start = req.getParameter("start");
        String end = req.getParameter("end");

        try {
            switch (action) {
                case "income" -> requestHandler.handleIncomeRequest(userId, start, end, resp);
                case "expenses" -> requestHandler.handleExpensesRequest(userId, start, end, resp);
                case "categories" -> requestHandler.handleCategoriesRequest(userId, resp);
                case "report" -> requestHandler.handleReportRequest(userId, resp);
                default -> requestHandler.handleUnknownAction(action, resp);
            }
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private String[] getPathParts(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length < 3) {
            return null;
        }
        return pathInfo.split("/");
    }

    private long parseUserId(String userIdStr, HttpServletResponse resp) throws IOException {
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "userId должен быть числом");
            return -1;
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        objectMapper.writeValue(resp.getOutputStream(), Map.of("error", message));
    }
}