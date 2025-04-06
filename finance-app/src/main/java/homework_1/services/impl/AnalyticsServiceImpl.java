package homework_1.services.impl;

import audit.aspect.annotation.Audit;
import audit.aspect.annotation.LogExecutionTime;
import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.domain.Category;
import homework_1.repositories.TransactionRepository;
import homework_1.services.AnalyticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация сервиса аналитики финансов.
 */
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final TransactionRepository transactionRepository;

    public AnalyticsServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Audit(action = "Получить суммарный доход")
    @LogExecutionTime
    @Override
    public double getTotalIncome(long userId, LocalDate start, LocalDate end) {

        return transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    @Audit(action = "Получить суммарный расход")
    @LogExecutionTime
    @Override
    public double getTotalExpenses(long userId, LocalDate start, LocalDate end) {

        return transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    @Audit(action = "Анализ расходов по категориям")
    @LogExecutionTime
    @Override
    public String analyzeExpensesByCategory(long userId) {
        Map<Category, Double> expensesByCategory = transactionRepository.findByUserId(userId).stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));

        StringBuilder report = new StringBuilder("Анализ расходов по категориям:\n");
        expensesByCategory.forEach((category, amount) -> report.append(category).append(": ").append(amount).append("\n"));
        return report.toString();
    }

    @Audit(action = "Финансовый отчёт для пользователя")
    @LogExecutionTime
    @Override
    public String generateFinancialReport(long userId) {
        double totalIncome = getTotalIncome(userId, LocalDate.of(2000, 1, 1), LocalDate.now());
        double totalExpenses = getTotalExpenses(userId, LocalDate.of(2000, 1, 1), LocalDate.now());
        double balance = totalIncome - totalExpenses;
        String categoryReport = analyzeExpensesByCategory(userId);

        System.out.println("categoryReport = " + categoryReport);
        return "Финансовый отчёт для пользователя с Id" + userId + ":\n" +
                "Общий доход: " + totalIncome + "\n" +
                "Общий расход: " + totalExpenses + "\n" +
                "Текущий баланс: " + balance + "\n\n" +
                categoryReport;
    }
}