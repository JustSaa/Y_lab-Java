package homework_1.domain;

/**
 * Финансовая цель пользователя.
 */
public class Goal {
    private long id;
    private final long userId;
    private String name;
    private final double targetAmount;
    private double currentAmount;

    /**
     * Конструктор создания цели.
     *
     * @param userId       Id пользователя
     * @param name         название цели
     * @param targetAmount сумма, которую нужно накопить
     */
    public Goal(long id, long userId, String name, double targetAmount, double currentAmount) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
    }

    /**
     * Конструктор создания цели.
     *
     * @param userId       Id пользователя
     * @param name         название цели
     * @param targetAmount сумма, которую нужно накопить
     */
    public Goal(long userId, String name, double targetAmount) {
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
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