package homework_1.services.impl;

import homework_1.aspect.Audit;
import homework_1.aspect.LogExecutionTime;
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

    @Audit(action = "Отправить уведомление")
    @LogExecutionTime
    @Override
    public void sendNotification(long userId, String message) {
        Notification notification = new Notification(userId, message);
        repository.save(notification);
    }
}