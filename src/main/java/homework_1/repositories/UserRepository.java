package homework_1.repositories;

import homework_1.domain.User;

import java.util.Optional;
import java.util.List;

/**
 * Интерфейс репозитория для работы с пользователями.
 */
public interface UserRepository {

    /**
     * Сохранение нового пользователя.
     *
     * @param user пользователь для сохранения
     */
    void save(User user);

    /**
     * Поиск пользователя по email.
     *
     * @param email электронная почта
     * @return найденный пользователь или пустой Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Удаление пользователя по email.
     *
     * @param userEmail идентификатор пользователя
     */
    void delete(String userEmail);

    /**
     * Обновление пользователя.
     *
     * @param user пользователь для обновления
     */
    void update(User user);

    List<User> findAll();

    void blockUser(String email);
}