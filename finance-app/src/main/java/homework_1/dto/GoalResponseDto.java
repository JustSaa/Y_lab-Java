package homework_1.dto;

public class GoalResponseDto {
    private long userId;
    private String name;
    private double targetAmount;

    public GoalResponseDto(long userId, String name, double targetAmount) {
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }
}
