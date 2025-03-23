package homework_1.dto;

import homework_1.domain.Category;
import homework_1.domain.TransactionType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class TransactionRequestDto {

    @NotNull(message = "userId не может быть null")
    private Long userId;

    @NotNull(message = "amount обязателен")
    @Positive(message = "Сумма должна быть положительной")
    private Double amount;

    @NotNull(message = "Тип обязателен")
    private TransactionType type;

    @NotNull(message = "Категория обязательна")
    private Category category;

    @NotNull(message = "Дата обязательна")
    private LocalDate date;

    private String description;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}