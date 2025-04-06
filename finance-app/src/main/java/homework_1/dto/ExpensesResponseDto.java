package homework_1.dto;

public class ExpensesResponseDto {
    private double expenses;

    public ExpensesResponseDto(double expenses) {
        this.expenses = expenses;
    }

    public double getExpenses() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }
}
