package homework_1.services;

/**
 * Интерфейс сервиса для отправки уведомлений.
 */
public interface NotificationService {

    /**
     * Отправляет уведомление пользователю.
     *
     * @param userEmail email пользователя
     * @param message текст уведомления
     */
    void sendNotification(String userEmail, String message);
}