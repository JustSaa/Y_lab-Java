package homework_1.repositories.jdbc;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTransactionRepository implements TransactionRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcTransactionRepository.class);

    private static final String SQL_INSERT = """
        INSERT INTO finance.transactions (user_id, amount, type, category, date, description)
        VALUES (?, ?, ?, ?, ?, ?)
        RETURNING id
    """;

    private static final String SQL_FIND_BY_ID = """
        SELECT * FROM finance.transactions
        WHERE user_id = ? AND id = ?
    """;

    private static final String SQL_FIND_BY_USER = """
        SELECT * FROM finance.transactions
        WHERE user_id = ?
    """;

    private static final String SQL_UPDATE = """
        UPDATE finance.transactions
        SET amount = ?, type = ?, category = ?, date = ?, description = ?
        WHERE id = ?
    """;

    private static final String SQL_DELETE = """
        DELETE FROM finance.transactions
        WHERE user_id = ? AND id = ?
    """;

    private static final String SQL_FIND_BY_DATE = """
        SELECT * FROM finance.transactions
        WHERE user_id = ? AND date = ?
    """;

    private static final String SQL_FIND_BY_CATEGORY = """
        SELECT * FROM finance.transactions
        WHERE user_id = ? AND category = ?
    """;

    private static final String SQL_FIND_BY_TYPE = """
        SELECT * FROM finance.transactions
        WHERE user_id = ? AND type = ?
    """;

    private final DataSource dataSource;

    public JdbcTransactionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Transaction transaction) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setLong(1, transaction.getUserId());
            stmt.setDouble(2, transaction.getAmount());
            stmt.setString(3, transaction.getType().name());
            stmt.setString(4, transaction.getCategory().name());
            stmt.setDate(5, Date.valueOf(transaction.getDate()));
            stmt.setString(6, transaction.getDescription());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    transaction.setId(rs.getLong("id"));
                    log.info("Создана транзакция с ID={}", transaction.getId());
                }
            }

        } catch (SQLException e) {
            log.error("Ошибка при сохранении транзакции: {}", transaction, e);
            throw new RuntimeException("Ошибка сохранения транзакции", e);
        }
    }

    @Override
    public Optional<Transaction> findById(long userId, long transactionId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, transactionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Transaction transaction = mapTransaction(rs);
                    log.debug("Найдена транзакция: {}", transaction);
                    return Optional.of(transaction);
                }
            }

        } catch (SQLException e) {
            log.error("Ошибка при поиске транзакции userId={}, transactionId={}", userId, transactionId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findByUserId(long userId) {
        return queryList(SQL_FIND_BY_USER, stmt -> stmt.setLong(1, userId));
    }

    @Override
    public List<Transaction> findByUserIdAndDate(long userId, LocalDate date) {
        return queryList(SQL_FIND_BY_DATE, stmt -> {
            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(date));
        });
    }

    @Override
    public List<Transaction> findByUserIdAndCategory(long userId, Category category) {
        return queryList(SQL_FIND_BY_CATEGORY, stmt -> {
            stmt.setLong(1, userId);
            stmt.setString(2, category.name());
        });
    }

    @Override
    public List<Transaction> findByUserIdAndType(long userId, TransactionType type) {
        return queryList(SQL_FIND_BY_TYPE, stmt -> {
            stmt.setLong(1, userId);
            stmt.setString(2, type.name());
        });
    }

    @Override
    public void update(Transaction transaction) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setDouble(1, transaction.getAmount());
            stmt.setString(2, transaction.getType().name());
            stmt.setString(3, transaction.getCategory().name());
            stmt.setDate(4, Date.valueOf(transaction.getDate()));
            stmt.setString(5, transaction.getDescription());
            stmt.setLong(6, transaction.getId());

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                log.info("Обновлена транзакция с ID={}", transaction.getId());
            }

        } catch (SQLException e) {
            log.error("Ошибка обновления транзакции: {}", transaction, e);
            throw new RuntimeException("Ошибка обновления транзакции", e);
        }
    }

    @Override
    public void delete(long userId, long transactionId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, transactionId);
            int deleted = stmt.executeUpdate();

            if (deleted > 0) {
                log.info("Удалена транзакция с ID={} для userId={}", transactionId, userId);
            }

        } catch (SQLException e) {
            log.error("Ошибка удаления транзакции userId={}, transactionId={}", userId, transactionId, e);
            throw new RuntimeException("Ошибка удаления транзакции", e);
        }
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getDouble("amount"),
                TransactionType.valueOf(rs.getString("type")),
                Category.valueOf(rs.getString("category")),
                rs.getDate("date").toLocalDate(),
                rs.getString("description")
        );
    }

    private List<Transaction> queryList(String sql, StatementSetter setter) {
        List<Transaction> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setter.set(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapTransaction(rs));
                }
                log.debug("Найдено {} транзакций", result.size());
            }

        } catch (SQLException e) {
            log.error("Ошибка при выполнении запроса: {}", sql, e);
            throw new RuntimeException("Ошибка при получении списка транзакций", e);
        }
        return result;
    }

    @FunctionalInterface
    private interface StatementSetter {
        void set(PreparedStatement stmt) throws SQLException;
    }
}