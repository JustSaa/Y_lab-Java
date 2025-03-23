package homework_1.repositories.jdbc;

import homework_1.domain.Notification;
import homework_1.repositories.NotificationRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcNotificationRepository implements NotificationRepository {
    private final Connection connection;
    private static final String SAVE = "INSERT INTO notifications (user_id, message, is_read, created_at) VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_USERID = "SELECT * FROM notifications WHERE user_id = ?";
    private static final String UPDATE = "UPDATE notifications SET is_read = true WHERE id = ?";


    public JdbcNotificationRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Notification notification) {
        try (PreparedStatement stmt = connection.prepareStatement(SAVE)) {
            stmt.setLong(1, notification.getUserId());
            stmt.setString(2, notification.getMessage());
            stmt.setBoolean(3, notification.isRead());
            stmt.setTimestamp(4, Timestamp.valueOf(notification.getCreatedAt()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения уведомления", e);
        }
    }

    @Override
    public List<Notification> findByUserId(long userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ?";
        List<Notification> notifications = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_USERID)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(new Notification(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getString("message"),
                        rs.getBoolean("is_read"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения уведомлений", e);
        }
        return notifications;
    }

    @Override
    public void markAsRead(long notificationId) {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE)) {
            stmt.setLong(1, notificationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления уведомления", e);
        }
    }
}