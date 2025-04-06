package homework_1.controller;

import homework_1.domain.Goal;
import homework_1.dto.CreateGoalDto;
import homework_1.dto.GoalResponseDto;
import homework_1.mapper.GoalMapper;
import homework_1.services.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;
    private final GoalMapper goalMapper;

    public GoalController(GoalService goalService, GoalMapper goalMapper) {
        this.goalService = goalService;
        this.goalMapper = goalMapper;
    }

    @PostMapping("/{userId}")
    @Operation(summary = "Создание цели", description = "Создает новую финансовую цель для пользователя")
    public ResponseEntity<GoalResponseDto> createGoal(@Valid @RequestBody CreateGoalDto dto) {
        Goal goal = goalService.createGoal(dto.getUserId(), dto.getName(), dto.getTargetAmount());
        return ResponseEntity.ok(goalMapper.toGoalResponseDto(goal.getUserId(), goal.getName(), goal.getTargetAmount()));
    }

    @DeleteMapping("/{goalId}")
    @Operation(summary = "Удаление цели", description = "Удаляет цель по её ID")
    public ResponseEntity<Void> deleteGoal(@PathVariable @Min(1) long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получение целей", description = "Получает все цели пользователя")
    public ResponseEntity<List<GoalResponseDto>> getUserGoals(@PathVariable @Min(1) long userId) throws SQLException {
        List<GoalResponseDto> goals = goalService.getUserGoals(userId).stream()
                .map(goal -> goalMapper.toGoalResponseDto(goal.getUserId(), goal.getName(), goal.getTargetAmount()))
                .toList();
        return ResponseEntity.ok(goals);
    }
}