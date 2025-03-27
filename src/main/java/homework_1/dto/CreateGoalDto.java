package homework_1.dto;

public class CreateGoalDto {
    private long userId;
    private String name;
    private double targetAmount;

    public CreateGoalDto(long userId, String name, double targetAmount) {
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
    }

    public CreateGoalDto() {
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
