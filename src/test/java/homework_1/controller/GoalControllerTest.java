package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.domain.Goal;
import homework_1.services.GoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GoalControllerTest {

    private MockMvc mockMvc;
    private GoalService goalService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        goalService = mock(GoalService.class);
        GoalController controller = new GoalController(goalService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createGoal_ShouldReturnCreatedGoal() throws Exception {
        Goal goal = new Goal(1, "Квартира", 1000000.0);
        when(goalService.createGoal(1, "Квартира", 1000000.0)).thenReturn(goal);

        mockMvc.perform(post("/api/goals/1")
                        .param("name", "Квартира")
                        .param("amount", "1000000.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(goal.getId()))
                .andExpect(jsonPath("$.userId").value(goal.getUserId()))
                .andExpect(jsonPath("$.name").value(goal.getName()));
    }

    @Test
    void deleteGoal_ShouldReturnNoContent() throws Exception {
        long goalId = 2L;

        mockMvc.perform(delete("/api/goals/" + goalId))
                .andExpect(status().isNoContent());

        verify(goalService).deleteGoal(goalId);
    }

    @Test
    void getUserGoals_ShouldReturnGoalList() throws Exception {
        List<Goal> goals = List.of(
                new Goal(1, "Квартира", 1000000.0),
                new Goal(2,"Машина", 500000.0)
        );
        when(goalService.getUserGoals(1)).thenReturn(goals);

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(goals.size()))
                .andExpect(jsonPath("$[0].name").value("Квартира"))
                .andExpect(jsonPath("$[0].targetAmount").value(1000000.0))
                .andExpect(jsonPath("$[1].name").value("Машина"))
                .andExpect(jsonPath("$[1].targetAmount").value(500000.0));
    }
}