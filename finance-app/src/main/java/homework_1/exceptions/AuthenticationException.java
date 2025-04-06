package homework_1.exceptions;

/**
 * Исключение при ошибках авторизации.
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}