package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.domain.Budget;
import homework_1.dto.SetBudgetDto;
import homework_1.services.BudgetService;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class BudgetControllerTest {
    private BudgetController controller;
    private BudgetService budgetService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ObjectMapper objectMapper;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws Exception {
        budgetService = mock(BudgetService.class);
        controller = new BudgetController(budgetService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        objectMapper = new ObjectMapper();
        outputStream = new ByteArrayOutputStream();

        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override public boolean isReady() { return true; }
            @Override public void setWriteListener(jakarta.servlet.WriteListener writeListener) {}
            @Override public void write(int b) { outputStream.write(b); }
        };

        when(response.getOutputStream()).thenReturn(servletOutputStream);
    }

    @Test
    void testSetBudget_Success() throws Exception {
        SetBudgetDto dto = new SetBudgetDto(1L, 5000.0);

        when(request.getInputStream()).thenReturn(toServletInputStream(dto));

        controller.doPost(request, response);

        verify(budgetService).setUserBudget(1L, 5000.0);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Бюджет установлен"));
    }

    @Test
    void testSetBudget_ServiceThrowsException() throws Exception {
        SetBudgetDto dto = new SetBudgetDto(1L, 5000.0);

        when(request.getInputStream()).thenReturn(toServletInputStream(dto));
        doThrow(new IllegalArgumentException("Ошибка сервиса")).when(budgetService).setUserBudget(anyLong(), anyDouble());

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Ошибка сервиса"));
    }

    @Test
    void testGetBudget_Success() throws Exception {
        Budget budget = new Budget(1L, 5000.0);
        when(request.getPathInfo()).thenReturn("/1");
        when(budgetService.getUserBudget(1L)).thenReturn(Optional.of(budget));

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("5000.0"));
    }

    @Test
    void testGetBudget_NotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        when(budgetService.getUserBudget(1L)).thenReturn(Optional.empty());

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Бюджет не найден"));
    }

    @Test
    void testGetBudget_InvalidUserId() throws Exception {
        when(request.getPathInfo()).thenReturn("/abc"); // Некорректный userId

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("userId должен быть числом"));
    }

    @Test
    void testIsBudgetExceeded_Success() throws Exception {
        when(request.getPathInfo()).thenReturn("/1/exceeded");
        when(budgetService.isBudgetExceeded(1L)).thenReturn(true);

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("budgetExceeded"));
    }

    @Test
    void testIsBudgetExceeded_InvalidUserId() throws Exception {
        when(request.getPathInfo()).thenReturn("/abc/exceeded"); // Некорректный userId

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("userId должен быть числом"));
    }

    @Test
    void testInvalidPath() throws Exception {
        when(request.getPathInfo()).thenReturn("/1/unknown");

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Неизвестный путь запроса"));
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