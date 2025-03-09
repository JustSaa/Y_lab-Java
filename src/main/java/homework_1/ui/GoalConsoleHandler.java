package homework_1.ui;

import homework_1.domain.Goal;
import homework_1.domain.User;
import homework_1.services.GoalService;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Обработчик команд для работы с целями.
 */
public class GoalConsoleHandler {
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

        goalService.createGoal(user.getEmail(), name, amount);
        System.out.println("Цель добавлена.");
    }

    /**
     * Отображает список целей пользователя.
     */
    public void showGoals(User user) {
        List<Goal> goals = goalService.getUserGoals(user.getEmail());
        goals.forEach(goal -> System.out.println(goal.getName() + " - " +
                goal.getCurrentAmount() + "/" + goal.getTargetAmount()));
    }

    /**
     * Пополняет цель на указанную сумму.
     */
    public void addToGoal() {
        System.out.println("Введите ID цели:");
        UUID goalId = UUID.fromString(scanner.nextLine());

        System.out.println("Введите сумму для пополнения:");
        double amount = Double.parseDouble(scanner.nextLine());

        goalService.addToGoal(goalId, amount);
        System.out.println("Цель пополнена.");
    }

    /**
     * Удаляет цель.
     */
    public void deleteGoal() {
        System.out.println("Введите ID цели для удаления:");
        UUID goalId = UUID.fromString(scanner.nextLine());

        goalService.deleteGoal(goalId);
        System.out.println("Цель удалена.");
    }
}