package homework_1.services;

import homework_1.domain.*;
import homework_1.repositories.TransactionRepository;

import homework_1.repositories.UserRepository;
import homework_1.services.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private TransactionServiceImpl transactionServiceImpl;

    private String userEmail;
    private User user;
    private Transaction transactionIncome;
    private Transaction transactionExpense;

    @BeforeEach
    void setUp() {
        userEmail = "example@mail.ru";
        user = new User("Иван Иванов", userEmail, "password123", UserRole.USER);

        transactionIncome = new Transaction(1,
                user.getId(), 1000.0, TransactionType.INCOME,
                Category.SALARY, LocalDate.now(), "Зарплата");

        transactionExpense = new Transaction(2,
                user.getId(), 200.0, TransactionType.EXPENSE,
                Category.FOOD, LocalDate.now(), "Покупка продуктов");
    }

    @Test
    void createTransaction_ShouldSaveTransaction_WhenBudgetNotExceeded() {
        when(budgetService.getUserBudget(user.getId())).thenReturn(Optional.of(new Budget(user.getId(), 500.0)));

        when(transactionRepository.findByUserIdAndType(user.getId(), TransactionType.EXPENSE)).thenReturn(List.of());

        transactionServiceImpl.createTransaction(transactionExpense);

        verify(transactionRepository, times(1)).save(transactionExpense);

        verify(notificationService, never()).sendNotification(anyLong(), anyString());
    }

    @Test
    void getTransactions_ShouldReturnUserTransactions() {
        when(transactionRepository.findByUserId(user.getId()))
                .thenReturn(List.of(transactionIncome, transactionExpense));

        List<Transaction> transactions = transactionServiceImpl.getTransactions(user.getId());

        assertThat(transactions).hasSize(2);
        assertThat(transactions).containsExactly(transactionIncome, transactionExpense);
    }

    @Test
    void updateTransaction_ShouldCallRepositoryUpdate() {
        transactionServiceImpl.updateTransaction(transactionIncome);
        verify(transactionRepository).update(transactionIncome);
    }

    @Test
    void deleteTransaction_ShouldCallRepositoryDelete() {
        long transactionId = transactionExpense.getId();
        transactionServiceImpl.deleteTransaction(user.getId(), transactionId);
        verify(transactionRepository).delete(user.getId(), transactionId);
    }

    @Test
    void calculateBalance_ShouldReturnCorrectBalance() {
        when(transactionRepository.findByUserId(user.getId()))
                .thenReturn(List.of(transactionIncome, transactionExpense));

        double balance = transactionServiceImpl.calculateBalance(user.getId());

        assertThat(balance).isEqualTo(800.0);
    }

    @Test
    void getTransactionsByDate_ShouldReturnFilteredTransactions() {
        when(transactionRepository.findByUserIdAndDate(user.getId(), LocalDate.now()))
                .thenReturn(List.of(transactionIncome));

        List<Transaction> transactions = transactionServiceImpl.getTransactionsByDate(user.getId(), LocalDate.now());

        assertThat(transactions).containsExactly(transactionIncome);
    }

    @Test
    void getTransactionsByCategory_ShouldReturnFilteredTransactions() {
        when(transactionRepository.findByUserIdAndCategory(user.getId(), Category.FOOD))
                .thenReturn(List.of(transactionExpense));

        List<Transaction> transactions = transactionServiceImpl.getTransactionsByCategory(user.getId(), Category.FOOD);

        assertThat(transactions).containsExactly(transactionExpense);
    }
}