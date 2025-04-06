package homework_1.controller;

import homework_1.domain.Budget;
import homework_1.dto.ApiResponse;
import homework_1.dto.BudgetResponseDto;
import homework_1.dto.CreateBudgetDto;
import homework_1.mapper.BudgetMapper;
import homework_1.services.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;
    private final BudgetMapper budgetMapper;

    public BudgetController(BudgetService budgetService, BudgetMapper budgetMapper) {
        this.budgetService = budgetService;
        this.budgetMapper = budgetMapper;
    }

    @PostMapping
    @Operation(summary = "Создание бюджета", description = "Создаёт или обновляет бюджет для пользователя.")
    public ResponseEntity<BudgetResponseDto> createBudget(@Valid @RequestBody CreateBudgetDto dto) {
        Budget budget = budgetService.createUserBudget(dto.getUserId(), dto.getLimit());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(budgetMapper.toBudgetResponseDto(budget.getUserId(), budget.getLimit()));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получение бюджета", description = "Возвращает установленный бюджет пользователя по его ID.")
    public ResponseEntity<BudgetResponseDto> getBudget(@PathVariable @Min(1) long userId) {
        Budget budget = budgetService.getUserBudget(userId);
        return ResponseEntity.ok(budgetMapper.toBudgetResponseDto(budget.getUserId(), budget.getLimit()));
    }

    @GetMapping("/{userId}/exceeded")
    @Operation(summary = "Проверка превышения бюджета", description = "Проверяет, превышен ли текущий бюджет пользователя.")
    public ResponseEntity<ApiResponse<Boolean>> isBudgetExceeded(@PathVariable @Min(1) long userId) {
        boolean exceeded = budgetService.isBudgetExceeded(userId);
        return ResponseEntity.ok(new ApiResponse<>("Превышен ли бюджет: ", exceeded));
    }
}