package homework_1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class CreateGoalDto {
    @Min(1)
    private long userId;
    @NotBlank
    private String name;
    @Positive
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
