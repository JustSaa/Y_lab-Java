package homework_1.repositories.jdbc;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


/**
 * Реализация {@link TransactionRepository} для работы с финансовыми транзакциями
 * с использованием JDBC и базы данных PostgreSQL.
 */
public class JdbcTransactionRepository implements TransactionRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcTransactionRepository.class);
    private static final String SAVE = """
            INSERT INTO finance.transactions (user_id, amount, type, category, date, description)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
            """;
    private static final String FIND_BY_ID = """
            SELECT id, user_id, amount, type, category, date, description
            FROM finance.transactions WHERE user_id = ? AND id = ?
            """;
    private static final String FIND_BY_USERID = "SELECT * FROM finance.transactions WHERE user_id = ?";
    private static final String UPDATE = """
            UPDATE finance.transactions
            SET amount = ?, type = ?, category = ?, date = ?, description = ?
            WHERE id = ?
            """;
    private static final String DELETE = "DELETE FROM finance.transactions WHERE user_id = ? AND id = ?";
    private static final String FIND_BY_USERID_AND_DATE = """
                SELECT * FROM finance.transactions WHERE user_id = ? AND date = ?
            """;
    private static final String FIND_BY_USERID_AND_CATEGORY = """
            SELECT * FROM finance.transactions
            WHERE user_id = ? AND category = ?
            """;
    private static final String FIND_BY_USERID_AND_TYPE = """
            SELECT * FROM finance.transactions
            WHERE user_id = ? AND type = ?
            """;

    private final Connection connection;

    /**
     * Конструктор репозитория транзакций.
     *
     * @param connection объект {@link Connection} для работы с базой данных.
     */
    public JdbcTransactionRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Сохраняет новую транзакцию в базе данных.
     *
     * @param transaction объект {@link Transaction}, содержащий данные о транзакции.
     * @throws RuntimeException если произошла ошибка при сохранении в БД.
     */
    @Override
    public void save(Transaction transaction) {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(SAVE)) {
                stmt.setLong(1, transaction.getUserId());
                stmt.setDouble(2, transaction.getAmount());
                stmt.setString(3, transaction.getType().name());
                stmt.setString(4, transaction.getCategory().name());
                stmt.setDate(5, Date.valueOf(transaction.getDate()));
                stmt.setString(6, transaction.getDescription());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        transaction.setId(rs.getLong("id"));
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                logger.error("Ошибка сохранения транзакции. Транзакция откатилась.", e);
                throw new RuntimeException("Ошибка сохранения транзакции", e);
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Ошибка при откате транзакции", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Ошибка при включении autoCommit", e);
            }
        }
    }

    /**
     * Ищет транзакцию по идентификатору и Id пользователя.
     *
     * @param userId        Id пользователя.
     * @param transactionId уникальный идентификатор транзакции.
     * @return {@link Optional} с объектом {@link Transaction}, если транзакция найдена, иначе пустой {@link Optional}.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public Optional<Transaction> findById(long userId, long transactionId) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, transactionId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapTransaction(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска транзакции по ID", e);
        }
    }

    /**
     * Возвращает список всех транзакций пользователя.
     *
     * @param userId Id пользователя.
     * @return список транзакций.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public List<Transaction> findByUserId(long userId) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_USERID)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                transactions.add(mapTransaction(rs));
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска транзакций пользователя", e);
        }
    }

    /**
     * Обновляет данные транзакции.
     *
     * @param transaction объект {@link Transaction} с обновленными значениями.
     * @throws RuntimeException если произошла ошибка при обновлении в БД.
     */
    @Override
    public void update(Transaction transaction) {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(UPDATE)) {
                stmt.setDouble(1, transaction.getAmount());
                stmt.setString(2, transaction.getType().name());
                stmt.setString(3, transaction.getCategory().name());
                stmt.setDate(4, Date.valueOf(transaction.getDate()));
                stmt.setString(5, transaction.getDescription());
                stmt.setLong(6, transaction.getId());

                stmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                logger.error("Ошибка обновления транзакции. Транзакция откатилась.", e);
                throw new RuntimeException("Ошибка обновления транзакции", e);
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Ошибка при откате транзакции", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Ошибка при включении autoCommit", e);
            }
        }
    }

    /**
     * Удаляет транзакцию по идентификатору и email пользователя.
     *
     * @param userId        Id пользователя.
     * @param transactionId уникальный идентификатор транзакции.
     * @throws RuntimeException если произошла ошибка при удалении из БД.
     */
    @Override
    public void delete(long userId, long transactionId) {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(DELETE)) {
                stmt.setLong(1, userId);
                stmt.setLong(2, transactionId);
                stmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                logger.error("Ошибка удаления транзакции. Транзакция откатилась.", e);
                throw new RuntimeException("Ошибка удаления транзакции", e);
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Ошибка при откате транзакции", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Ошибка при включении autoCommit", e);
            }
        }
    }

    /**
     * Возвращает список транзакций пользователя за указанную дату.
     *
     * @param userId Id пользователя.
     * @param date   дата транзакции.
     * @return список транзакций.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public List<Transaction> findByUserIdAndDate(long userId, LocalDate date) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_USERID_AND_DATE)) {
            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                transactions.add(mapTransaction(rs));
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска транзакций по дате", e);
        }
    }

    /**
     * Возвращает список транзакций пользователя по указанной категории.
     *
     * @param userId   Id пользователя.
     * @param category категория транзакции.
     * @return список транзакций.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public List<Transaction> findByUserIdAndCategory(long userId, Category category) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_USERID_AND_CATEGORY)) {
            stmt.setLong(1, userId);
            stmt.setString(2, category.name());
            ResultSet rs = stmt.executeQuery();

            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                transactions.add(mapTransaction(rs));
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения транзакций по категории", e);
        }
    }

    /**
     * Возвращает список транзакций пользователя по указанному типу (доход или расход).
     *
     * @param userId Id пользователя.
     * @param type   тип транзакции.
     * @return список транзакций.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public List<Transaction> findByUserIdAndType(long userId, TransactionType type) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_USERID_AND_TYPE)) {
            stmt.setLong(1, userId);
            stmt.setString(2, type.name());
            ResultSet rs = stmt.executeQuery();

            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                transactions.add(mapTransaction(rs));
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска транзакций по типу", e);
        }
    }

    /**
     * Метод для маппинга ResultSet на сущность Transaction.
     */
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
}