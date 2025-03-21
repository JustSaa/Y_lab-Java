package homework_1.services.impl;

import homework_1.services.NotificationService;

/**
 * Реализация сервиса уведомлений (консольный вывод).
 */
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendNotification(long userId, String message) {
        System.out.println("Уведомление для " + userId + ": " + message);
    }
}