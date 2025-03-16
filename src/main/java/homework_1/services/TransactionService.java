package homework_1.services;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Интерфейс сервиса для работы с транзакциями.
 */
public interface TransactionService {

    /**
     * Создание новой транзакции.
     */
    void createTransaction(Transaction transaction);

    /**
     * Получение всех транзакций пользователя.
     */
    List<Transaction> getTransactions(String userEmail);

    /**
     * Обновление существующей транзакции.
     */
    void updateTransaction(Transaction transaction);

    /**
     * Удаление транзакции.
     */
    void deleteTransaction(String userEmail, long transactionId);

    /**
     * Подсчет текущего баланса пользователя.
     */
    double calculateBalance(String userEmail);

    /**
     * Возвращает список транзакций пользователя за указанную дату.
     *
     * @param email почта пользователя
     * @param date  дата транзакции
     * @return список транзакций за указанную дату
     */
    List<Transaction> getTransactionsByDate(String email, LocalDate date);

    /**
     * Возвращает список транзакций пользователя по категории.
     *
     * @param email    почта пользователя
     * @param category категория транзакции
     * @return список транзакций указанной категории
     */
    List<Transaction> getTransactionsByCategory(String email, Category category);

    /**
     * Возвращает список транзакций пользователя по их типу (доход или расход).
     *
     * @param email почта пользователя
     * @param type  тип транзакции (доход или расход)
     * @return список транзакций указанного типа
     */
    List<Transaction> getTransactionsByType(String email, TransactionType type);

    /**
     * Проверка бюджета пользователя.
     *
     * @param email почта пользователя
     */
    boolean isBudgetExceeded(String email);
}