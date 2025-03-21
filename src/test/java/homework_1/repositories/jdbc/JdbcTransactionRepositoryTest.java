package homework_1.repositories.jdbc;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcTransactionRepositoryTest extends AbstractTestContainerTest {

    private JdbcTransactionRepository transactionRepository;
    private long userId;

    @BeforeEach
    void setUp() {
        transactionRepository = new JdbcTransactionRepository(connection);

        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM finance.transactions")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM finance.users")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String userSql = """
            INSERT INTO finance.users (name, email, password, user_role, is_blocked) 
            VALUES (?, ?, ?, ?, ?) RETURNING id
            """;
        try (PreparedStatement stmt = connection.prepareStatement(userSql)) {
            stmt.setString(1, "Test User");
            stmt.setString(2, "test@example.com");
            stmt.setString(3, "password");
            stmt.setString(4, "USER");
            stmt.setBoolean(5, false);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании тестового пользователя", e);
        }
    }

    @Test
    void shouldSaveAndFindTransactionById() {
        Transaction transaction = new Transaction(1, userId, 1500.0, TransactionType.INCOME, Category.SALARY, LocalDate.now(), "Зарплата");
        transactionRepository.save(transaction);

        Optional<Transaction> foundTransaction = transactionRepository.findById(userId, transaction.getId());

        assertThat(foundTransaction).isPresent();
        assertThat(foundTransaction.get().getAmount()).isEqualTo(1500.0);
        assertThat(foundTransaction.get().getCategory()).isEqualTo(Category.SALARY);
    }

    @Test
    void shouldFindTransactionsByUserEmail() {
        transactionRepository.save(new Transaction(1, userId, 1000.0, TransactionType.INCOME, Category.SALARY, LocalDate.now(), "Зарплата"));
        transactionRepository.save(new Transaction(2, userId, 500.0, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Обед"));

        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getCategory).contains(Category.SALARY, Category.FOOD);
    }

    @Test
    void shouldFindTransactionsByCategory() {
        transactionRepository.save(new Transaction(1, userId, 200.0, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Завтрак"));
        transactionRepository.save(new Transaction(2, userId, 300.0, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Ужин"));

        List<Transaction> transactions = transactionRepository.findByUserIdAndCategory(userId, Category.FOOD);

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getAmount).contains(200.0, 300.0);
    }

    @Test
    void shouldFindTransactionsByType() {
        transactionRepository.save(new Transaction(1, userId, 500.0, TransactionType.INCOME, Category.SALARY, LocalDate.now(), "Бонус"));
        transactionRepository.save(new Transaction(2, userId, 100.0, TransactionType.EXPENSE, Category.ENTERTAINMENT, LocalDate.now(), "Кино"));

        List<Transaction> incomeTransactions = transactionRepository.findByUserIdAndType(userId, TransactionType.INCOME);

        assertThat(incomeTransactions).hasSize(1);
        assertThat(incomeTransactions.get(0).getCategory()).isEqualTo(Category.SALARY);
    }

    @Test
    void shouldUpdateTransaction() {
        Transaction transaction = new Transaction(1, userId, 500.0, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Ресторан");
        transactionRepository.save(transaction);

        // Проверяем, что транзакция действительно сохранилась
        Optional<Transaction> savedTransaction = transactionRepository.findById(userId, transaction.getId());
        assertThat(savedTransaction).isPresent();

        // Обновляем транзакцию
        transaction.setAmount(700.0);
        transaction.setCategory(Category.ENTERTAINMENT);
        transaction.setDescription("Изменено");
        transactionRepository.update(transaction);

        Optional<Transaction> updatedTransaction = transactionRepository.findById(userId, transaction.getId());

        assertThat(updatedTransaction).isPresent();
        assertThat(updatedTransaction.get().getAmount()).isEqualTo(700.0);
        assertThat(updatedTransaction.get().getCategory()).isEqualTo(Category.ENTERTAINMENT);
    }

    @Test
    void shouldDeleteTransaction() {
        Transaction transaction = new Transaction(1, userId, 300.0, TransactionType.EXPENSE, Category.TRANSPORT, LocalDate.now(), "Такси");
        transactionRepository.save(transaction);

        transactionRepository.delete(userId, transaction.getId());

        Optional<Transaction> deletedTransaction = transactionRepository.findById(userId, transaction.getId());
        assertThat(deletedTransaction).isEmpty();
    }
}