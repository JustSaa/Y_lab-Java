package homework_1.repositories.jdbc;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.repositories.TransactionRepository;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class JdbcTransactionRepository implements TransactionRepository {

    private final Connection connection;

    public JdbcTransactionRepository(Connection connection) {
        this.connection = connection;
    }

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