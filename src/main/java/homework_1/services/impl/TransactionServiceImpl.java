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
        double totalExpenses = transactionRepository.findByUserEmailAndType(transaction.getUserEmail(), TransactionType.EXPENSE)
                .stream().mapToDouble(Transaction::getAmount).sum();

        double budgetLimit = budgetService.getUserBudget(transaction.getUserEmail())
                .map(Budget::getLimit)
                .orElse(Double.MAX_VALUE);

        if (transaction.getType() == TransactionType.EXPENSE && (totalExpenses + transaction.getAmount()) > budgetLimit) {
            notificationService.sendNotification(transaction.getUserEmail(),
                    "Ваши расходы превысили установленный бюджет!");
            System.out.println("Внимание! Ваши расходы превысили установленный бюджет. Транзакция не сохранена.");
            return;
        }

        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactions(String userEmail) {
        return Collections.unmodifiableList(transactionRepository.findByUserEmail(userEmail));
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        transactionRepository.update(transaction);
    }

    @Override
    public void deleteTransaction(String userEmail, long transactionId) {
        transactionRepository.delete(userEmail, transactionId);
    }

    @Override
    public double calculateBalance(String userEmail) {
        List<Transaction> transactions = transactionRepository.findByUserEmail(userEmail);
        return transactions.stream()
                .mapToDouble(t -> t.getType() == TransactionType.INCOME ? t.getAmount() : -t.getAmount())
                .sum();
    }

    @Override
    public List<Transaction> getTransactionsByDate(String email, LocalDate date) {
        return transactionRepository.findByUserEmailAndDate(email, date);
    }

    @Override
    public List<Transaction> getTransactionsByCategory(String email, Category category) {
        return transactionRepository.findByUserEmailAndCategory(email, category);
    }

    @Override
    public List<Transaction> getTransactionsByType(String email, TransactionType type) {
        return transactionRepository.findByUserEmailAndType(email, type);
    }

    @Override
    public boolean isBudgetExceeded(String email) {
        return false;
    }
}