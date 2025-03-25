package homework_1.controller;

import homework_1.domain.Goal;
import homework_1.dto.AddToGoalDto;
import homework_1.dto.CreateGoalDto;
import homework_1.services.GoalService;
import homework_1.utils.ControllerUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/goals/*")
public class GoalController extends HttpServlet {
    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, CreateGoalDto.class, this::handleCreateGoal);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleGetGoals(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if ("/add".equals(req.getPathInfo())) {
            handleRequest(req, resp, AddToGoalDto.class, this::handleAddToGoal);
        } else {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Неизвестный путь");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleDeleteGoal(req, resp);
    }

    private void handleCreateGoal(HttpServletResponse resp, CreateGoalDto dto) throws IOException {
        if (dto.getName() == null || dto.getName().isBlank() || dto.getTargetAmount() <= 0) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректные данные");
            return;
        }

        goalService.createGoal(dto.getUserId(), dto.getName(), dto.getTargetAmount());
        ControllerUtil.writeResponse(resp, HttpServletResponse.SC_CREATED, Map.of("message", "Цель создана"));
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
            ControllerUtil.writeResponse(resp, goals);
        } catch (NumberFormatException e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "userId должен быть числом");
        } catch (Exception e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Не удалось получить цели");
        }
    }

    private void handleAddToGoal(HttpServletResponse resp, AddToGoalDto dto) throws IOException {
        goalService.addToGoal(dto.getGoalName(), dto.getAmount());
        ControllerUtil.writeResponse(resp, Map.of("message", "Цель пополнена"));
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
        } catch (NumberFormatException e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "goalId должен быть числом");
        } catch (Exception e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Не удалось удалить цель");
        }
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