package homework_1.repositories.jdbc;

import homework_1.domain.User;
import homework_1.repositories.UserRepository;

import java.sql.*;
import java.util.*;

public class JdbcUserRepository implements UserRepository {

    private final Connection connection;

    public JdbcUserRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(User user) {
        String sql = """
                INSERT INTO finance.users (id, name, email, password, is_admin, is_blocked)
                VALUES (nextval('finance.users_seq'), ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setBoolean(4, user.isAdmin());
            stmt.setBoolean(5, user.isBlocked());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения пользователя", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM finance.users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("is_admin"),
                        rs.getBoolean("is_blocked")
                );
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска пользователя по email", e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM finance.users";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<User> users = new ArrayList<>();
            while (rs.next()) {
                User user = new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("is_admin"),
                        rs.getBoolean("is_blocked")
                );
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка пользователей", e);
        }
    }

    @Override
    public void delete(String email) {
        String sql = "DELETE FROM finance.users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления пользователя", e);
        }
    }

    @Override
    public void update(User user) {
        String sql = """
                UPDATE finance.users
                SET name = ?, password = ?, is_admin = ?, is_blocked = ?
                WHERE email = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());
            stmt.setBoolean(3, user.isAdmin());
            stmt.setBoolean(4, user.isBlocked());
            stmt.setString(5, user.getEmail());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("Пользователь с email " + user.getEmail() + " не найден");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления пользователя", e);
        }
    }

    @Override
    public void blockUser(String email) {
        String sql = "UPDATE finance.users SET is_blocked = TRUE WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка блокировки пользователя", e);
        }
    }
}