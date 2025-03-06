package homework_1.application.ports.in;

import homework_1.domain.User;
import homework_1.common.exceptions.AuthenticationException;

/**
 * Интерфейс сервиса авторизации и регистрации пользователей.
 */
public interface AuthInputPort {

    /**
     * Регистрация нового пользователя.
     *
     * @param name имя
     * @param email электронная почта
     * @param password пароль
     * @return созданный пользователь
     */
    User register(String name, String email, String password);

    /**
     * Авторизация пользователя.
     *
     * @param email email пользователя
     * @param password пароль пользователя
     * @return авторизованный пользователь
     * @throws AuthenticationException при ошибке авторизации
     */
    User login(String email, String password) throws AuthenticationException;
}