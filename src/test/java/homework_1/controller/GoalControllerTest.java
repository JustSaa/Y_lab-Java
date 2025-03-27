package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.domain.Goal;
import homework_1.dto.AddToGoalDto;
import homework_1.dto.CreateGoalDto;
import homework_1.services.GoalService;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GoalControllerTest {
    private GoalController controller;
    private GoalService goalService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ObjectMapper objectMapper;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws Exception {
        goalService = mock(GoalService.class);
        controller = new GoalController(goalService);

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
    void testCreateGoal_Success() throws Exception {
        CreateGoalDto dto = new CreateGoalDto(1L, "Машина", 500000.0);

        when(request.getInputStream()).thenReturn(toServletInputStream(dto));

        controller.doPost(request, response);

        verify(goalService).createGoal(1L, "Машина", 500000.0);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Цель создана"));
    }

    @Test
    void testCreateGoal_InvalidData() throws Exception {
        CreateGoalDto dto = new CreateGoalDto(1L, "", -500.0);

        when(request.getInputStream()).thenReturn(toServletInputStream(dto));

        controller.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testGetGoals_Success() throws Exception {
        long userId = 1L;
        when(request.getPathInfo()).thenReturn("/" + userId);
        List<Goal> goals = List.of(new Goal(1L, userId, "Квартира", 2000000.0, 500000.0));
        when(goalService.getUserGoals(userId)).thenReturn(goals);

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Квартира"));
    }

    @Test
    void testGetGoals_BadRequest() throws Exception {
        when(request.getPathInfo()).thenReturn(null);

        controller.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("userId обязателен"));
    }

    @Test
    void testAddToGoal_Success() throws Exception {
        AddToGoalDto dto = new AddToGoalDto("Машина", 10000.0);

        when(request.getInputStream()).thenReturn(toServletInputStream(dto));
        when(request.getPathInfo()).thenReturn("/add");

        controller.doPut(request, response);

        verify(goalService).addToGoal("Машина", 10000.0);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Цель пополнена"));
    }

    @Test
    void testDeleteGoal_Success() throws Exception {
        long goalId = 1L;
        when(request.getPathInfo()).thenReturn("/" + goalId);

        controller.doDelete(request, response);

        verify(goalService).deleteGoal(goalId);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void testDeleteGoal_InvalidPath() throws Exception {
        when(request.getPathInfo()).thenReturn(null);

        controller.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outputStream.toString(StandardCharsets.UTF_8).contains("Неверный путь /{goalId}"));
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