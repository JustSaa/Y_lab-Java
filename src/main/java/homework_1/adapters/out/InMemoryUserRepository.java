package homework_1.adapters.out;

import homework_1.application.ports.out.UserRepository;
import homework_1.domain.User;

import java.util.*;

/**
 * Реализация UserRepository на основе коллекции HashMap.
 */
public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> users = new HashMap<>();

    /**
     * Сохраняет пользователя в памяти.
     *
     * @param user пользователь
     */
    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    /**
     * Ищет пользователя по его электронной почте.
     *
     * @param email email пользователя
     * @return Optional с найденным пользователем
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    /**
     * Ищет пользователя по ID.
     *
     * @param userId идентификатор пользователя
     * @return Optional с пользователем или пустой, если не найден
     */
    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(users.get(userId));
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     */
    @Override
    public void delete(UUID userId) {
        users.remove(userId);
    }
}