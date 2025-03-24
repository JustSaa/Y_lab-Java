package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.dto.TransactionRequestDto;
import homework_1.dto.TransactionResponseDto;
import homework_1.services.TransactionService;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TransactionControllerTest {
    private TransactionController controller;
    private TransactionService transactionService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ObjectMapper objectMapper;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws Exception {
        transactionService = mock(TransactionService.class);
        controller = new TransactionController(transactionService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        objectMapper = new ObjectMapper();
        outputStream = new ByteArrayOutputStream();
        objectMapper.registerModule(new JavaTimeModule());

        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override public boolean isReady() { return true; }
            @Override public void setWriteListener(jakarta.servlet.WriteListener writeListener) {}
            @Override public void write(int b) { outputStream.write(b); }
        };

        when(response.getOutputStream()).thenReturn(servletOutputStream);
    }

    @Test
    void testCreateTransaction_Success() throws Exception {
        TransactionRequestDto dto = new TransactionRequestDto(1L, 2000.0, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Обед");

        when(request.getInputStream()).thenReturn(toServletInputStream(dto));

        controller.doPost(request, response);

        verify(transactionService).createTransaction(any());
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Обед"));
    }

    @Test
    void testCreateTransaction_InvalidData() throws Exception {
        TransactionRequestDto dto = new TransactionRequestDto(1L, -500.0, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(),"");

        when(request.getInputStream()).thenReturn(toServletInputStream(dto));

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testGetTransactions_Success() throws Exception {
        long userId = 1L;
        when(request.getPathInfo()).thenReturn("/" + userId);
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, 1500.0, TransactionType.INCOME,
                        Category.SALARY, LocalDate.now(), "Зарплата"));
        when(transactionService.getTransactions(userId)).thenReturn(transactions);

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Зарплата"));
    }

    @Test
    void testGetTransactions_InvalidPath() throws Exception {
        when(request.getPathInfo()).thenReturn(null);

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Нужен userId в URL"));
    }

    @Test
    void testDeleteTransaction_Success() throws Exception {
        long userId = 1L;
        long transactionId = 2L;
        when(request.getPathInfo()).thenReturn("/" + userId + "/" + transactionId);

        controller.doDelete(request, response);

        verify(transactionService).deleteTransaction(userId, transactionId);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void testDeleteTransaction_InvalidPath() throws Exception {
        when(request.getPathInfo()).thenReturn(null);

        controller.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Формат должен быть /userId/transactionId"));
    }

    @Test
    void testUpdateTransaction_Success() throws Exception {
        long transactionId = 3L;
        TransactionRequestDto dto = new TransactionRequestDto(1L, 500.0, TransactionType.EXPENSE, Category.HEALTH, LocalDate.now(), "Лекарства");

        when(request.getPathInfo()).thenReturn("/" + transactionId);
        when(request.getInputStream()).thenReturn(toServletInputStream(dto));

        controller.doPut(request, response);

        verify(transactionService).updateTransaction(any());
        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Транзакция обновлена"));
    }

    @Test
    void testUpdateTransaction_InvalidPath() throws Exception {
        when(request.getPathInfo()).thenReturn(null);

        controller.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Путь должен быть вида"));
    }

    private ServletInputStream toServletInputStream(Object dto) throws IOException {
        String json = objectMapper.writeValueAsString(dto);
        ByteArrayInputStream byteStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        return new ServletInputStream() {
            @Override public int read() { return byteStream.read(); }
            @Override public boolean isFinished() { return byteStream.available() == 0; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(jakarta.servlet.ReadListener readListener) {}
        };
    }
}