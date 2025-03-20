package homework_1.ui;

import homework_1.domain.Goal;
import homework_1.domain.User;
import homework_1.services.GoalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Обработчик команд для работы с целями.
 */
public class GoalConsoleHandler {
    private static final Logger logger = LoggerFactory.getLogger(GoalConsoleHandler.class);
    private final GoalService goalService;
    private final Scanner scanner;

    public GoalConsoleHandler(GoalService goalService, Scanner scanner) {
        this.goalService = goalService;
        this.scanner = scanner;
    }

    /**
     * Создаёт новую цель.
     */
    public void createGoal(User user) {
        System.out.println("Введите название цели:");
        String name = scanner.nextLine();

        System.out.println("Введите сумму накопления:");
        double amount = Double.parseDouble(scanner.nextLine());

        goalService.createGoal(user.getId(), name, amount);
        System.out.println("Цель добавлена.");
        logger.info("Пользователь {} создал новую цель: {} на сумму {}", user.getEmail(), name, amount);
    }

    /**
     * Отображает список целей пользователя.
     */
    public void showGoals(User user) throws SQLException {
        List<Goal> goals = goalService.getUserGoals(user.getId());
        goals.forEach(goal -> System.out.println(goal.getName() + " - " +
                goal.getCurrentAmount() + "/" + goal.getTargetAmount()));
    }

    /**
     * Пополняет цель на указанную сумму.
     */
    public void addToGoal() {
        System.out.println("Введите название цели:");
        String goalName = scanner.nextLine();

        System.out.println("Введите сумму для пополнения:");
        double amount = Double.parseDouble(scanner.nextLine());

        goalService.addToGoal(goalName, amount);
        System.out.println("Цель пополнена.");
    }

    /**
     * Удаляет цель.
     */
    public void deleteGoal() {
        System.out.println("Введите ID цели для удаления:");
        long goalId = Long.parseLong(scanner.nextLine());

        goalService.deleteGoal(goalId);
        System.out.println("Цель удалена.");
    }
}