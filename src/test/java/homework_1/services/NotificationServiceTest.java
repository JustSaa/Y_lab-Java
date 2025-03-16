package homework_1.services;

import homework_1.domain.*;
import homework_1.repositories.TransactionRepository;
import homework_1.repositories.UserRepository;
import homework_1.services.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationService notificationService;
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private TransactionServiceImpl transactionServiceImpl;
    private BudgetService budgetService;

    private final String testEmail = "test@mail.com";

    @BeforeEach
    void setUp() {
        notificationService = Mockito.mock(NotificationService.class);
        transactionRepository = Mockito.mock(TransactionRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        budgetService = Mockito.mock(BudgetService.class);
        transactionServiceImpl = new TransactionServiceImpl(transactionRepository, budgetService, notificationService);
    }

    @Test
    void sendNotification_ShouldBeCalled() {
        notificationService.sendNotification(testEmail, "Тестовое уведомление.");
        verify(notificationService, times(1)).sendNotification(eq(testEmail), eq("Тестовое уведомление."));
    }

    @Test
    void sendNotification_WhenBudgetExceeded_ShouldBeTriggered() {
        String testEmail = "user@example.com";

        when(budgetService.getUserBudget(testEmail)).thenReturn(Optional.of(new Budget(testEmail, 500)));

        when(transactionRepository.findByUserEmailAndType(testEmail, TransactionType.EXPENSE))
                .thenReturn(List.of(
                        new Transaction(1L, testEmail, 600, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Еда")
                ));

        Transaction transaction = new Transaction(2, testEmail, 200, TransactionType.EXPENSE, Category.TRANSPORT, LocalDate.now(), "Такси");

        transactionServiceImpl.createTransaction(transaction);

        verify(notificationService, times(1))
                .sendNotification(eq(testEmail), contains("превысили установленный бюджет"));

        verify(transactionRepository, never()).save(transaction);
    }

    @Test
    void sendNotification_WhenBudgetNotExceeded_ShouldNotBeTriggered() {
        User user = new User("Тест", testEmail, "password", false);
        Budget budget = new Budget(testEmail, 500);

        when(budgetService.getUserBudget(testEmail)).thenReturn(Optional.of(budget));

        when(transactionRepository.findByUserEmail(testEmail))
                .thenReturn(List.of(new Transaction(1, testEmail, 600, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Еда")));

        Transaction transaction = new Transaction(2, testEmail, 200, TransactionType.EXPENSE, Category.TRANSPORT, LocalDate.now(), "Такси");

        transactionServiceImpl.createTransaction(transaction);

        verify(notificationService, never())
                .sendNotification(anyString(), anyString());
    }
}