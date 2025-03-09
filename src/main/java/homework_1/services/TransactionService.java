package homework_1.services;

import homework_1.domain.Category;
import homework_1.domain.User;
import homework_1.repositories.NotificationService;
import homework_1.repositories.TransactionRepository;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.repositories.UserRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Сервис управления транзакциями пользователей.
 */
public class TransactionService implements TransactionInputPort {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * Конструктор TransactionService.
     *
     * @param transactionRepository репозиторий транзакций
     * @param userRepository        репозиторий пользователей
     */
    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository,
                              NotificationService notificationService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
    }

    /**
     * Создает новую транзакцию.
     *
     * @param transaction транзакция
     */
    public void createTransaction(Transaction transaction) {
        if (isBudgetExceeded(transaction.getUserEmail())) {
            notificationService.sendNotification(transaction.getUserEmail(),
                    "Ваши расходы превысили установленный бюджет!");
            throw new IllegalStateException("Ваши расходы превышают установленный бюджет!");
        }

        transactionRepository.save(transaction);
    }

    /**
     * Возвращает список транзакций пользователя.
     *
     * @param userEmail почта пользователя
     * @return список транзакций
     */
    @Override
    public List<Transaction> getTransactions(String userEmail) {
        return Collections.unmodifiableList(transactionRepository.findByUserEmail(userEmail));
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
     * @param userEmail     почта пользователя
     * @param transactionId идентификатор транзакции
     */
    @Override
    public void deleteTransaction(String userEmail, UUID transactionId) {
        transactionRepository.delete(userEmail, transactionId);
    }

    /**
     * Подсчитывает текущий баланс пользователя.
     *
     * @param userEmail идентификатор пользователя
     * @return текущий баланс
     */
    @Override
    public double calculateBalance(String userEmail) {
        List<Transaction> transactions = transactionRepository.findByUserEmail(userEmail);
        return transactions.stream()
                .mapToDouble(t -> t.getType() == TransactionType.INCOME ? t.getAmount() : -t.getAmount())
                .sum();
    }

    /**
     * Возвращает список транзакций пользователя за определенную дату.
     *
     * @param email почта пользователя
     * @param date  дата транзакции
     * @return список транзакций за указанную дату
     */
    public List<Transaction> getTransactionsByDate(String email, LocalDate date) {
        return transactionRepository.findByUserEmailAndDate(email, date);
    }

    /**
     * Возвращает список транзакций пользователя по указанной категории.
     *
     * @param email    почта пользователя
     * @param category категория транзакции
     * @return список транзакций указанной категории
     */
    public List<Transaction> getTransactionsByCategory(String email, Category category) {
        return transactionRepository.findByUserEmailAndCategory(email, category);
    }

    /**
     * Возвращает список транзакций пользователя по указанному типу (доход или расход).
     *
     * @param email почта пользователя
     * @param type  тип транзакции (доход или расход)
     * @return список транзакций указанного типа
     */
    public List<Transaction> getTransactionsByType(String email, TransactionType type) {
        return transactionRepository.findByUserEmailAndType(email, type);
    }

    public boolean isBudgetExceeded(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        double expenses = transactionRepository.findByUserEmail(email).stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        return expenses > user.getMonthlyBudget();
    }
}