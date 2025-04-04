package homework_1.repositories.jdbc;

import homework_1.domain.Notification;
import homework_1.repositories.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcNotificationRepository implements NotificationRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcNotificationRepository.class);

    private static final String SQL_INSERT = """
                INSERT INTO notifications (user_id, message, is_read, created_at)
                VALUES (?, ?, ?, ?)
            """;

    private static final String SQL_SELECT_BY_USER = """
                SELECT * FROM notifications WHERE user_id = ?
            """;

    private static final String SQL_MARK_AS_READ = """
                UPDATE notifications SET is_read = true WHERE id = ?
            """;

    private final DataSource dataSource;

    public JdbcNotificationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Notification notification) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setLong(1, notification.getUserId());
            stmt.setString(2, notification.getMessage());
            stmt.setBoolean(3, notification.isRead());
            stmt.setTimestamp(4, Timestamp.valueOf(notification.getCreatedAt()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                log.debug("Сохранено уведомление: {}", notification);
            } else {
                log.warn("Уведомление не было сохранено: {}", notification);
            }

        } catch (SQLException e) {
            log.error("Ошибка при сохранении уведомления: {}", notification, e);
            throw new RuntimeException("Ошибка сохранения уведомления", e);
        }
    }

    @Override
    public List<Notification> findByUserId(long userId) {
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_USER)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapNotification(rs));
                }
            }

            log.debug("Найдено {} уведомлений для userId={}", notifications.size(), userId);
            return notifications;

        } catch (SQLException e) {
            log.error("Ошибка при получении уведомлений для userId={}", userId, e);
            throw new RuntimeException("Ошибка получения уведомлений", e);
        }
    }

    @Override
    public void markAsRead(long notificationId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_MARK_AS_READ)) {

            stmt.setLong(1, notificationId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                log.debug("Уведомление помечено как прочитанное: id={}", notificationId);
            } else {
                log.warn("Не найдено уведомление для обновления: id={}", notificationId);
            }

        } catch (SQLException e) {
            log.error("Ошибка при обновлении уведомления id={}", notificationId, e);
            throw new RuntimeException("Ошибка обновления уведомления", e);
        }
    }

    private Notification mapNotification(ResultSet rs) throws SQLException {
        return new Notification(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("message"),
                rs.getBoolean("is_read"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}