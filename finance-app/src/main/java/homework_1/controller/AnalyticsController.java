package homework_1.controller;

import audit.aspect.annotation.Audit;
import audit.aspect.annotation.LogExecutionTime;
import homework_1.dto.*;
import homework_1.mapper.AnalyticsMapper;
import homework_1.services.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.mapstruct.factory.Mappers;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AnalyticsMapper analyticsMapper;

    public AnalyticsController(AnalyticsService analyticsService, AnalyticsMapper analyticsMapper) {
        this.analyticsService = analyticsService;
        this.analyticsMapper = analyticsMapper;
    }

    @GetMapping("/{userId}/income")
    @Operation(summary = "Получение доходов за период", description = "Возвращает сумму доходов пользователя за указанный период")
    public IncomeResponseDto getIncome(
            @PathVariable @Min(1) long userId,
            @Valid AnalyticsRequestDto dto) {
        dto.setUserId(userId);
        double income = analyticsService.getTotalIncome(userId, dto.getStart(), dto.getEnd());
        return analyticsMapper.toIncomeDto(income);
    }

    @GetMapping("/{userId}/expenses")
    @Operation(summary = "Получение расходов за период", description = "Возвращает сумму расходов пользователя за указанный период")
    public ExpensesResponseDto getExpenses(
            @PathVariable @Min(1) long userId,
            @Valid AnalyticsRequestDto dto) {
        dto.setUserId(userId);
        double expenses = analyticsService.getTotalExpenses(userId, dto.getStart(), dto.getEnd());
        return analyticsMapper.toExpensesDto(expenses);
    }

    @GetMapping("/{userId}/categories")
    @Operation(summary = "Отчёт по категориям расходов", description = "Возвращает отчёт расходов пользователя по категориям")
    public CategoryResponseDto getCategoryReport(@PathVariable("userId") @Min(1) long userId) {
        String report = analyticsService.analyzeExpensesByCategory(userId);
        return analyticsMapper.toCategoryDto(report);
    }

    @GetMapping("/{userId}/report")
    @Operation(summary = "Полный финансовый отчёт", description = "Возвращает полный финансовый отчёт пользователя")
    public FullReportResponseDto getFullReport(@PathVariable("userId") @Min(1) long userId) {
        String report = analyticsService.generateFinancialReport(userId);
        return analyticsMapper.toFullReportDto(report);
    }
}