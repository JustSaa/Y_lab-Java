package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.config.ConnectionManager;
import homework_1.config.JacksonConfig;
import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.dto.TransactionRequestDto;
import homework_1.dto.TransactionResponseDto;
import homework_1.mapper.TransactionMapper;
import homework_1.repositories.BudgetRepository;
import homework_1.repositories.TransactionRepository;
import homework_1.repositories.jdbc.JdbcBudgetRepository;
import homework_1.repositories.jdbc.JdbcTransactionRepository;
import homework_1.services.BudgetService;
import homework_1.services.NotificationService;
import homework_1.services.TransactionService;
import homework_1.services.impl.BudgetServiceImpl;
import homework_1.services.impl.NotificationServiceImpl;
import homework_1.services.impl.TransactionServiceImpl;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/api/transactions/*")
public class TransactionController extends HttpServlet {

    private final ObjectMapper objectMapper = JacksonConfig.objectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);
    private TransactionService transactionService;

    @Override
    public void init() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = ConnectionManager.getConnection();

            TransactionRepository repository = new JdbcTransactionRepository(connection);
            BudgetRepository budgetRepository = new JdbcBudgetRepository(connection);
            BudgetService budgetService = new BudgetServiceImpl(budgetRepository, repository);
            NotificationService notificationService = new NotificationServiceImpl();

            this.transactionService = new TransactionServiceImpl(repository, budgetService, notificationService);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации TransactionController", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleCreateTransaction(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleGetTransactions(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleDeleteTransaction(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleUpdateTransaction(req, resp);
    }

    // --- Обработчики ---

    private void handleCreateTransaction(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TransactionRequestDto dto = objectMapper.readValue(req.getInputStream(), TransactionRequestDto.class);
        if (!isValid(dto, resp)) {
            return;
        }

        Transaction transaction = mapper.toEntity(dto);
        transactionService.createTransaction(transaction);
        TransactionResponseDto responseDto = mapper.toDto(transaction);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getOutputStream(), responseDto);
    }

    private void handleGetTransactions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Нужен userId в URL: /api/transactions/{userId}");
            return;
        }

        try {
            long userId = Long.parseLong(pathInfo.substring(1));
            List<Transaction> result = resolveFilteredTransactions(userId, req);
            List<TransactionResponseDto> dtos = result.stream().map(mapper::toDto).toList();

            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), dtos);
        } catch (Exception e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Ошибка обработки запроса: " + e.getMessage());
        }
    }

    private void handleDeleteTransaction(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] parts = Optional.ofNullable(req.getPathInfo()).orElse("").split("/");
        if (parts.length != 3) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Формат должен быть /userId/transactionId");
            return;
        }

        try {
            long userId = Long.parseLong(parts[1]);
            long transactionId = Long.parseLong(parts[2]);

            transactionService.deleteTransaction(userId, transactionId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Не удалось удалить транзакцию");
        }
    }

    private void handleUpdateTransaction(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] parts = Optional.ofNullable(req.getPathInfo()).orElse("").split("/");
        if (parts.length != 2) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Путь должен быть вида /{transactionId}");
            return;
        }

        try {
            long transactionId = Long.parseLong(parts[1]);
            TransactionRequestDto dto = objectMapper.readValue(req.getInputStream(), TransactionRequestDto.class);
            if (!isValid(dto, resp)) {
                return;
            }

            Transaction transaction = mapper.toEntity(dto);
            transaction.setId(transactionId);
            transactionService.updateTransaction(transaction);

            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("message", "Транзакция обновлена"));
        } catch (NumberFormatException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "ID транзакции должен быть числом");
        }
    }

    private List<Transaction> resolveFilteredTransactions(long userId, HttpServletRequest req) {
        String type = req.getParameter("type");
        String category = req.getParameter("category");
        String date = req.getParameter("date");

        if (type != null) {
            return transactionService.getTransactionsByType(userId, TransactionType.valueOf(type));
        } else if (category != null) {
            return transactionService.getTransactionsByCategory(userId, Category.valueOf(category));
        } else if (date != null) {
            return transactionService.getTransactionsByDate(userId, LocalDate.parse(date));
        } else {
            return transactionService.getTransactions(userId);
        }
    }

    // --- Вспомогательные методы ---

    private <T> boolean isValid(T dto, HttpServletResponse resp) throws IOException {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<T> v : violations) {
                errors.put(v.getPropertyPath().toString(), v.getMessage());
            }
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("errors", errors));
            return false;
        }
        return true;
    }

    private void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        objectMapper.writeValue(resp.getOutputStream(), Map.of("error", message));
    }
}
