package homework_1.repositories.in_memory;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.repositories.TransactionRepository;

import java.time.LocalDate;
import java.util.*;

/**
 * Реализация TransactionRepository на основе HashMap и List (in-memory).
 */
public class InMemoryTransactionRepository implements TransactionRepository {

    /**
     * Хранение транзакций по Id пользователя.
     */
    private final Map<Long, List<Transaction>> transactionsMap = new HashMap<>();

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
     * @param userId Id пользователя
     * @return список транзакций
     */
    @Override
    public List<Transaction> findByUserId(long userId) {
        return Collections.unmodifiableList(
                transactionsMap.getOrDefault(userId, Collections.emptyList()));
    }

    /**
     * Поиск транзакции по идентификатору.
     *
     * @param userId        Id пользователя
     * @param transactionId ID транзакции
     * @return Optional транзакции
     */
    @Override
    public Optional<Transaction> findById(long userId, long transactionId) {
        return transactionsMap.getOrDefault(userId, List.of())
                .stream()
                .filter(transaction -> transaction.getId() == (transactionId))
                .findFirst();
    }

    /**
     * Обновляет существующую транзакцию.
     *
     * @param transaction обновлённая транзакция
     */
    @Override
    public void update(Transaction transaction) {
        Long userId = transaction.getUserId();
        List<Transaction> transactions = transactionsMap.get(userId);
        if (transactions == null) {
            return;
        }

        transactions.replaceAll(existingTransaction ->
                existingTransaction.getId() == (transaction.getId())
                        ? transaction
                        : existingTransaction
        );
    }

    /**
     * Удаляет транзакцию пользователя.
     *
     * @param userId        Id пользователя
     * @param transactionId ID транзакции
     */
    @Override
    public void delete(long userId, long transactionId) {
        transactionsMap.getOrDefault(userId, new ArrayList<>())
                .removeIf(transaction -> transaction.getId() == transactionId);
    }

    /**
     * Возвращает список транзакций пользователя за указанную дату.
     *
     * @param userId Id пользователя
     * @param date   дата транзакции
     * @return список транзакций за указанную дату
     */
    @Override
    public List<Transaction> findByUserIdAndDate(long userId, LocalDate date) {
        return transactionsMap.getOrDefault(userId, List.of())
                .stream()
                .filter(t -> t.getDate().equals(date))
                .toList();
    }

    /**
     * Возвращает список транзакций пользователя по категории.
     *
     * @param userId   Id пользователя
     * @param category категория транзакции
     * @return список транзакций указанной категории
     */
    @Override
    public List<Transaction> findByUserIdAndCategory(long userId, Category category) {
        return transactionsMap.getOrDefault(userId, List.of())
                .stream()
                .filter(t -> t.getCategory().equals(category))
                .toList();
    }

    /**
     * Возвращает список транзакций пользователя по их типу (доход или расход).
     *
     * @param userId Id пользователя
     * @param type   тип транзакции (доход или расход)
     * @return список транзакций указанного типа
     */
    @Override
    public List<Transaction> findByUserIdAndType(long userId, TransactionType type) {
        return transactionsMap.getOrDefault(userId, List.of())
                .stream()
                .filter(t -> t.getType().equals(type))
                .toList();
    }
}