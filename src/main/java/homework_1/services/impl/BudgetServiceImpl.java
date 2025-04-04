package homework_1.services.impl;

import homework_1.aspect.Audit;
import homework_1.aspect.LogExecutionTime;
import homework_1.domain.Budget;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.repositories.BudgetRepository;
import homework_1.repositories.TransactionRepository;
import homework_1.services.BudgetService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Реализация сервиса управления бюджетом.
 */
@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetServiceImpl(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    @Audit(action = "Установить бюджет")
    @LogExecutionTime
    @Override
    public void setUserBudget(long userId, double limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Бюджет должен быть больше 0.");
        }
        budgetRepository.save(new Budget(userId, limit));
    }

    @Audit(action = "Получить бюджет пользователя по ID")
    @LogExecutionTime
    @Override
    public Optional<Budget> getUserBudget(long userId) {
        return budgetRepository.findByUserId(userId);
    }

    @Audit(action = "Проверить бюджет пользователя")
    @LogExecutionTime
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