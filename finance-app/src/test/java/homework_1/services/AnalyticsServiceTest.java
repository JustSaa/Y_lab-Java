package homework_1.services;

import homework_1.domain.Category;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.repositories.TransactionRepository;
import homework_1.services.impl.AnalyticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class AnalyticsServiceTest {

    private AnalyticsService analyticsService;
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        analyticsService = new AnalyticsServiceImpl(transactionRepository);
    }

    @Test
    void testAnalyzeExpensesByCategory() {
        long userId = 0;
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(
                new Transaction(1, userId, 300, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Обед"),
                new Transaction(2, userId, 200, TransactionType.EXPENSE, Category.TRANSPORT, LocalDate.now(), "Такси"),
                new Transaction(3, userId, 500, TransactionType.EXPENSE, Category.FOOD, LocalDate.now(), "Ужин")
        ));

        String report = analyticsService.analyzeExpensesByCategory(userId);
        assertThat(report).contains("FOOD: 800", "TRANSPORT: 200");
    }
}