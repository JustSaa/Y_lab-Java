package homework_1.ui;

import homework_1.common.utils.Validator;
import homework_1.domain.User;
import homework_1.domain.UserRole;
import homework_1.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Обработчик команд аутентификации и управления профилем.
 */
public class AuthConsoleHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuthConsoleHandler.class);
    private final AuthService authService;
    private final Scanner scanner;
    private User currentUser;

    public AuthConsoleHandler(AuthService authService, Scanner scanner) {
        this.authService = authService;
        this.scanner = scanner;
    }

    /**
     * Регистрирует нового пользователя.
     */
    public User register() {
        System.out.println("Введите имя:");
        String name = scanner.nextLine();

        System.out.println("Введите email:");
        String email = scanner.nextLine();
        if (!Validator.isValidEmail(email)) {
            logger.warn("Ошибка регистрации: неверный формат email - {}", email);
            System.out.println("Ошибка: Неверный формат email.");
            return null;
        }

        System.out.println("Введите пароль:");
        String password = scanner.nextLine();

        System.out.println("Является ли пользователь администратором? (да/нет)");
        String roleInput = scanner.nextLine().trim().toLowerCase();
        UserRole role = roleInput.equals("да") ? UserRole.ADMIN : UserRole.USER;

        try {
            currentUser = authService.register(name, email, password, role);
            logger.info("Успешная регистрация пользователя: {} | Роль: {}",
                    email, role);
            System.out.println("Вы успешно зарегистрированы: " + currentUser.getEmail());
            return currentUser;
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка регистрации: {}", e.getMessage());
            System.out.println("Ошибка: " + e.getMessage());
            return null;
        }
    }

    /**
     * Авторизует пользователя.
     */
    public User login() {
        System.out.println("Введите email:");
        String email = scanner.nextLine();
        System.out.println("Введите пароль:");
        String password = scanner.nextLine();

        try {
            currentUser = authService.login(email, password);
            logger.info("Успешный вход пользователя: {}", email);
            System.out.println("Добро пожаловать, " + currentUser.getName());
            return currentUser;
        } catch (Exception e) {
            logger.warn("Ошибка входа: {}", e.getMessage());
            System.out.println("Ошибка входа: " + e.getMessage());
            return null;
        }
    }

    /**
     * Позволяет пользователю изменить профиль.
     */
    public void editProfile() {
        System.out.println("Введите новое имя:");
        String newName = scanner.nextLine();

        System.out.println("Введите новый email:");
        String newEmail = scanner.nextLine();
        if (!Validator.isValidEmail(newEmail)) {
            logger.warn("Ошибка обновления профиля: некорректный email - {}", newEmail);
            System.out.println("Ошибка: Неверный формат email.");
            return;
        }

        System.out.println("Введите новый пароль:");
        String newPassword = scanner.nextLine();

        System.out.println("Является ли пользователь администратором? (да/нет)");
        String roleInput = scanner.nextLine().trim().toLowerCase();
        UserRole newRole = roleInput.equals("да") ? UserRole.ADMIN : UserRole.USER;

        authService.updateUser(currentUser.getEmail(), newName, newEmail, newPassword, newRole);
        logger.info("Профиль пользователя {} обновлён. Новый email: {}", currentUser.getEmail(), newEmail);
        System.out.println("Профиль обновлён.");
    }

    /**
     * Удаляет аккаунт пользователя.
     */
    public void deleteAccount() {
        System.out.println("Вы уверены, что хотите удалить аккаунт? (да/нет)");
        String confirmation = scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("да")) {
            logger.info("Отмена удаления аккаунта пользователем {}", currentUser.getEmail());
            System.out.println("Отмена удаления.");
            return;
        }

        authService.deleteUser(currentUser.getEmail());
        logger.info("Аккаунт {} был удалён", currentUser.getEmail());
        System.out.println("Аккаунт удалён.");
        currentUser = null;
    }

    /**
     * Возвращает текущего пользователя.
     */
    public User getCurrentUser() {
        return currentUser;
    }
}