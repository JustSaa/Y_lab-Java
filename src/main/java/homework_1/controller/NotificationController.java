package homework_1.controller;

import homework_1.config.ServiceFactory;
import homework_1.domain.Notification;
import homework_1.repositories.NotificationRepository;
import homework_1.utils.ControllerUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/notifications/*")
public class NotificationController extends HttpServlet {
    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationController() {
        try {
            this.notificationRepository = ServiceFactory.getInstance().getNotificationRepository();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании NotificationController: невозможно получить notificationRepository", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, this::handleGetNotifications);
    }

    private void handleGetNotifications(HttpServletResponse resp, Long userId) throws IOException {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        ControllerUtil.writeResponse(resp, notifications);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp, RequestHandler handler) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "userId обязателен");
            return;
        }

        try {
            long userId = Long.parseLong(pathInfo.substring(1));
            handler.handle(resp, userId);
        } catch (NumberFormatException e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "userId должен быть числом");
        } catch (Exception e) {
            ControllerUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Не удалось получить уведомления");
        }
    }

    @FunctionalInterface
    private interface RequestHandler {
        void handle(HttpServletResponse resp, Long userId) throws IOException;
    }
}