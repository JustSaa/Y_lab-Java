package homework_1.repositories.in_memory;

import homework_1.domain.User;
import homework_1.repositories.UserRepository;

import java.util.*;

/**
 * Реализация UserRepository на основе коллекции HashMap.
 */
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new HashMap<>();

    /**
     * Сохраняет пользователя в памяти.
     *
     * @param user пользователь
     */
    @Override
    public void save(User user) {
        users.put(user.getEmail(), user);
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
     * Удаляет пользователя по его почте.
     *
     * @param userEmail почта пользователя
     */
    @Override
    public void delete(String userEmail) {
        users.remove(userEmail);
    }

    /**
     * Обновление пользователя.
     *
     * @param user пользователь для обновления
     */
    @Override
    public void update(User user) {
        users.put(user.getEmail(), user);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void blockUser(String email) {
        User user = users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        user.setBlocked(true);
    }
}