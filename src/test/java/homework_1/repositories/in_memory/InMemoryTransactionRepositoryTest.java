package homework_1.repositories.in_memory;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class InMemoryTransactionRepositoryTest {

    private TransactionRepository repository;
    private String userEmail;
    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
        userEmail = "test@mail.com";

        transaction1 = new Transaction(1, userEmail, 1000, TransactionType.INCOME, Category.SALARY, LocalDate.of(2024, 3, 10), "Зарплата");
        transaction2 = new Transaction(2, userEmail, 200, TransactionType.EXPENSE, Category.FOOD, LocalDate.of(2024, 3, 15), "Покупка еды");
        transaction3 = new Transaction(3, userEmail, 300, TransactionType.EXPENSE, Category.TRANSPORT, LocalDate.of(2024, 3, 15), "Проезд");

        repository.save(transaction1);
        repository.save(transaction2);
        repository.save(transaction3);
    }

    @Test
    void findById_ShouldReturnTransaction() {
        Optional<Transaction> foundTransaction = repository.findById(userEmail, transaction1.getId());

        assertThat(foundTransaction).isPresent();
        assertThat(foundTransaction.get().getAmount()).isEqualTo(1000);
    }

    @Test
    void findById_ShouldReturnEmptyForNonexistentTransaction() {
        long randomId = 6;

        Optional<Transaction> foundTransaction = repository.findById(userEmail, randomId);

        assertThat(foundTransaction).isEmpty();
    }

    @Test
    void findByUserEmailAndDate_ShouldReturnTransactionsForDate() {
        List<Transaction> transactions = repository.findByUserEmailAndDate(userEmail, LocalDate.of(2024, 3, 15));

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getCategory).containsExactlyInAnyOrder(Category.FOOD, Category.TRANSPORT);
    }

    @Test
    void findByUserEmailAndCategory_ShouldReturnTransactionsOfCategory() {
        List<Transaction> transactions = repository.findByUserEmailAndCategory(userEmail, Category.FOOD);

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getAmount()).isEqualTo(200);
    }

    @Test
    void findByUserEmailAndType_ShouldReturnTransactionsOfType() {
        List<Transaction> incomeTransactions = repository.findByUserEmailAndType(userEmail, TransactionType.INCOME);

        assertThat(incomeTransactions).hasSize(1);
        assertThat(incomeTransactions.get(0).getAmount()).isEqualTo(1000);

        List<Transaction> expenseTransactions = repository.findByUserEmailAndType(userEmail, TransactionType.EXPENSE);
        assertThat(expenseTransactions).hasSize(2);
    }

    @Test
    void update_ShouldModifyTransaction() {
        transaction1.setAmount(1200);
        repository.update(transaction1);

        Optional<Transaction> updatedTransaction = repository.findById(userEmail, transaction1.getId());

        assertThat(updatedTransaction).isPresent();
        assertThat(updatedTransaction.get().getAmount()).isEqualTo(1200);
    }

    @Test
    void update_ShouldNotChangeIfTransactionNotExists() {
        Transaction nonExistentTransaction = new Transaction(7, userEmail, 500, TransactionType.EXPENSE, Category.OTHER, LocalDate.now(), "Новая транзакция");

        repository.update(nonExistentTransaction);

        Optional<Transaction> foundTransaction = repository.findById(userEmail, nonExistentTransaction.getId());
        assertThat(foundTransaction).isEmpty();
    }
}