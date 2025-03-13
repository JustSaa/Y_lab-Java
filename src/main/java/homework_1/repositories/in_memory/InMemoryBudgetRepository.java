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
    private final Map<String, Budget> budgets = new HashMap<>();

    @Override
    public void save(Budget budget) {
        budgets.put(budget.getUserEmail(), budget);
    }

    @Override
    public Optional<Budget> findByUserEmail(String userEmail) {
        return Optional.ofNullable(budgets.get(userEmail));
    }
}