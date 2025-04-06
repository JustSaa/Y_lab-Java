package homework_1.dto;

public class IncomeResponseDto {
    private double income;

    public IncomeResponseDto(double income) {
        this.income = income;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }
}
