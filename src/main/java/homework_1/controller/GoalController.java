package homework_1.controller;

import homework_1.domain.Goal;
import homework_1.services.GoalService;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Goal> createGoal(
            @PathVariable @Min(1) long userId,
            @RequestParam String name,
            @RequestParam double amount
    ) {
        Goal goal = goalService.createGoal(userId, name, amount);
        return ResponseEntity.ok(goal);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable @Min(1) long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Goal>> getUserGoals(@PathVariable @Min(1) long userId) throws SQLException {
        return ResponseEntity.ok(goalService.getUserGoals(userId));
    }
}