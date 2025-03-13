package homework_1.common.exceptions;

/**
 * Исключение при ошибках авторизации.
 */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}