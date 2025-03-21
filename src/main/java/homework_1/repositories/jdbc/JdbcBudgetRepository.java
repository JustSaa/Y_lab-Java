package homework_1.repositories.jdbc;

import homework_1.domain.Budget;
import homework_1.repositories.BudgetRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Реализация репозитория {@link BudgetRepository} для работы с бюджетами пользователей
 * с использованием JDBC и базы данных PostgreSQL.
 */
public class JdbcBudgetRepository implements BudgetRepository {
    private static final String INSERT_BUDGET = "INSERT INTO finance.budgets (id, user_id, budget_limit) VALUES (nextval('finance.budgets_seq'), ?, ?)";
    private static final String SELECT_BY_USERID = "SELECT * FROM finance.budgets WHERE user_id = ?";
    private final Connection connection;

    /**
     * Конструктор для создания репозитория бюджета.
     *
     * @param connection объект {@link Connection} для работы с базой данных.
     */
    public JdbcBudgetRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Сохраняет новый бюджет пользователя в базе данных.
     *
     * @param budget объект {@link Budget}, содержащий Id пользователя и лимит бюджета.
     * @throws RuntimeException если произошла ошибка при сохранении в БД.
     */
    @Override
    public void save(Budget budget) {
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement(INSERT_BUDGET)) {
                stmt.setLong(1, budget.getUserId());
                stmt.setDouble(2, budget.getLimit());
                stmt.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                throw new RuntimeException("Ошибка сохранения бюджета пользователя. Транзакция откатилась.", e);
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Ошибка при откате транзакции", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка при включении autoCommit", e);
            }
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
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_USERID)) {
            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Budget budget = new Budget(
                            rs.getLong("user_id"),
                            rs.getDouble("budget_limit")
                    );
                    return Optional.of(budget);
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска бюджета пользователя по email", e);
        }
    }
}
