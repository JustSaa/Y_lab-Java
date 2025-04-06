package homework_1.dto;

public class BudgetResponseDto {
    private Long userId;
    private double limit;

    public BudgetResponseDto(Long userId, double limit) {
        this.userId = userId;
        this.limit = limit;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }
}
