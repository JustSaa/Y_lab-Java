package homework_1.domain;

/**
 * Сущность бюджета пользователя.
 */
public class Budget {
    private long id;
    private final String userEmail;
    private final double limit;

    public Budget(String userEmail, double limit) {
        this.userEmail = userEmail;
        this.limit = limit;
    }

    public Budget(long id, String userEmail, double limit) {
        this.id = id;
        this.userEmail = userEmail;
        this.limit = limit;
    }

    public String getUserEmail() {
        return userEmail;
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