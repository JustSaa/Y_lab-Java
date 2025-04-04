package homework_1.repositories.jdbc;

import homework_1.domain.Budget;
import homework_1.repositories.BudgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Реализация репозитория {@link BudgetRepository} для работы с бюджетами пользователей
 * с использованием JDBC и базы данных PostgreSQL.
 */
@Repository
public class JdbcBudgetRepository implements BudgetRepository {
    private static final Logger log = LoggerFactory.getLogger(JdbcBudgetRepository.class);
    private static final String SQL_INSERT_BUDGET = "INSERT INTO finance.budgets (id, user_id, budget_limit) VALUES (nextval('finance.budgets_seq'), ?, ?)";
    private static final String SQL_SELECT_BY_USERID = "SELECT * FROM finance.budgets WHERE user_id = ?";
    private final DataSource dataSource;

    /**
     * Конструктор для создания репозитория бюджета.
     *
     * @param dataSource источник соединений с БД.
     */
    public JdbcBudgetRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Сохраняет новый бюджет пользователя в базе данных.
     *
     * @param budget объект {@link Budget}, содержащий Id пользователя и лимит бюджета.
     * @throws RuntimeException если произошла ошибка при сохранении в БД.
     */
    @Override
    public void save(Budget budget) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQL_INSERT_BUDGET)) {

            stmt.setLong(1, budget.getUserId());
            stmt.setDouble(2, budget.getLimit());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                log.warn("Не удалось сохранить бюджет: {}", budget);
                throw new SQLException("Не удалось вставить бюджет");
            }

            log.info("Бюджет сохранён для userId={}, limit={}", budget.getUserId(), budget.getLimit());

        } catch (SQLException e) {
            log.error("Ошибка при сохранении бюджета: {}", budget, e);
            throw new RuntimeException("Ошибка сохранения бюджета пользователя", e);
        }
    }

    /**
     * Ищет бюджет пользователя по его email.
     *
     * @param userId Id пользователя, для которого нужно найти бюджет.
     * @return {@link Optional} с объектом {@link Budget}, если бюджет найден, иначе пустой {@link Optional}.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public Optional<Budget> findByUserId(long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_BY_USERID)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Budget budget = mapToBudget(rs);
                    log.debug("Бюджет найден: {}", budget);
                    return Optional.of(budget);
                } else {
                    log.info("Бюджет не найден для userId={}", userId);
                    return Optional.empty(); // <-- Важно!
                }
            }

        } catch (SQLException e) {
            log.error("Ошибка при поиске бюджета по userId={}", userId, e);
            throw new RuntimeException("Ошибка поиска бюджета", e);
        }
    }

    private Budget mapToBudget(ResultSet rs) throws SQLException {
        return new Budget(
                rs.getLong("user_id"),
                rs.getDouble("budget_limit")
        );
    }
}
