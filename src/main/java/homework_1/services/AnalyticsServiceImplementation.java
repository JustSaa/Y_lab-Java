package homework_1.services;

import homework_1.domain.Transaction;
import homework_1.domain.TransactionType;
import homework_1.domain.Category;
import homework_1.repositories.TransactionRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация сервиса аналитики финансов.
 */
public class AnalyticsServiceImplementation implements AnalyticsService {

    private final TransactionRepository transactionRepository;

    public AnalyticsServiceImplementation(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public double getTotalIncome(String userEmail, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        return transactionRepository.findByUserEmail(userEmail).stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    @Override
    public double getTotalExpenses(String userEmail, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        return transactionRepository.findByUserEmail(userEmail).stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    @Override
    public String analyzeExpensesByCategory(String userEmail) {
        Map<Category, Double> expensesByCategory = transactionRepository.findByUserEmail(userEmail).stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));

        StringBuilder report = new StringBuilder("Анализ расходов по категориям:\n");
        expensesByCategory.forEach((category, amount) -> report.append(category).append(": ").append(amount).append("\n"));
        return report.toString();
    }

    @Override
    public String generateFinancialReport(String userEmail) {
        double totalIncome = getTotalIncome(userEmail, "2000-01-01", LocalDate.now().toString());
        double totalExpenses = getTotalExpenses(userEmail, "2000-01-01", LocalDate.now().toString());
        double balance = totalIncome - totalExpenses;
        String categoryReport = analyzeExpensesByCategory(userEmail);

        return "Финансовый отчёт для " + userEmail + ":\n" +
                "Общий доход: " + totalIncome + "\n" +
                "Общий расход: " + totalExpenses + "\n" +
                "Текущий баланс: " + balance + "\n\n" +
                categoryReport;
    }
}