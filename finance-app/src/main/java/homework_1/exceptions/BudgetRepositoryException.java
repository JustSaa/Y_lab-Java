package homework_1.exceptions;

public class BudgetRepositoryException extends RuntimeException {
    public BudgetRepositoryException(String message) {
        super(message);
    }

    public BudgetRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
