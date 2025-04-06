package homework_1.repositories.in_memory;

import homework_1.domain.Budget;
import homework_1.repositories.BudgetRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Реализация репозитория бюджетов в памяти.
 */
public class InMemoryBudgetRepository implements BudgetRepository {
    private final Map<Long, Budget> budgets = new HashMap<>();

    @Override
    public void save(Budget budget) {
        budgets.put(budget.getUserId(), budget);
    }

    @Override
    public Optional<Budget> findByUserId(long userId) {
        return Optional.ofNullable(budgets.get(userId));
    }
}