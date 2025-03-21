package homework_1.ui;

import homework_1.domain.User;
import homework_1.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Обработчик команд администратора.
 */
public class AdminConsoleHandler {
    private static final Logger logger = LoggerFactory.getLogger(AdminConsoleHandler.class);
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
        if (users.isEmpty()) {
            logger.info("В системе пока нет зарегистрированных пользователей.");
            System.out.println("В системе пока нет зарегистрированных пользователей.");
            return;
        }

        logger.info("Список пользователей:");
        users.forEach(user -> {
            String userInfo = user.getEmail() + " | " +
                    (user.isAdmin() ? "Админ" : "Пользователь") +
                    (user.isBlocked() ? " | Заблокирован" : "");

            logger.info(userInfo);
            System.out.println(userInfo);
        });
    }

    /**
     * Блокирует пользователя.
     */
    public void blockUser(User admin) {
        if (!admin.isAdmin()) {
            logger.warn("Попытка блокировки без прав администратора.");
            System.out.println("Ошибка: у вас нет прав администратора.");
            return;
        }

        System.out.println("Введите email пользователя для блокировки:");
        String email = scanner.nextLine();

        try {
            authService.blockUser(admin.getEmail(), email);
            logger.info("Пользователь {} заблокирован администратором {}", email, admin.getEmail());
            System.out.println("Пользователь " + email + " заблокирован.");
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка блокировки пользователя {}: {}", email, e.getMessage());
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Удаляет пользователя.
     */
    public void deleteUser(User admin) {
        if (!admin.isAdmin()) {
            logger.warn("Попытка удаления пользователя без прав администратора.");
            System.out.println("Ошибка: у вас нет прав администратора.");
            return;
        }

        System.out.println("Введите email пользователя для удаления:");
        String email = scanner.nextLine();

        try {
            authService.deleteUser(admin.getEmail(), email);
            logger.info("Пользователь {} удалён администратором {}", email, admin.getEmail());
            System.out.println("Пользователь " + email + " удалён.");
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка удаления пользователя {}: {}", email, e.getMessage());
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}