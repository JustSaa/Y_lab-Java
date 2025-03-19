package homework_1.services;

/**
 * Интерфейс сервиса для отправки уведомлений.
 */
public interface NotificationService {

    /**
     * Отправляет уведомление пользователю.
     *
     * @param userId  Id пользователя
     * @param message текст уведомления
     */
    void sendNotification(long userId, String message);
}