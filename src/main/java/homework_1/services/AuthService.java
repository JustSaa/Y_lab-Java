package homework_1.services;

import homework_1.domain.User;
import homework_1.common.exceptions.AuthenticationException;

import java.util.List;

/**
 * Интерфейс сервиса авторизации и регистрации пользователей.
 */
public interface AuthService {

    /**
     * Регистрация нового пользователя.
     *
     * @param name     имя
     * @param email    электронная почта
     * @param password пароль
     * @return созданный пользователь
     */
    User register(String name, String email, String password, boolean isAdmin);

    /**
     * Авторизация пользователя.
     *
     * @param email    email пользователя
     * @param password пароль пользователя
     * @return авторизованный пользователь
     * @throws AuthenticationException при ошибке авторизации
     */
    User login(String email, String password) throws AuthenticationException;


    /**
     * Обновление пользователя.
     *
     * @param email       электронная почта
     * @param newName     новое имя
     * @param newEmail    новая почта
     * @param newPassword новый пароль
     * @throws IllegalArgumentException если пользователь не найден
     */
    void updateUser(String email, String newName, String newEmail, String newPassword);

    /**
     * Удаление пользователя.
     *
     * @param email электронная почта
     */
    void deleteUser(String email);

    /**
     * Удаление пользователя администратором.
     *
     * @param adminEmail email администратора
     * @param userEmail  email пользователя для удаления
     */
    void deleteUser(String adminEmail, String userEmail);

    /**
     * Получает список всех пользователей.
     *
     * @return список пользователей
     */
    List<User> getAllUsers();

    /**
     * Блокирует пользователя.
     *
     * @param adminEmail email администратора
     * @param userEmail  email пользователя для блокировки
     */
    void blockUser(String adminEmail, String userEmail);
}