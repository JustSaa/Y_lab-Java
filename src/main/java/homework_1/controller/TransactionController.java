package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.config.ConnectionManager;
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

    private final ObjectMapper objectMapper = new ObjectMapper();
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
        TransactionRequestDto dto = objectMapper.readValue(req.getInputStream(), TransactionRequestDto.class);

        if (!isValid(dto, resp)) return;

        Transaction transaction = mapper.toEntity(dto);
        transactionService.createTransaction(transaction);
        TransactionResponseDto responseDto = mapper.toDto(transaction);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getOutputStream(), responseDto);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Нужен userId в URL: /api/transactions/{userId}"));
            return;
        }

        try {
            long userId = Long.parseLong(pathInfo.substring(1));

            String type = req.getParameter("type");
            String category = req.getParameter("category");
            String date = req.getParameter("date");

            List<Transaction> result;

            if (type != null) {
                result = transactionService.getTransactionsByType(userId, Enum.valueOf(TransactionType.class, type));
            } else if (category != null) {
                result = transactionService.getTransactionsByCategory(userId, Enum.valueOf(Category.class, category));
            } else if (date != null) {
                result = transactionService.getTransactionsByDate(userId, LocalDate.parse(date));
            } else {
                result = transactionService.getTransactions(userId);
            }

            List<TransactionResponseDto> dtos = result.stream().map(mapper::toDto).toList();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), dtos);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Ошибка обработки запроса: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length != 3) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Формат должен быть /userId/transactionId"));
            return;
        }

        try {
            String[] parts = pathInfo.split("/");
            long userId = Long.parseLong(parts[1]);
            long transactionId = Long.parseLong(parts[2]);

            transactionService.deleteTransaction(userId, transactionId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Не удалось удалить транзакцию"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "ID транзакции обязателен в пути"));
            return;
        }

        String[] parts = pathInfo.split("/");
        if (parts.length != 2) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Некорректный путь"));
            return;
        }

        long transactionId;
        try {
            transactionId = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "ID транзакции должен быть числом"));
            return;
        }

        TransactionRequestDto dto = objectMapper.readValue(req.getInputStream(), TransactionRequestDto.class);
        if (!isValid(dto, resp)) return;

        Transaction transaction = mapper.toEntity(dto);
        transaction.setId(transactionId);

        transactionService.updateTransaction(transaction);
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getOutputStream(), Map.of("message", "Транзакция обновлена"));
    }

    private <T> boolean isValid(T dto, HttpServletResponse resp) throws IOException {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<T> v : violations) {
                errors.put(v.getPropertyPath().toString(), v.getMessage());
            }
            objectMapper.writeValue(resp.getOutputStream(), Map.of("errors", errors));
            return false;
        }
        return true;
    }
}