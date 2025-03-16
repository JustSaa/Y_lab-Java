package homework_1.repositories.jdbc;

import homework_1.domain.Budget;
import homework_1.repositories.BudgetRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcBudgetRepository implements BudgetRepository {

    private final Connection connection;

    public JdbcBudgetRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Budget budget) {
        String sql = "INSERT INTO finance.budgets (id, user_email, budget_limit) VALUES (nextval('finance.budgets_seq'), ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, budget.getUserEmail());
            stmt.setDouble(2, budget.getLimit());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения бюджета пользователя", e);
        }
    }

    @Override
    public Optional<Budget> findByUserEmail(String userEmail) {
        String sql = "SELECT * FROM finance.budgets WHERE user_email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Budget budget = new Budget(
                            rs.getString("user_email"),
                            rs.getDouble("budget_limit")
                    );
                    return Optional.of(budget);
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска бюджета пользователя по email", e);
        }
    }
}
