package homework_1.controller;

import homework_1.domain.Budget;
import homework_1.dto.SetBudgetDto;
import homework_1.services.BudgetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> setBudget(@Valid @RequestBody SetBudgetDto dto) {
        budgetService.setUserBudget(dto.getUserId(), dto.getLimit());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Бюджет установлен"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getBudget(@PathVariable @Min(1) long userId) {
        Optional<Budget> budget = budgetService.getUserBudget(userId);
        return budget
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Бюджет не найден")));
    }

    @GetMapping("/{userId}/exceeded")
    public ResponseEntity<Map<String, Boolean>> isBudgetExceeded(@PathVariable @Min(1) long userId) {
        boolean exceeded = budgetService.isBudgetExceeded(userId);
        return ResponseEntity.ok(Map.of("budgetExceeded", exceeded));
    }
}