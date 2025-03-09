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
     * Хранение транзакций по Почте пользователя.
     */
    private final Map<String, List<Transaction>> transactionsMap = new HashMap<>();

    /**
     * Сохраняет транзакцию в памяти.
     *
     * @param transaction транзакция для сохранения
     */
    @Override
    public void save(Transaction transaction) {
        transactionsMap
                .computeIfAbsent(transaction.getUserEmail(), k -> new ArrayList<>())
                .add(transaction);
    }

    /**
     * Возвращает список транзакций пользователя.
     *
     * @param userEmail почта пользователя
     * @return список транзакций
     */
    @Override
    public List<Transaction> findByUserEmail(String userEmail) {
        return Collections.unmodifiableList(
                transactionsMap.getOrDefault(userEmail, Collections.emptyList()));
    }

    /**
     * Поиск транзакции по идентификатору.
     *
     * @param userEmail     почта пользователя
     * @param transactionId ID транзакции
     * @return Optional транзакции
     */
    @Override
    public Optional<Transaction> findById(String userEmail, UUID transactionId) {
        return transactionsMap.getOrDefault(userEmail, List.of())
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
        String userEmail = transaction.getUserEmail();
        List<Transaction> transactions = transactionsMap.get(userEmail);
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
     * @param userEmail     почта пользователя
     * @param transactionId ID транзакции
     */
    @Override
    public void delete(String userEmail, UUID transactionId) {
        transactionsMap.getOrDefault(userEmail, new ArrayList<>())
                .removeIf(transaction -> transaction.getId().equals(transactionId));
    }

    /**
     * Возвращает список транзакций пользователя за указанную дату.
     *
     * @param email почта пользователя
     * @param date  дата транзакции
     * @return список транзакций за указанную дату
     */
    @Override
    public List<Transaction> findByUserEmailAndDate(String email, LocalDate date) {
        return transactionsMap.getOrDefault(email, List.of())
                .stream()
                .filter(t -> t.getDate().equals(date))
                .toList();
    }

    /**
     * Возвращает список транзакций пользователя по категории.
     *
     * @param email    почта пользователя
     * @param category категория транзакции
     * @return список транзакций указанной категории
     */
    @Override
    public List<Transaction> findByUserEmailAndCategory(String email, Category category) {
        return transactionsMap.getOrDefault(email, List.of())
                .stream()
                .filter(t -> t.getCategory().equals(category))
                .toList();
    }

    /**
     * Возвращает список транзакций пользователя по их типу (доход или расход).
     *
     * @param email почта пользователя
     * @param type  тип транзакции (доход или расход)
     * @return список транзакций указанного типа
     */
    @Override
    public List<Transaction> findByUserEmailAndType(String email, TransactionType type) {
        return transactionsMap.getOrDefault(email, List.of())
                .stream()
                .filter(t -> t.getType().equals(type))
                .toList();
    }
}