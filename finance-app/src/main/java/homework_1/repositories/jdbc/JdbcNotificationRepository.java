package homework_1.repositories.jdbc;

import homework_1.domain.Notification;
import homework_1.repositories.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
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

    private final JdbcTemplate jdbcTemplate;

    public JdbcNotificationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Notification notification) {
        int rows = jdbcTemplate.update(SQL_INSERT,
                notification.getUserId(),
                notification.getMessage(),
                notification.isRead(),
                Timestamp.valueOf(notification.getCreatedAt())
        );

        if (rows > 0) {
            log.debug("Сохранено уведомление: {}", notification);
        } else {
            log.warn("Уведомление не было сохранено: {}", notification);
        }
    }

    @Override
    public List<Notification> findByUserId(long userId) {
        List<Notification> notifications = jdbcTemplate.query(SQL_SELECT_BY_USER, notificationMapper(), userId);
        log.debug("Найдено {} уведомлений для userId={}", notifications.size(), userId);
        return notifications;
    }

    @Override
    public void markAsRead(long notificationId) {
        int rows = jdbcTemplate.update(SQL_MARK_AS_READ, notificationId);

        if (rows > 0) {
            log.debug("Уведомление помечено как прочитанное: id={}", notificationId);
        } else {
            log.warn("Не найдено уведомление для обновления: id={}", notificationId);
        }
    }

    /**
     * Маппер для объекта Notification.
     */
    private RowMapper<Notification> notificationMapper() {
        return (ResultSet rs, int rowNum) -> new Notification(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("message"),
                rs.getBoolean("is_read"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}