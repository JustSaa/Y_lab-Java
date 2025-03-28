package homework_1.controller;

import homework_1.services.AnalyticsService;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/{userId}/income")
    public Map<String, Double> getIncome(
            @PathVariable @Min(1) long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        double income = analyticsService.getTotalIncome(userId, start.toString(), end.toString());
        return Map.of("income", income);
    }

    @GetMapping("/{userId}/expenses")
    public Map<String, Double> getExpenses(
            @PathVariable @Min(1) long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        double expenses = analyticsService.getTotalExpenses(userId, start.toString(), end.toString());
        return Map.of("expenses", expenses);
    }

    @GetMapping("/{userId}/categories")
    public Map<String, String> getCategoryReport(@PathVariable @Min(1) long userId) {
        String report = analyticsService.analyzeExpensesByCategory(userId);
        return Map.of("categoryReport", report);
    }

    @GetMapping("/{userId}/report")
    public Map<String, String> getFullReport(@PathVariable @Min(1) long userId) {
        String report = analyticsService.generateFinancialReport(userId);
        return Map.of("report", report);
    }
}