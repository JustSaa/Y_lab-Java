package homework_1.services.impl;

import homework_1.domain.Budget;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.repositories.BudgetRepository;
import homework_1.repositories.TransactionRepository;
import homework_1.services.BudgetService;

import java.util.Optional;

/**
 * Реализация сервиса управления бюджетом.
 */
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetServiceImpl(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void setUserBudget(long userId, double limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Бюджет должен быть больше 0.");
        }
        budgetRepository.save(new Budget(userId, limit));
    }

    @Override
    public Optional<Budget> getUserBudget(long userId) {
        return budgetRepository.findByUserId(userId);
    }

    @Override
    public boolean isBudgetExceeded(long userId) {
        Optional<Budget> budgetOpt = budgetRepository.findByUserId(userId);
        if (budgetOpt.isEmpty()) {
            return false;
        }

        Budget budget = budgetOpt.get();
        double totalExpenses = transactionRepository.findByUserIdAndType(userId, TransactionType.EXPENSE)
                .stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        return totalExpenses > budget.getLimit();
    }
}