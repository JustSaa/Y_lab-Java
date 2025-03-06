package homework_1.application.ports.out;

import homework_1.domain.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс репозитория для работы с пользователями.
 */
public interface UserRepository {

    /**
     * Сохранение нового пользователя.
     * @param user пользователь для сохранения
     */
    void save(User user);

    /**
     * Поиск пользователя по email.
     * @param email электронная почта
     * @return найденный пользователь или пустой Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Поиск пользователя по ID.
     * @param userId идентификатор пользователя
     * @return Optional с пользователем или пустой, если не найдено
     */
    Optional<User> findById(UUID userId);

    /**
     * Удаление пользователя по ID.
     * @param userId идентификатор пользователя
     */
    void delete(UUID userId);
}