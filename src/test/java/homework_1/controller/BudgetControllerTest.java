package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.domain.Budget;
import homework_1.services.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BudgetControllerTest {

    private MockMvc mockMvc;
    private BudgetService budgetService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        budgetService = mock(BudgetService.class);
        BudgetController controller = new BudgetController(budgetService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void setBudget_ShouldReturnCreated() throws Exception {
        Map<String, Object> dto = Map.of("userId", 1L, "limit", 1000.0);

        mockMvc.perform(post("/api/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Бюджет установлен"));

        verify(budgetService).setUserBudget(1L, 1000.0);
    }

    @Test
    void getBudget_ShouldReturnBudget_WhenExists() throws Exception {
        Budget budget = new Budget(1L, 1500.0);
        when(budgetService.getUserBudget(1L)).thenReturn(Optional.of(budget));

        mockMvc.perform(get("/api/budget/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.limit").value(1500.0));
    }

    @Test
    void getBudget_ShouldReturnNotFound_WhenNotExists() throws Exception {
        when(budgetService.getUserBudget(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/budget/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Бюджет не найден"));
    }

    @Test
    void isBudgetExceeded_ShouldReturnTrue() throws Exception {
        when(budgetService.isBudgetExceeded(1L)).thenReturn(true);

        mockMvc.perform(get("/api/budget/1/exceeded"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.budgetExceeded").value(true));
    }

    @Test
    void isBudgetExceeded_ShouldReturnFalse() throws Exception {
        when(budgetService.isBudgetExceeded(1L)).thenReturn(false);

        mockMvc.perform(get("/api/budget/1/exceeded"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.budgetExceeded").value(false));
    }
}