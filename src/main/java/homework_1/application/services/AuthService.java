package homework_1.application.services;

import homework_1.application.ports.in.AuthInputPort;
import homework_1.application.ports.out.UserRepository;
import homework_1.common.exceptions.AuthenticationException;
import homework_1.domain.User;

import java.util.Optional;

/**
 * Реализация сервиса авторизации.
 */
public class AuthService implements AuthInputPort {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Регистрация нового пользователя.
     *
     * @param name имя
     * @param email электронная почта
     * @param password пароль
     * @return созданный пользователь
     */
    @Override
    public User register(String name, String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        User newUser = new User(name, email, password);
        userRepository.save(newUser);

        return newUser;
    }

    /**
     * Авторизация пользователя.
     *
     * @param email email пользователя
     * @param password пароль пользователя
     * @return авторизованный пользователь
     * @throws AuthenticationException при ошибке авторизации
     */
    @Override
    public User login(String email, String password) throws AuthenticationException {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new AuthenticationException("Неверный email или пароль"));
    }
}