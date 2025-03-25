package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.services.AnalyticsService;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AnalyticsControllerTest {

    private AnalyticsController controller;
    private AnalyticsService analyticsService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ByteArrayOutputStream outputStream;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        analyticsService = mock(AnalyticsService.class);
        controller = new AnalyticsController(analyticsService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        outputStream = new ByteArrayOutputStream();

        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() { return true; }
            @Override
            public void setWriteListener(jakarta.servlet.WriteListener writeListener) {}
            @Override
            public void write(int b) { outputStream.write(b); }
        };

        when(response.getOutputStream()).thenReturn(servletOutputStream);
        objectMapper = new ObjectMapper();
    }

    /** ✅ Тест запроса /income */
    @Test
    void testGetIncome_Success() throws IOException {
        when(request.getPathInfo()).thenReturn("/1/income");
        when(request.getParameter("start")).thenReturn("2024-01-01");
        when(request.getParameter("end")).thenReturn("2024-12-31");
        when(analyticsService.getTotalIncome(1L, "2024-01-01", "2024-12-31")).thenReturn(5000.0);

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"income\":5000.0"));
    }

    /** ✅ Тест запроса /expenses */
    @Test
    void testGetExpenses_Success() throws IOException {
        when(request.getPathInfo()).thenReturn("/1/expenses");
        when(request.getParameter("start")).thenReturn("2024-01-01");
        when(request.getParameter("end")).thenReturn("2024-12-31");
        when(analyticsService.getTotalExpenses(1L, "2024-01-01", "2024-12-31")).thenReturn(2000.0);

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"expenses\":2000.0"));
    }

    /** ✅ Тест запроса /categories */
    @Test
    void testGetCategories_Success() throws IOException {
        when(request.getPathInfo()).thenReturn("/1/categories");
        when(analyticsService.analyzeExpensesByCategory(1L)).thenReturn("Food: 30%, Rent: 50%, Other: 20%");

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"categoryReport\":\"Food: 30%, Rent: 50%, Other: 20%\""));
    }

    /** ✅ Тест запроса /report */
    @Test
    void testGetReport_Success() throws IOException {
        when(request.getPathInfo()).thenReturn("/1/report");
        when(analyticsService.generateFinancialReport(1L)).thenReturn("Total Income: $5000, Total Expenses: $2000");

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"report\":\"Total Income: $5000, Total Expenses: $2000\""));
    }

    /** ❌ Неверный userId */
    @Test
    void testInvalidUserId() throws IOException {
        when(request.getPathInfo()).thenReturn("/abc/income");

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String json = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"error\":\"userId должен быть числом\""));
    }

    /** ❌ Неизвестное действие */
    @Test
    void testUnknownAction() throws IOException {
        when(request.getPathInfo()).thenReturn("/1/unknown");

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        String json = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"error\":\"Неизвестное действие: unknown\""));
    }

    /** ❌ Недостаточно параметров в пути */
    @Test
    void testInvalidPath() throws IOException {
        when(request.getPathInfo()).thenReturn("/1");

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String json = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"error\":\"Неверный путь запроса\""));
    }

    /** ❌ Ошибка сервиса */
    @Test
    void testServiceThrowsException() throws IOException {
        when(request.getPathInfo()).thenReturn("/1/income");
        when(request.getParameter("start")).thenReturn("2024-01-01");
        when(request.getParameter("end")).thenReturn("2024-12-31");
        when(analyticsService.getTotalIncome(1L, "2024-01-01", "2024-12-31")).thenThrow(new RuntimeException("Ошибка БД"));

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String json = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"error\":\"Ошибка БД\""));
    }
}