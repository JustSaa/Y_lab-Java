package homework_1.services.impl;

import homework_1.domain.Notification;
import homework_1.repositories.NotificationRepository;
import homework_1.services.NotificationService;

/**
 * Реализация сервиса уведомлений (консольный вывод).
 */
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    public NotificationServiceImpl(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void sendNotification(long userId, String message) {
        Notification notification = new Notification(userId, message);
        repository.save(notification);
    }
}