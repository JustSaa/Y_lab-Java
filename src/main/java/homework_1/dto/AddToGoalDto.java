package homework_1.dto;

public class AddToGoalDto {
    private String goalName;
    private double amount;

    public AddToGoalDto(String goalName, double amount) {
        this.goalName = goalName;
        this.amount = amount;
    }

    public AddToGoalDto() {
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
