package homework_1.controller;

import homework_1.config.ServiceFactory;
import homework_1.domain.Budget;
import homework_1.dto.SetBudgetDto;
import homework_1.services.BudgetService;
import homework_1.utils.ControllerUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@WebServlet("/api/budget/*")
public class BudgetController extends HttpServlet {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public BudgetController() {
        try {
            this.budgetService = ServiceFactory.getInstance().getBudgetService();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании BudgetController: невозможно получить budgetService", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SetBudgetDto dto = ControllerUtil.readRequest(req, SetBudgetDto.class, resp);
        if (dto == null) {
            return;
        }

        try {
            budgetService.setUserBudget(dto.getUserId(), dto.getLimit());
            ControllerUtil.writeResponse(resp, HttpServletResponse.SC_CREATED, Map.of("message", "Бюджет установлен"));
        } catch (IllegalArgumentException e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = Optional.ofNullable(req.getPathInfo()).orElse("").trim();

        if (path.isEmpty() || path.equals("/")) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "userId обязателен");
            return;
        }

        String[] parts = path.substring(1).split("/");

        try {
            long userId = Long.parseLong(parts[0]);
            String action = (parts.length > 1) ? parts[1] : "";

            switch (action) {
                case "exceeded" -> handleCheckExceeded(resp, userId);
                case "" -> handleGetBudget(resp, userId);
                default ->
                        ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неизвестный путь запроса: " + path);
            }
        } catch (NumberFormatException e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "userId должен быть числом");
        }
    }

    private void handleGetBudget(HttpServletResponse resp, long userId) throws IOException {
        Optional<Budget> budget = budgetService.getUserBudget(userId);
        if (budget.isPresent()) {
            ControllerUtil.writeResponse(resp, HttpServletResponse.SC_OK, budget.get());
        } else {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Бюджет не найден");
        }
    }

    private void handleCheckExceeded(HttpServletResponse resp, long userId) throws IOException {
        boolean exceeded = budgetService.isBudgetExceeded(userId);
        ControllerUtil.writeResponse(resp, HttpServletResponse.SC_OK, Map.of("budgetExceeded", exceeded));
    }
}