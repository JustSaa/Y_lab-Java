package homework_1.domain;

/**
 * Финансовая цель пользователя.
 */
public class Goal {
    private long id;
    private final String userEmail;
    private String name;
    private final double targetAmount;
    private double currentAmount;

    /**
     * Конструктор создания цели.
     *
     * @param userEmail    почта пользователя
     * @param name         название цели
     * @param targetAmount сумма, которую нужно накопить
     */
    public Goal(long id, String userEmail, String name, double targetAmount, double currentAmount) {
        this.id = id;
        this.userEmail = userEmail;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
    }

    /**
     * Конструктор создания цели.
     *
     * @param userEmail    почта пользователя
     * @param name         название цели
     * @param targetAmount сумма, которую нужно накопить
     */
    public Goal(String userEmail, String name, double targetAmount) {
        this.userEmail = userEmail;
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

    public String getUserEmail() {
        return userEmail;
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