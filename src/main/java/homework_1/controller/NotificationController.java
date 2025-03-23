package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.config.ConnectionManager;
import homework_1.domain.Notification;
import homework_1.repositories.NotificationRepository;
import homework_1.repositories.jdbc.JdbcNotificationRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

@WebServlet("/api/notifications/*")
public class NotificationController extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private NotificationRepository notificationRepository;

    @Override
    public void init() {
        try {
            Connection connection = ConnectionManager.getConnection();
            this.notificationRepository = new JdbcNotificationRepository(connection);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации NotificationController", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // /{userId}
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "userId обязателен"));
            return;
        }

        try {
            long userId = Long.parseLong(pathInfo.substring(1));
            List<Notification> notifications = notificationRepository.findByUserId(userId);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), notifications);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "userId должен быть числом"));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getOutputStream(), Map.of("error", "Не удалось получить уведомления"));
        }
    }
}