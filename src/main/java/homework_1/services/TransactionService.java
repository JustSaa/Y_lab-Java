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
    List<Transaction> getTransactions(long userId);

    /**
     * Обновление существующей транзакции.
     */
    void updateTransaction(Transaction transaction);

    /**
     * Удаление транзакции.
     */
    void deleteTransaction(long userId, long transactionId);

    /**
     * Подсчет текущего баланса пользователя.
     */
    double calculateBalance(long userId);

    /**
     * Возвращает список транзакций пользователя за указанную дату.
     *
     * @param userId Id пользователя
     * @param date   дата транзакции
     * @return список транзакций за указанную дату
     */
    List<Transaction> getTransactionsByDate(long userId, LocalDate date);

    /**
     * Возвращает список транзакций пользователя по категории.
     *
     * @param userId   Id пользователя
     * @param category категория транзакции
     * @return список транзакций указанной категории
     */
    List<Transaction> getTransactionsByCategory(long userId, Category category);

    /**
     * Возвращает список транзакций пользователя по их типу (доход или расход).
     *
     * @param userId Id пользователя
     * @param type   тип транзакции (доход или расход)
     * @return список транзакций указанного типа
     */
    List<Transaction> getTransactionsByType(long userId, TransactionType type);

    /**
     * Проверка бюджета пользователя.
     *
     * @param userId Id пользователя
     */
    boolean isBudgetExceeded(long userId);
}