package homework_1.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность финансовой транзакции.
 */
public class Transaction {

    /**
     * Уникальный идентификатор транзакции.
     */
    private final UUID id;

    /**
     * Идентификатор пользователя, которому принадлежит транзакция.
     */
    private final UUID userId;

    /**
     * Сумма транзакции.
     */
    private double amount;

    /**
     * Тип транзакции: доход или расход.
     */
    private TransactionType type;

    /**
     * Категория транзакции.
     */
    private Category category;

    /**
     * Дата совершения транзакции.
     */
    private LocalDate date;

    /**
     * Описание транзакции.
     */
    private String description;

    /**
     * Конструктор для создания новой транзакции.
     *
     * @param userId      идентификатор пользователя
     * @param amount      сумма транзакции
     * @param type        тип транзакции (доход или расход)
     * @param category    категория транзакции
     * @param date        дата транзакции
     * @param description описание транзакции
     */
    public Transaction(UUID userId, double amount, TransactionType type,
                       Category category, LocalDate date, String description) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0
                && Objects.equals(id, that.id) && Objects.equals(userId, that.userId)
                && type == that.type && category == that.category && Objects.equals(date, that.date)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, amount, type, category, date, description);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userId=" + userId +
                ", amount=" + amount +
                ", type=" + type +
                ", category=" + category +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }
}
