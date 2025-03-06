package homework_1.adapters.out;

import homework_1.application.ports.out.TransactionRepository;
import homework_1.domain.Transaction;

import java.util.*;

/**
 * Реализация TransactionRepository на основе HashMap и List (in-memory).
 */
public class InMemoryTransactionRepository implements TransactionRepository {

    /**
     * Хранение транзакций по ID пользователя.
     */
    private final Map<UUID, List<Transaction>> transactionsMap = new HashMap<>();

    /**
     * Сохраняет транзакцию в памяти.
     *
     * @param transaction транзакция для сохранения
     */
    @Override
    public void save(Transaction transaction) {
        transactionsMap
                .computeIfAbsent(transaction.getUserId(), k -> new ArrayList<>())
                .add(transaction);
    }

    /**
     * Возвращает список транзакций пользователя.
     *
     * @param userId ID пользователя
     * @return список транзакций
     */
    @Override
    public List<Transaction> findByUserId(UUID userId) {
        return Collections.unmodifiableList(
                transactionsMap.getOrDefault(userId, Collections.emptyList()));
    }

    /**
     * Поиск транзакции по идентификатору.
     *
     * @param userId        идентификатор пользователя
     * @param transactionId ID транзакции
     * @return Optional транзакции
     */
    @Override
    public Optional<Transaction> findById(UUID userId, UUID transactionId) {
        return transactionsMap.getOrDefault(userId, List.of())
                .stream()
                .filter(transaction -> transaction.getId().equals(transactionId))
                .findFirst();
    }

    /**
     * Обновляет существующую транзакцию.
     *
     * @param transaction обновлённая транзакция
     */
    @Override
    public void update(Transaction transaction) {
        UUID userId = transaction.getUserId();
        List<Transaction> transactions = transactionsMap.get(userId);
        if (transactions == null) {
            return;
        }

        transactions.replaceAll(existingTransaction ->
                existingTransaction.getId().equals(transaction.getId())
                        ? transaction
                        : existingTransaction
        );
    }

    /**
     * Удаляет транзакцию пользователя.
     *
     * @param userId        ID пользователя
     * @param transactionId ID транзакции
     */
    @Override
    public void delete(UUID userId, UUID transactionId) {
        transactionsMap.getOrDefault(userId, new ArrayList<>())
                .removeIf(transaction -> transaction.getId().equals(transactionId));
    }
}