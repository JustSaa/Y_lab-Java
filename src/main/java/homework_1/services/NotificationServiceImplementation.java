package homework_1.services;

import homework_1.repositories.NotificationService;

/**
 * Реализация сервиса уведомлений (консольный вывод).
 */
public class NotificationServiceImplementation implements NotificationService {

    @Override
    public void sendNotification(String userEmail, String message) {
        System.out.println("Уведомление для " + userEmail + ": " + message);
    }
}