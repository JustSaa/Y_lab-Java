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

    private final long userId = 0;

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
        notificationService.sendNotification(userId, "Тестовое уведомление.");
        verify(notificationService, times(1)).sendNotification(eq(userId), eq("Тестовое уведомление."));
    }

    @Test
    void sendNotification_WhenBudgetExceeded_ShouldBeTriggered() {
        long userId = 0;

        when(budgetService.getUserBudget(userId)).thenReturn(Optional.of(new Budget(userId, 500)));

        when(transactionRepository.findByUserIdAndType(0, TransactionType.EXPENSE))
                .thenReturn(List.of(
                        new Transaction(1L, userId, 600, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Еда")
                ));

        Transaction transaction = new Transaction(2, userId, 200, TransactionType.EXPENSE, Category.TRANSPORT, LocalDate.now(), "Такси");

        transactionServiceImpl.createTransaction(transaction);

        verify(notificationService, times(1))
                .sendNotification(eq(userId), contains("превысили установленный бюджет"));

        verify(transactionRepository, never()).save(transaction);
    }

    @Test
    void sendNotification_WhenBudgetNotExceeded_ShouldNotBeTriggered() {
        User user = new User("Тест", "Тест@mail.ru", "password", UserRole.USER);
        Budget budget = new Budget(user.getId(), 500);

        when(budgetService.getUserBudget(user.getId())).thenReturn(Optional.of(budget));

        when(transactionRepository.findByUserId(user.getId()))
                .thenReturn(List.of(new Transaction(1, user.getId(), 600, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Еда")));

        Transaction transaction = new Transaction(2, user.getId(), 200, TransactionType.EXPENSE, Category.TRANSPORT, LocalDate.now(), "Такси");

        transactionServiceImpl.createTransaction(transaction);

        verify(notificationService, never())
                .sendNotification(anyLong(), anyString());
    }
}