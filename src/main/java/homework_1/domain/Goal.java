package homework_1.domain;

import java.util.UUID;

/**
 * Финансовая цель пользователя.
 */
public class Goal {
    private final UUID id;
    private final String userEmail;
    private final String name;
    private final double targetAmount;
    private double currentAmount;

    /**
     * Конструктор создания цели.
     *
     * @param userEmail    почта пользователя
     * @param name         название цели
     * @param targetAmount сумма, которую нужно накопить
     */
    public Goal(String userEmail, String name, double targetAmount) {
        this.id = UUID.randomUUID();
        this.userEmail = userEmail;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
    }

    public UUID getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getName() {
        return name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void addToGoal(double amount) {
        if (amount > 0) {
            this.currentAmount += amount;
        }
    }

    public boolean isAchieved() {
        return currentAmount >= targetAmount;
    }
}