package homework_1.ui;

import homework_1.common.utils.Validator;
import homework_1.domain.User;
import homework_1.services.AuthService;

import java.util.Scanner;

/**
 * Обработчик команд аутентификации и управления профилем.
 */
public class AuthConsoleHandler {
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
            System.out.println("Ошибка: Неверный формат email.");
            return null;
        }

        System.out.println("Введите пароль:");
        String password = scanner.nextLine();

        System.out.println("Является ли пользователь администратором? (да/нет)");
        String adminResponse = scanner.nextLine().trim().toLowerCase();
        boolean isAdmin = adminResponse.equals("да");

        try {
            currentUser = authService.register(name, email, password, isAdmin);
            currentUser.setAdmin(isAdmin);
            System.out.println("Вы успешно зарегистрированы: " + currentUser.getEmail());
            System.out.println("Роль: " + (isAdmin ? "Администратор" : "Обычный пользователь"));
            return currentUser;
        } catch (IllegalArgumentException e) {
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
            System.out.println("Добро пожаловать, " + currentUser.getName());
            return currentUser;
        } catch (Exception e) {
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
            System.out.println("Ошибка: Неверный формат email.");
            return;
        }

        System.out.println("Введите новый пароль:");
        String newPassword = scanner.nextLine();

        authService.updateUser(currentUser.getEmail(), newName, newEmail, newPassword);
        System.out.println("Профиль обновлён.");
    }

    /**
     * Удаляет аккаунт пользователя.
     */
    public void deleteAccount() {
        System.out.println("Вы уверены, что хотите удалить аккаунт? (да/нет)");
        String confirmation = scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("да")) {
            System.out.println("Отмена удаления.");
            return;
        }

        authService.deleteUser(currentUser.getEmail());
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