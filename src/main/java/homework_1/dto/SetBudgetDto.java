package homework_1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SetBudgetDto {

    @NotNull(message = "userId обязателен")
    private Long userId;

    @Min(value = 1, message = "Лимит бюджета должен быть положительным")
    private double limit;

    public Long getUserId() {
        return userId;
    }

    public double getLimit() {
        return limit;
    }
}