package homework_1.services;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.domain.User;
import homework_1.repositories.NotificationService;
import homework_1.repositories.TransactionRepository;
import homework_1.repositories.UserRepository;
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
    private TransactionService transactionService;

    private final String testEmail = "test@mail.com";

    @BeforeEach
    void setUp() {
        notificationService = Mockito.mock(NotificationService.class);
        transactionRepository = Mockito.mock(TransactionRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        transactionService = new TransactionService(transactionRepository, userRepository, notificationService);
    }

    @Test
    void sendNotification_ShouldBeCalled() {
        notificationService.sendNotification(testEmail, "Тестовое уведомление.");
        verify(notificationService, times(1)).sendNotification(eq(testEmail), eq("Тестовое уведомление."));
    }

    @Test
    void sendNotification_WhenBudgetExceeded_ShouldBeTriggered() {
        User user = new User("Тест", testEmail, "password");
        user.setMonthlyBudget(500);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
        when(transactionRepository.findByUserEmail(testEmail))
                .thenReturn(List.of(new Transaction(testEmail, 600, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Еда")));

        Transaction transaction = new Transaction(testEmail, 200, TransactionType.EXPENSE, Category.TRANSPORT, LocalDate.now(), "Такси");

        assertThatThrownBy(() -> transactionService.createTransaction(transaction))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ваши расходы превышают установленный бюджет!");

        verify(notificationService, times(1))
                .sendNotification(eq(testEmail), contains("превысили установленный бюджет"));
    }

    @Test
    void sendNotification_WhenBudgetNotExceeded_ShouldNotBeTriggered() {
        User user = new User("Тест", testEmail, "password");
        user.setMonthlyBudget(2000);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));

        when(transactionRepository.findByUserEmail(testEmail))
                .thenReturn(List.of(new Transaction(testEmail, 600, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Еда")));

        Transaction transaction = new Transaction(testEmail, 200, TransactionType.EXPENSE, Category.TRANSPORT, LocalDate.now(), "Такси");

        transactionService.createTransaction(transaction);

        verify(notificationService, never())
                .sendNotification(anyString(), anyString());
    }
}