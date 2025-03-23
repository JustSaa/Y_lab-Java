package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.config.ConnectionManager;
import homework_1.domain.Goal;
import homework_1.dto.AddToGoalDto;
import homework_1.dto.CreateGoalDto;
import homework_1.repositories.GoalRepository;
import homework_1.repositories.jdbc.JdbcGoalRepository;
import homework_1.services.GoalService;
import homework_1.services.impl.GoalServiceImpl;

import homework_1.utils.ControllerUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.io.IOException;
import java.sql.Connection;
import java.util.*;

@WebServlet("/api/goals/*")
public class GoalController extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private GoalService goalService;

    @Override
    public void init() {
        try {
            Connection connection = ConnectionManager.getConnection();
            GoalRepository goalRepository = new JdbcGoalRepository(connection);
            this.goalService = new GoalServiceImpl(goalRepository);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации GoalController", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleCreateGoal(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleGetGoals(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if ("/add".equals(req.getPathInfo())) {
            handleAddToGoal(req, resp);
        } else {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Неизвестный путь");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleDeleteGoal(req, resp);
    }

    private void handleCreateGoal(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CreateGoalDto dto = objectMapper.readValue(req.getInputStream(), CreateGoalDto.class);
        if (!ControllerUtil.validate(dto, resp)) return;

        try {
            goalService.createGoal(dto.getUserId(), dto.getName(), dto.getTargetAmount());
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("message", "Цель создана"));
        } catch (IllegalArgumentException e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleGetGoals(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "userId обязателен");
            return;
        }

        try {
            long userId = Long.parseLong(pathInfo.substring(1));
            List<Goal> goals = goalService.getUserGoals(userId);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), goals);
        } catch (Exception e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Не удалось получить цели");
        }
    }

    private void handleAddToGoal(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AddToGoalDto dto = objectMapper.readValue(req.getInputStream(), AddToGoalDto.class);
        if (!ControllerUtil.validate(dto, resp)) return;

        try {
            goalService.addToGoal(dto.getGoalName(), dto.getAmount());
            objectMapper.writeValue(resp.getOutputStream(), Map.of("message", "Цель пополнена"));
        } catch (Exception e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleDeleteGoal(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length != 2) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Неверный путь /{goalId}");
            return;
        }

        try {
            long goalId = Long.parseLong(pathInfo.substring(1));
            goalService.deleteGoal(goalId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Не удалось удалить цель");
        }
    }
}