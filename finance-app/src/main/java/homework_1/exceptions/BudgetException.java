package homework_1.exceptions;

/**
 * Исключение при ошибках бюджета.
 */
public class BudgetException extends RuntimeException {
    public BudgetException(String message) {
        super(message);
    }
}
