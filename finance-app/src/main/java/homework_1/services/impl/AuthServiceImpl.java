package homework_1.services.impl;

import audit.aspect.annotation.Audit;
import audit.aspect.annotation.LogExecutionTime;
import homework_1.domain.UserRole;
import homework_1.repositories.UserRepository;
import homework_1.exceptions.AuthenticationException;
import homework_1.domain.User;
import homework_1.services.AuthService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

/**
 * Реализация сервиса авторизации.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Регистрация нового пользователя.
     *
     * @param name     имя
     * @param email    электронная почта
     * @param password пароль
     * @param role     роль пользователя (например, ADMIN, USER)
     * @return созданный пользователь
     */
    @Audit(action = "Регистрация нового пользователя")
    @LogExecutionTime
    @Override
    public User register(String name, String email, String password, UserRole role) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        User newUser = new User(name, email, password, role);
        userRepository.save(newUser);

        return newUser;
    }

    /**
     * Авторизация пользователя.
     *
     * @param email    email пользователя
     * @param password пароль пользователя
     * @return авторизованный пользователь
     * @throws AuthenticationException при ошибке авторизации
     */
    @Audit(action = "Авторизация пользователя")
    @LogExecutionTime
    @Override
    public User login(String email, String password) throws AuthenticationException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Пользователь с таким email не найден"));

        if (!user.getPassword().equals(password)) {
            throw new AuthenticationException("Неверный пароль");
        }

        if (user.isBlocked()) {
            throw new AuthenticationException("Пользователь заблокирован");
        }

        return user;
    }

    /**
     * Обновление пользователя.
     *
     * @param email       электронная почта
     * @param newName     новое имя
     * @param newEmail    новая почта
     * @param newPassword новый пароль
     * @throws IllegalArgumentException если пользователь не найден
     */
    @Audit(action = "Обновление пользователя")
    @LogExecutionTime
    public void updateUser(String email, String newName, String newEmail, String newPassword, UserRole newRole) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        user.setName(newName);
        user.setEmail(newEmail);
        user.setPassword(newPassword);
        user.setRole(newRole);

        userRepository.update(user);
    }

    /**
     * Удаление пользователя.
     *
     * @param email электронная почта
     */
    @Audit(action = "Удаление пользователя")
    @LogExecutionTime
    public void deleteUser(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new IllegalArgumentException("Пользователь не найден.");
        }
        userRepository.delete(email);
    }

    /**
     * Получает список всех пользователей.
     *
     * @return список пользователей
     */
    @Audit(action = "Получить всех пользователей")
    @LogExecutionTime
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Блокирует пользователя.
     *
     * @param adminEmail email администратора
     * @param userEmail  email пользователя для блокировки
     */
    @Audit(action = "Блокирование пользователя")
    @LogExecutionTime
    public void blockUser(String adminEmail, String userEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("Администратор не найден"));

        if (!admin.isAdmin()) {
            throw new IllegalArgumentException("У вас нет прав для блокировки пользователей.");
        }

        userRepository.blockUser(userEmail);
    }

    /**
     * Удаление пользователя администратором.
     *
     * @param adminEmail email администратора
     * @param userEmail  email пользователя для удаления
     */
    @Audit(action = "Удаление пользователя администратором")
    @LogExecutionTime
    public void deleteUser(String adminEmail, String userEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("Администратор не найден"));

        if (!admin.isAdmin()) {
            throw new IllegalArgumentException("У вас нет прав для удаления пользователей.");
        }

        userRepository.delete(userEmail);
    }
}