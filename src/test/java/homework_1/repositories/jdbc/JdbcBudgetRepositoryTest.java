package homework_1.repositories.jdbc;

import homework_1.domain.Budget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcBudgetRepositoryTest extends AbstractTestContainerTest {

    private JdbcBudgetRepository budgetRepository;

    @BeforeEach
    void setUp() {
        budgetRepository = new JdbcBudgetRepository(connection);
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM finance.budgets")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM finance.users WHERE id = ?")) {
            stmt.setLong(1, 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String userSql = "INSERT INTO finance.users (id, name, email, password, user_role, is_blocked) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(userSql)) {
            stmt.setLong(1, 0);
            stmt.setString(2, "Test User");
            stmt.setString(3, "test@example.com");
            stmt.setString(4, "password");
            stmt.setString(5, "ADMIN");
            stmt.setBoolean(6, false);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании тестового пользователя", e);
        }
    }

    @Test
    void shouldSaveAndFindBudgetByUserId() throws SQLException {
        long userId = 0;
        Budget budget = new Budget(userId, 5000.0);

        budgetRepository.save(budget);
        Optional<Budget> foundBudget = budgetRepository.findByUserId(userId);

        assertThat(foundBudget).isPresent();
        assertThat(foundBudget.get().getUserId()).isEqualTo(userId);
        assertThat(foundBudget.get().getLimit()).isEqualTo(5000.0);
    }

    @Test
    void shouldReturnEmptyWhenBudgetNotFound() throws SQLException {
        Optional<Budget> foundBudget = budgetRepository.findByUserId(1);

        assertThat(foundBudget).isEmpty();
    }
}