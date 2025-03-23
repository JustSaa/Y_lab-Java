package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.config.ConnectionManager;
import homework_1.domain.Budget;
import homework_1.dto.SetBudgetDto;
import homework_1.repositories.BudgetRepository;
import homework_1.repositories.TransactionRepository;
import homework_1.repositories.jdbc.JdbcBudgetRepository;
import homework_1.repositories.jdbc.JdbcTransactionRepository;
import homework_1.services.BudgetService;
import homework_1.services.impl.BudgetServiceImpl;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.io.IOException;
import java.sql.Connection;
import java.util.*;

@WebServlet("/api/budget/*")
public class BudgetController extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private BudgetService budgetService;

    @Override
    public void init() {
        try {
            Connection connection = ConnectionManager.getConnection();
            BudgetRepository budgetRepository = new JdbcBudgetRepository(connection);
            TransactionRepository transactionRepository = new JdbcTransactionRepository(connection);
            this.budgetService = new BudgetServiceImpl(budgetRepository, transactionRepository);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации BudgetController", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleSetBudget(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "userId обязателен");
            return;
        }

        String[] parts = path.substring(1).split("/");

        if (parts.length == 1) {
            handleGetBudget(resp, parts[0]);
        } else if (parts.length == 2 && "exceeded".equals(parts[1])) {
            handleCheckExceeded(resp, parts[0]);
        } else {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный путь запроса");
        }
    }

    // === Handlers ===

    private void handleSetBudget(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SetBudgetDto dto = objectMapper.readValue(req.getInputStream(), SetBudgetDto.class);
        if (!isValid(dto, resp)) return;

        try {
            budgetService.setUserBudget(dto.getUserId(), dto.getLimit());
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("message", "Бюджет установлен"));
        } catch (IllegalArgumentException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleGetBudget(HttpServletResponse resp, String userIdStr) throws IOException {
        try {
            long userId = Long.parseLong(userIdStr);
            Optional<Budget> budget = budgetService.getUserBudget(userId);

            if (budget.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getOutputStream(), budget.get());
            } else {
                writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Бюджет не найден");
            }
        } catch (NumberFormatException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "userId должен быть числом");
        }
    }

    private void handleCheckExceeded(HttpServletResponse resp, String userIdStr) throws IOException {
        try {
            long userId = Long.parseLong(userIdStr);
            boolean exceeded = budgetService.isBudgetExceeded(userId);

            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("budgetExceeded", exceeded));
        } catch (NumberFormatException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "userId должен быть числом");
        }
    }

    // === Валидация и ошибка ===

    private <T> boolean isValid(T dto, HttpServletResponse resp) throws IOException {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<T> v : violations) {
                errors.put(v.getPropertyPath().toString(), v.getMessage());
            }
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, errors);
            return false;
        }
        return true;
    }

    private void writeError(HttpServletResponse resp, int status, Object message) throws IOException {
        resp.setStatus(status);
        objectMapper.writeValue(resp.getOutputStream(), Map.of("error", message));
    }
}