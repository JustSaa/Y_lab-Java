package homework_1.repositories.jdbc;

import homework_1.domain.Budget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcBudgetRepositoryTest extends AbstractTestContainerTest {

    private JdbcBudgetRepository budgetRepository;

    @BeforeEach
    void setUp() {
        budgetRepository = new JdbcBudgetRepository(connection);
    }

    @Test
    void shouldSaveAndFindBudgetByUserEmail() throws SQLException {
        String email = "user@test.com";
        Budget budget = new Budget(email, 5000.0);

        budgetRepository.save(budget);
        Optional<Budget> foundBudget = budgetRepository.findByUserEmail(email);

        assertThat(foundBudget).isPresent();
        assertThat(foundBudget.get().getUserEmail()).isEqualTo(email);
        assertThat(foundBudget.get().getLimit()).isEqualTo(5000.0);
    }

    @Test
    void shouldReturnEmptyWhenBudgetNotFound() throws SQLException {
        Optional<Budget> foundBudget = budgetRepository.findByUserEmail("notfound@test.com");

        assertThat(foundBudget).isEmpty();
    }
}