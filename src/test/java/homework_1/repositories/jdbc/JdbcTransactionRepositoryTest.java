package homework_1.repositories.jdbc;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcTransactionRepositoryTest extends AbstractTestContainerTest {

    private JdbcTransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository = new JdbcTransactionRepository(connection);
    }

    @Test
    void shouldSaveAndFindTransactionById() {
        String userEmail = "user@mail.com";
        Transaction transaction = new Transaction(1, userEmail, 1500.0, TransactionType.INCOME, Category.SALARY, LocalDate.now(), "Зарплата");
        transactionRepository.save(transaction);

        Optional<Transaction> foundTransaction = transactionRepository.findById(userEmail, transaction.getId());

        assertThat(foundTransaction).isPresent();
        assertThat(foundTransaction.get().getAmount()).isEqualTo(1500.0);
        assertThat(foundTransaction.get().getCategory()).isEqualTo(Category.SALARY);
    }

    @Test
    void shouldFindTransactionsByUserEmail() {
        String userEmail = "userT@mail.com";
        transactionRepository.save(new Transaction(1, userEmail, 1000.0, TransactionType.INCOME, Category.SALARY, LocalDate.now(), "Зарплата"));
        transactionRepository.save(new Transaction(2, userEmail, 500.0, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Обед"));

        List<Transaction> transactions = transactionRepository.findByUserEmail(userEmail);

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getCategory).contains(Category.SALARY, Category.FOOD);
    }

    @Test
    void shouldFindTransactionsByCategory() {
        String userEmail = "userTest@mail.com";
        transactionRepository.save(new Transaction(1, userEmail, 200.0, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Завтрак"));
        transactionRepository.save(new Transaction(2, userEmail, 300.0, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Ужин"));

        List<Transaction> transactions = transactionRepository.findByUserEmailAndCategory(userEmail, Category.FOOD);

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getAmount).contains(200.0, 300.0);
    }

    @Test
    void shouldFindTransactionsByType() {
        String userEmail = "userTest@mail.com";
        transactionRepository.save(new Transaction(1, userEmail, 500.0, TransactionType.INCOME, Category.SALARY, LocalDate.now(), "Бонус"));
        transactionRepository.save(new Transaction(2, userEmail, 100.0, TransactionType.EXPENSE, Category.ENTERTAINMENT, LocalDate.now(), "Кино"));

        List<Transaction> incomeTransactions = transactionRepository.findByUserEmailAndType(userEmail, TransactionType.INCOME);

        assertThat(incomeTransactions).hasSize(1);
        assertThat(incomeTransactions.get(0).getCategory()).isEqualTo(Category.SALARY);
    }

    @Test
    void shouldUpdateTransaction() {
        String userEmail = "user@mail.com";
        Transaction transaction = new Transaction(1, userEmail, 500.0, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Ресторан");
        transactionRepository.save(transaction);

        transaction.setAmount(700.0);
        transaction.setCategory(Category.ENTERTAINMENT);
        transaction.setDescription("Изменено");
        transactionRepository.update(transaction);

        Optional<Transaction> updatedTransaction = transactionRepository.findById(userEmail, transaction.getId());

        assertThat(updatedTransaction).isPresent();
        assertThat(updatedTransaction.get().getAmount()).isEqualTo(700.0);
        assertThat(updatedTransaction.get().getCategory()).isEqualTo(Category.ENTERTAINMENT);
    }

    @Test
    void shouldDeleteTransaction() {
        String userEmail = "user@mail.com";
        Transaction transaction = new Transaction(1, userEmail, 300.0, TransactionType.EXPENSE, Category.TRANSPORT, LocalDate.now(), "Такси");
        transactionRepository.save(transaction);

        transactionRepository.delete(userEmail, transaction.getId());

        Optional<Transaction> deletedTransaction = transactionRepository.findById(userEmail, transaction.getId());
        assertThat(deletedTransaction).isEmpty();
    }
}