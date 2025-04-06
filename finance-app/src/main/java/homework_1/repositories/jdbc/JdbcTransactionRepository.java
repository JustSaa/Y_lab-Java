package homework_1.repositories.jdbc;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
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

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Transaction> transactionMapper = (rs, rowNum) -> new Transaction(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getDouble("amount"),
            TransactionType.valueOf(rs.getString("type")),
            Category.valueOf(rs.getString("category")),
            rs.getDate("date").toLocalDate(),
            rs.getString("description")
    );

    @Override
    public void save(Transaction transaction) {
        Long id = jdbcTemplate.queryForObject(SQL_INSERT, new Object[]{
                transaction.getUserId(),
                transaction.getAmount(),
                transaction.getType().name(),
                transaction.getCategory().name(),
                Date.valueOf(transaction.getDate()),
                transaction.getDescription()
        }, Long.class);
        transaction.setId(id);
        log.info("Создана транзакция с ID={}", id);
    }

    @Override
    public Optional<Transaction> findById(long userId, long transactionId) {
        return jdbcTemplate.query(SQL_FIND_BY_ID, transactionMapper, userId, transactionId)
                .stream().findFirst();
    }

    @Override
    public List<Transaction> findByUserId(long userId) {
        return jdbcTemplate.query(SQL_FIND_BY_USER, transactionMapper, userId);
    }

    @Override
    public List<Transaction> findByUserIdAndDate(long userId, LocalDate date) {
        return jdbcTemplate.query(SQL_FIND_BY_DATE, transactionMapper, userId, Date.valueOf(date));
    }

    @Override
    public List<Transaction> findByUserIdAndCategory(long userId, Category category) {
        return jdbcTemplate.query(SQL_FIND_BY_CATEGORY, transactionMapper, userId, category.name());
    }

    @Override
    public List<Transaction> findByUserIdAndType(long userId, TransactionType type) {
        return jdbcTemplate.query(SQL_FIND_BY_TYPE, transactionMapper, userId, type.name());
    }

    @Override
    public void update(Transaction transaction) {
        int rows = jdbcTemplate.update(SQL_UPDATE,
                transaction.getAmount(),
                transaction.getType().name(),
                transaction.getCategory().name(),
                Date.valueOf(transaction.getDate()),
                transaction.getDescription(),
                transaction.getId()
        );
        if (rows > 0) {
            log.info("Обновлена транзакция с ID={}", transaction.getId());
        }
    }

    @Override
    public void delete(long userId, long transactionId) {
        int rows = jdbcTemplate.update(SQL_DELETE, userId, transactionId);
        if (rows > 0) {
            log.info("Удалена транзакция с ID={} для userId={}", transactionId, userId);
        }
    }
}