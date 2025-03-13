package homework_1.ui;

import homework_1.domain.User;
import homework_1.services.AuthService;

import java.util.List;
import java.util.Scanner;

/**
 * Обработчик команд администратора.
 */
public class AdminConsoleHandler {
    private final AuthService authService;
    private final Scanner scanner;

    public AdminConsoleHandler(AuthService authService, Scanner scanner) {
        this.authService = authService;
        this.scanner = scanner;
    }

    /**
     * Отображает список пользователей.
     */
    public void showAllUsers() {
        List<User> users = authService.getAllUsers();
        users.forEach(user -> System.out.println(user.getEmail() + " | " +
                (user.isAdmin() ? "Админ" : "Пользователь") +
                (user.isBlocked() ? " | Заблокирован" : "")));
    }

    /**
     * Блокирует пользователя.
     */
    public void blockUser(User admin) {
        if (!admin.isAdmin()) {
            System.out.println("Ошибка: у вас нет прав администратора.");
            return;
        }

        System.out.println("Введите email пользователя для блокировки:");
        String email = scanner.nextLine();

        try {
            authService.blockUser(admin.getEmail(), email);
            System.out.println("Пользователь " + email + " заблокирован.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Удаляет пользователя.
     */
    public void deleteUser(User admin) {
        if (!admin.isAdmin()) {
            System.out.println("Ошибка: у вас нет прав администратора.");
            return;
        }

        System.out.println("Введите email пользователя для удаления:");
        String email = scanner.nextLine();

        try {
            authService.deleteUser(admin.getEmail(), email);
            System.out.println("Пользователь " + email + " удалён.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}