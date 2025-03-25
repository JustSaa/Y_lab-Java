package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.config.JacksonConfig;
import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.dto.TransactionRequestDto;
import homework_1.dto.TransactionResponseDto;
import homework_1.mapper.TransactionMapper;
import homework_1.services.TransactionService;
import homework_1.utils.ControllerUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@WebServlet("/api/transactions/*")
public class TransactionController extends HttpServlet {
    private final ObjectMapper objectMapper = JacksonConfig.objectMapper();
    private final TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, TransactionRequestDto.class, this::handleCreateTransaction);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleGetTransactions(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length != 3) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Формат должен быть /transactions/{userId}/{transactionId}");
            return;
        }

        try {
            String[] parts = pathInfo.substring(1).split("/");
            long userId = Long.parseLong(parts[0]);
            long transactionId = Long.parseLong(parts[1]);

            transactionService.deleteTransaction(userId, transactionId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "ID пользователя и транзакции должны быть числами");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, TransactionRequestDto.class, this::handleUpdateTransaction);
    }

    private void handleCreateTransaction(HttpServletResponse resp, TransactionRequestDto dto) throws IOException {
        Transaction transaction = mapper.toEntity(dto);
        transactionService.createTransaction(transaction);
        ControllerUtil.writeResponse(resp, HttpServletResponse.SC_CREATED, mapper.toDto(transaction));
    }

    private void handleUpdateTransaction(HttpServletResponse resp, TransactionRequestDto dto) throws IOException {
        Transaction transaction = mapper.toEntity(dto);
        transactionService.updateTransaction(transaction);
        ControllerUtil.writeResponse(resp, Map.of("message", "Транзакция обновлена"));
    }

    private void handleGetTransactions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Нужен userId в URL: /api/transactions/{userId}");
            return;
        }

        try {
            long userId = Long.parseLong(pathInfo.substring(1));
            List<Transaction> transactions = filterTransactions(userId, req);
            List<TransactionResponseDto> dtos = transactions.stream().map(mapper::toDto).toList();

            ControllerUtil.writeResponse(resp, dtos);
        } catch (NumberFormatException e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "userId должен быть числом");
        }
    }

    private List<Transaction> filterTransactions(long userId, HttpServletRequest req) {
        String type = req.getParameter("type");
        String category = req.getParameter("category");
        String date = req.getParameter("date");

        if (type != null) {
            return transactionService.getTransactionsByType(userId, TransactionType.valueOf(type));
        }
        if (category != null) {
            return transactionService.getTransactionsByCategory(userId, Category.valueOf(category));
        }
        if (date != null) {
            return transactionService.getTransactionsByDate(userId, LocalDate.parse(date));
        }
        return transactionService.getTransactions(userId);
    }

    private <T> void handleRequest(HttpServletRequest req, HttpServletResponse resp, Class<T> dtoClass, RequestHandler<T> handler) throws IOException {
        T dto = ControllerUtil.readRequest(req, dtoClass, resp);
        if (dto != null) {
            try {
                handler.handle(resp, dto);
            } catch (IllegalArgumentException e) {
                ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }

    @FunctionalInterface
    private interface RequestHandler<T> {
        void handle(HttpServletResponse resp, T dto) throws IOException;
    }
}