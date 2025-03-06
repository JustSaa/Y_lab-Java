package homework_1.application.ports.in;

import homework_1.domain.Transaction;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс сервиса для работы с транзакциями.
 */
public interface TransactionInputPort {

    /**
     * Создание новой транзакции.
     */
    void createTransaction(Transaction transaction);

    /**
     * Получение всех транзакций пользователя.
     */
    List<Transaction> getTransactions(UUID userId);

    /**
     * Обновление существующей транзакции.
     */
    void updateTransaction(Transaction transaction);

    /**
     * Удаление транзакции.
     */
    void deleteTransaction(UUID userId, UUID transactionId);

    /**
     * Подсчет текущего баланса пользователя.
     */
    double calculateBalance(UUID userId);
}