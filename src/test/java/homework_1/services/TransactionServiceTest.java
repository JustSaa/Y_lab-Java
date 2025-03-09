package homework_1.services;

import homework_1.domain.User;
import homework_1.repositories.NotificationService;
import homework_1.repositories.TransactionRepository;
import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;

import homework_1.repositories.UserRepository;
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
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransactionService transactionService;

    private String userEmail;
    private User user;
    private Transaction transactionIncome;
    private Transaction transactionExpense;

    @BeforeEach
    void setUp() {
        userEmail = "example@mail.ru";
        user = new User("Иван Иванов", userEmail, "password123");
        user.setMonthlyBudget(500);

        transactionIncome = new Transaction(
                userEmail, 1000.0, TransactionType.INCOME,
                Category.SALARY, LocalDate.now(), "Зарплата");

        transactionExpense = new Transaction(
                userEmail, 200.0, TransactionType.EXPENSE,
                Category.FOOD, LocalDate.now(), "Покупка продуктов");
    }

    @Test
    void createTransaction_ShouldCallRepositorySave() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        transactionService.createTransaction(transactionIncome);
        verify(transactionRepository, times(1)).save(transactionIncome);
    }

    @Test
    void getTransactions_ShouldReturnUserTransactions() {
        when(transactionRepository.findByUserEmail(userEmail))
                .thenReturn(List.of(transactionIncome, transactionExpense));

        List<Transaction> transactions = transactionService.getTransactions(userEmail);

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
        transactionService.deleteTransaction(userEmail, transactionId);
        verify(transactionRepository).delete(userEmail, transactionId);
    }

    @Test
    void calculateBalance_ShouldReturnCorrectBalance() {
        when(transactionRepository.findByUserEmail(userEmail))
                .thenReturn(List.of(transactionIncome, transactionExpense));

        double balance = transactionService.calculateBalance(userEmail);

        assertThat(balance).isEqualTo(800.0);
    }

    @Test
    void getTransactionsByDate_ShouldReturnFilteredTransactions() {
        when(transactionRepository.findByUserEmailAndDate(userEmail, LocalDate.now()))
                .thenReturn(List.of(transactionIncome));

        List<Transaction> transactions = transactionService.getTransactionsByDate(userEmail, LocalDate.now());

        assertThat(transactions).containsExactly(transactionIncome);
    }

    @Test
    void getTransactionsByCategory_ShouldReturnFilteredTransactions() {
        when(transactionRepository.findByUserEmailAndCategory(userEmail, Category.FOOD))
                .thenReturn(List.of(transactionExpense));

        List<Transaction> transactions = transactionService.getTransactionsByCategory(userEmail, Category.FOOD);

        assertThat(transactions).containsExactly(transactionExpense);
    }

    @Test
    void isBudgetExceeded_TrueWhenExceeded() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(transactionRepository.findByUserEmail(userEmail))
                .thenReturn(List.of(new Transaction(userEmail, 1000, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Еда")));

        user.setMonthlyBudget(500);

        assertThat(transactionService.isBudgetExceeded(userEmail)).isTrue();
    }

    @Test
    void createTransaction_ThrowsExceptionWhenBudgetExceeded() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(transactionRepository.findByUserEmail(userEmail))
                .thenReturn(List.of(new Transaction(userEmail, 1000, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Еда")));

        user.setMonthlyBudget(500);

        Transaction transaction = new Transaction(userEmail, 200, TransactionType.EXPENSE, Category.TRANSPORT, LocalDate.now(), "Такси");

        assertThatThrownBy(() -> transactionService.createTransaction(transaction))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("расходы превышают установленный бюджет");

        verify(notificationService, times(1))
                .sendNotification(eq(userEmail), contains("превысили установленный бюджет"));
    }
}