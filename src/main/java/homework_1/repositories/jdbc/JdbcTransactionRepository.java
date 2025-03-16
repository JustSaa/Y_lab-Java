package homework_1.repositories.jdbc;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.repositories.TransactionRepository;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


/**
 * Реализация {@link TransactionRepository} для работы с финансовыми транзакциями
 * с использованием JDBC и базы данных PostgreSQL.
 */
public class JdbcTransactionRepository implements TransactionRepository {

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
        String sql = """
                INSERT INTO finance.transactions (id, user_email, amount, type, category, date, description)
                VALUES (nextval('finance.transactions_seq'), ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, transaction.getUserEmail());
            stmt.setDouble(2, transaction.getAmount());
            stmt.setString(3, transaction.getType().name());
            stmt.setString(4, transaction.getCategory().name());
            stmt.setDate(5, Date.valueOf(transaction.getDate()));
            stmt.setString(6, transaction.getDescription());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения транзакции", e);
        }
    }

    /**
     * Ищет транзакцию по идентификатору и email пользователя.
     *
     * @param userEmail    email пользователя.
     * @param transactionId уникальный идентификатор транзакции.
     * @return {@link Optional} с объектом {@link Transaction}, если транзакция найдена, иначе пустой {@link Optional}.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public Optional<Transaction> findById(String userEmail, long transactionId) {
        String sql = """
                SELECT id, user_email, amount, type, category, date, description
                FROM finance.transactions WHERE user_email = ? AND id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
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
     * @param userEmail email пользователя.
     * @return список транзакций.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public List<Transaction> findByUserEmail(String userEmail) {
        String sql = "SELECT * FROM finance.transactions WHERE user_email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
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
        String sql = """
                UPDATE finance.transactions
                SET amount = ?, type = ?, category = ?, date = ?, description = ?
                WHERE id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, transaction.getAmount());
            stmt.setString(2, transaction.getType().name());
            stmt.setString(3, transaction.getCategory().name());
            stmt.setDate(4, Date.valueOf(transaction.getDate()));
            stmt.setString(5, transaction.getDescription());
            stmt.setLong(6, transaction.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления транзакции", e);
        }
    }

    /**
     * Удаляет транзакцию по идентификатору и email пользователя.
     *
     * @param userEmail    email пользователя.
     * @param transactionId уникальный идентификатор транзакции.
     * @throws RuntimeException если произошла ошибка при удалении из БД.
     */
    @Override
    public void delete(String userEmail, long transactionId) {
        String sql = "DELETE FROM finance.transactions WHERE user_email = ? AND id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
            stmt.setLong(2, transactionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления транзакции", e);
        }
    }

    /**
     * Возвращает список транзакций пользователя за указанную дату.
     *
     * @param email email пользователя.
     * @param date  дата транзакции.
     * @return список транзакций.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public List<Transaction> findByUserEmailAndDate(String email, LocalDate date) {
        String sql = """
                    SELECT * FROM finance.transactions WHERE user_email = ? AND date = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
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
     * @param email   email пользователя.
     * @param category категория транзакции.
     * @return список транзакций.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public List<Transaction> findByUserEmailAndCategory(String email, Category category) {
        String sql = """
                SELECT * FROM finance.transactions
                WHERE user_email = ? AND category = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
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
     * @param email email пользователя.
     * @param type  тип транзакции.
     * @return список транзакций.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public List<Transaction> findByUserEmailAndType(String email, TransactionType type) {
        String sql = """
                SELECT * FROM finance.transactions
                WHERE user_email = ? AND type = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
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
                rs.getString("user_email"),
                rs.getDouble("amount"),
                TransactionType.valueOf(rs.getString("type")),
                Category.valueOf(rs.getString("category")),
                rs.getDate("date").toLocalDate(),
                rs.getString("description")
        );
    }
}