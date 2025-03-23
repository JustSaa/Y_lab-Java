package homework_1.repositories;

import homework_1.domain.Notification;

import java.util.List;

public interface NotificationRepository {
    void save(Notification notification);
    List<Notification> findByUserId(long userId);
    void markAsRead(long notificationId);
}