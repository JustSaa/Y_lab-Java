package homework_1.application.services;

import homework_1.application.ports.in.TransactionInputPort;
import homework_1.application.ports.out.TransactionRepository;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Сервис управления транзакциями пользователей.
 */
public class TransactionService implements TransactionInputPort {

    private final TransactionRepository transactionRepository;

    /**
     * Конструктор TransactionService.
     *
     * @param transactionRepository репозиторий транзакций
     */
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Создает новую транзакцию.
     *
     * @param transaction транзакция
     */
    @Override
    public void createTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    /**
     * Возвращает список транзакций пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список транзакций
     */
    @Override
    public List<Transaction> getTransactions(UUID userId) {
        return Collections.unmodifiableList(transactionRepository.findByUserId(userId));
    }

    /**
     * Обновляет существующую транзакцию.
     *
     * @param transaction транзакция для обновления
     */
    @Override
    public void updateTransaction(Transaction transaction) {
        transactionRepository.update(transaction);
    }

    /**
     * Удаляет транзакцию по идентификатору.
     *
     * @param userId        идентификатор пользователя
     * @param transactionId идентификатор транзакции
     */
    @Override
    public void deleteTransaction(UUID userId, UUID transactionId) {
        transactionRepository.delete(userId, transactionId);
    }

    /**
     * Подсчитывает текущий баланс пользователя.
     *
     * @param userId идентификатор пользователя
     * @return текущий баланс
     */
    @Override
    public double calculateBalance(UUID userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream()
                .mapToDouble(t -> t.getType() == TransactionType.INCOME ? t.getAmount() : -t.getAmount())
                .sum();
    }
}