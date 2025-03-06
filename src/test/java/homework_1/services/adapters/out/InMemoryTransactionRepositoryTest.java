package homework_1.services.adapters.out;

import homework_1.adapters.out.InMemoryTransactionRepository;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.domain.Category;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class InMemoryTransactionRepositoryTest {

    private final InMemoryTransactionRepository repository = new InMemoryTransactionRepository();

    @Test
    void saveTransaction_Success() {
        Transaction transaction = new Transaction(UUID.randomUUID(), 100,
                TransactionType.INCOME, Category.SALARY, LocalDate.now(), "Зарплата");

        repository.save(transaction);

        assertThat(repository.findByUserId(transaction.getUserId())).contains(transaction);
    }

    @Test
    void deleteTransaction_Success() {
        UUID userId = UUID.randomUUID();
        Transaction transaction = new Transaction(userId, 100,
                TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Покупка продуктов");
        repository.save(transaction);

        repository.delete(userId, transaction.getId());

        assertThat(repository.findById(userId, transaction.getId())).isEmpty();
    }
}