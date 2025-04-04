package homework_1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SetBudgetDto {

    @NotNull(message = "userId обязателен")
    private Long userId;

    @Positive(message = "Лимит бюджета должен быть положительным")
    private double limit;

    public SetBudgetDto() {
    }
    public SetBudgetDto(Long userId, double limit) {
        this.userId = userId;
        this.limit = limit;
    }

    public Long getUserId() {
        return userId;
    }

    public double getLimit() {
        return limit;
    }
}