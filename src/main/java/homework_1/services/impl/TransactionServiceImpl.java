package homework_1.services.impl;

import homework_1.domain.Budget;
import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.repositories.TransactionRepository;
import homework_1.services.BudgetService;
import homework_1.services.NotificationService;
import homework_1.services.TransactionService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Реализация сервиса управления транзакциями.
 */
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetService budgetService;
    private final NotificationService notificationService;

    /**
     * Конструктор TransactionService.
     *
     * @param transactionRepository репозиторий транзакций
     * @param budgetService         сервис бюджета
     * @param notificationService   сервис уведомлений
     */
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  BudgetService budgetService,
                                  NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.budgetService = budgetService;
        this.notificationService = notificationService;
    }

    /**
     * Создает новую транзакцию.
     *
     * @param transaction транзакция
     */
    @Override
    public void createTransaction(Transaction transaction) {
        double totalExpenses = transactionRepository.findByUserIdAndType(transaction.getUserId(), TransactionType.EXPENSE)
                .stream().mapToDouble(Transaction::getAmount).sum();

        double budgetLimit = budgetService.getUserBudget(transaction.getUserId())
                .map(Budget::getLimit)
                .orElse(Double.MAX_VALUE);

        if (transaction.getType() == TransactionType.EXPENSE && (totalExpenses + transaction.getAmount()) > budgetLimit) {
            notificationService.sendNotification(transaction.getUserId(),
                    "Ваши расходы превысили установленный бюджет!");
            System.out.println("Внимание! Ваши расходы превысили установленный бюджет. Транзакция не сохранена.");
            return;
        }

        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactions(long userId) {
        return Collections.unmodifiableList(transactionRepository.findByUserId(userId));
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        transactionRepository.update(transaction);
    }

    @Override
    public void deleteTransaction(long userId, long transactionId) {
        transactionRepository.delete(userId, transactionId);
    }

    @Override
    public double calculateBalance(long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream()
                .mapToDouble(t -> t.getType() == TransactionType.INCOME ? t.getAmount() : -t.getAmount())
                .sum();
    }

    @Override
    public List<Transaction> getTransactionsByDate(long userId, LocalDate date) {
        return transactionRepository.findByUserIdAndDate(userId, date);
    }

    @Override
    public List<Transaction> getTransactionsByCategory(long userId, Category category) {
        return transactionRepository.findByUserIdAndCategory(userId, category);
    }

    @Override
    public List<Transaction> getTransactionsByType(long userId, TransactionType type) {
        return transactionRepository.findByUserIdAndType(userId, type);
    }

    @Override
    public boolean isBudgetExceeded(long userId) {
        return false;
    }
}