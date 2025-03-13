package homework_1.domain;

/**
 * Сущность бюджета пользователя.
 */
public class Budget {
    private final String userEmail;
    private final double limit;

    public Budget(String userEmail, double limit) {
        this.userEmail = userEmail;
        this.limit = limit;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public double getLimit() {
        return limit;
    }
}