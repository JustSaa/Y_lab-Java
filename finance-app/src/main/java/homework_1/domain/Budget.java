package homework_1.domain;

/**
 * Сущность бюджета пользователя.
 */
public class Budget {
    private long id;
    private final long userId;
    private final double limit;

    public Budget(long userId, double limit) {
        this.userId = userId;
        this.limit = limit;
    }

    public Budget(long id, long userId, double limit) {
        this.id = id;
        this.userId = userId;
        this.limit = limit;
    }

    public long getUserId() {
        return userId;
    }

    public double getLimit() {
        return limit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}