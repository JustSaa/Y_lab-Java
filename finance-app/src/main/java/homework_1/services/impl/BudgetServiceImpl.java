package homework_1.services.impl;

import audit.aspect.annotation.Audit;
import audit.aspect.annotation.LogExecutionTime;
import homework_1.domain.Budget;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.exceptions.BudgetException;
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
    public Budget createUserBudget(long userId, double limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Бюджет должен быть больше 0.");
        }
        Budget budget = new Budget(userId, limit);
        budgetRepository.save(budget);
        return budget;
    }

    @Audit(action = "Получить бюджет пользователя по ID")
    @LogExecutionTime
    @Override
    public Budget getUserBudget(long userId) {
        return budgetRepository.findByUserId(userId).orElseThrow(
                () -> new BudgetException("Бюжет для пользователя с ID " + userId + " не найден"));
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