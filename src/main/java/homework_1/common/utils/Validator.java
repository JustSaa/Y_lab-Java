package homework_1.common.utils;

import homework_1.domain.Category;
import homework_1.domain.TransactionType;

import java.util.regex.Pattern;

/**
 * Класс для валидации пользовательского ввода.
 */
public class Validator {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    /**
     * Проверяет корректность email.
     *
     * @param email введенный email
     * @return true, если email корректен
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Проверяет, является ли введенное число положительным.
     *
     * @param amount сумма транзакции
     * @return true, если сумма > 0
     */
    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }

    /**
     * Проверяет корректность типа транзакции (1 или 2).
     *
     * @param typeChoice введенный номер типа
     * @return соответствующий TransactionType или null, если неверно
     */
    public static TransactionType getTransactionType(int typeChoice) {
        return switch (typeChoice) {
            case 1 -> TransactionType.INCOME;
            case 2 -> TransactionType.EXPENSE;
            default -> null;
        };
    }

    /**
     * Проверяет, является ли введенная категория валидной.
     *
     * @param categoryInput введенная строка категории
     * @return Category или null, если категория некорректна
     */
    public static Category getCategory(String categoryInput) {
        try {
            return Category.valueOf(categoryInput.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}