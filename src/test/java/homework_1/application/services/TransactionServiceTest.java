package homework_1.application.services;

import homework_1.application.ports.out.TransactionRepository;
import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private UUID userId;
    private Transaction transactionIncome;
    private Transaction transactionExpense;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        transactionIncome = new Transaction(
                userId, 1000.0, TransactionType.INCOME,
                Category.SALARY, LocalDate.now(), "Зарплата");

        transactionExpense = new Transaction(
                userId, 200.0, TransactionType.EXPENSE,
                Category.FOOD, LocalDate.now(), "Покупка продуктов");
    }

    @Test
    void createTransaction_ShouldCallRepositorySave() {
        transactionService.createTransaction(transactionIncome);
        verify(transactionRepository, times(1)).save(transactionIncome);
    }

    @Test
    void getTransactions_ShouldReturnUserTransactions() {
        when(transactionRepository.findByUserId(userId))
                .thenReturn(List.of(transactionIncome, transactionExpense));

        List<Transaction> transactions = transactionService.getTransactions(userId);

        assertThat(transactions).hasSize(2);
        assertThat(transactions).containsExactly(transactionIncome, transactionExpense);
    }

    @Test
    void updateTransaction_ShouldCallRepositoryUpdate() {
        transactionService.updateTransaction(transactionIncome);
        verify(transactionRepository).update(transactionIncome);
    }

    @Test
    void deleteTransaction_ShouldCallRepositoryDelete() {
        UUID transactionId = transactionExpense.getId();
        transactionService.deleteTransaction(userId, transactionId);
        verify(transactionRepository).delete(userId, transactionId);
    }

    @Test
    void calculateBalance_ShouldReturnCorrectBalance() {
        when(transactionRepository.findByUserId(userId))
                .thenReturn(List.of(transactionIncome, transactionExpense));

        double balance = transactionService.calculateBalance(userId);

        assertThat(balance).isEqualTo(800.0);
    }
}