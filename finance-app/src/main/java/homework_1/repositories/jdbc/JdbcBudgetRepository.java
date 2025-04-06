package homework_1.repositories.jdbc;

import homework_1.domain.Budget;
import homework_1.exceptions.BudgetRepositoryException;
import homework_1.repositories.BudgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Реализация репозитория {@link BudgetRepository} для работы с бюджетами пользователей
 * с использованием JDBC и базы данных PostgreSQL.
 */
@Repository
public class JdbcBudgetRepository implements BudgetRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcBudgetRepository.class);

    private static final String INSERT_SQL = """
        INSERT INTO finance.budgets (id, user_id, budget_limit) 
        VALUES (nextval('finance.budgets_seq'), ?, ?)
    """;

    private static final String SELECT_SQL = """
        SELECT user_id, budget_limit 
        FROM finance.budgets 
        WHERE user_id = ?
    """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcBudgetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Budget budget) {
        int rows = jdbcTemplate.update(INSERT_SQL, budget.getUserId(), budget.getLimit());
        if (rows == 0) {
            log.warn("Не удалось сохранить бюджет: {}", budget);
            throw new BudgetRepositoryException("Не удалось вставить бюджет");
        }
        log.info("Бюджет сохранён для userId={}, limit={}", budget.getUserId(), budget.getLimit());
    }

    @Override
    public Optional<Budget> findByUserId(long userId) {
        try {
            return jdbcTemplate.query(SELECT_SQL, rs -> {
                if (rs.next()) {
                    Budget budget = new Budget(rs.getLong("user_id"), rs.getDouble("budget_limit"));
                    log.debug("Бюджет найден: {}", budget);
                    return Optional.of(budget);
                }
                log.info("Бюджет не найден для userId={}", userId);
                return Optional.empty();
            }, userId);
        } catch (DataAccessException e) {
            log.error("Ошибка при поиске бюджета по userId={}", userId, e);
            throw new BudgetRepositoryException("Ошибка поиска бюджета", e);
        }
    }
}
