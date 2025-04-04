package homework_1.repositories.jdbc;

import homework_1.domain.User;
import homework_1.domain.UserRole;
import homework_1.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(JdbcUserRepository.class);

    private static final String SQL_INSERT = """
            INSERT INTO finance.users (name, email, password, user_role, is_blocked)
            VALUES (?, ?, ?, ?, ?) RETURNING id
            """;

    private static final String SQL_SELECT_BY_EMAIL = "SELECT * FROM finance.users WHERE email = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM finance.users";
    private static final String SQL_DELETE_BY_EMAIL = "DELETE FROM finance.users WHERE email = ?";
    private static final String SQL_UPDATE = """
            UPDATE finance.users
            SET name = ?, password = ?, user_role = ?, is_blocked = ?, email = ?
            WHERE id = ?
            """;

    private static final String SQL_BLOCK_USER = "UPDATE finance.users SET is_blocked = TRUE WHERE email = ?";

    private final DataSource dataSource;

    public JdbcUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(User user) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole().name());
            stmt.setBoolean(5, user.isBlocked());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong("id"));
                    log.info("Пользователь сохранён: {}", user);
                }
            }

        } catch (SQLException e) {
            log.error("Ошибка при сохранении пользователя: {}", user, e);
            throw new RuntimeException("Ошибка сохранения пользователя", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_EMAIL)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapUser(rs);
                    log.debug("Пользователь найден по email {}: {}", email, user);
                    return Optional.of(user);
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            log.error("Ошибка поиска пользователя по email={}", email, e);
            throw new RuntimeException("Ошибка поиска пользователя", e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
            log.debug("Найдено пользователей: {}", users.size());
            return users;

        } catch (SQLException e) {
            log.error("Ошибка получения всех пользователей", e);
            throw new RuntimeException("Ошибка получения пользователей", e);
        }
    }

    @Override
    public void delete(String email) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_BY_EMAIL)) {

            stmt.setString(1, email);
            stmt.executeUpdate();
            log.info("Пользователь с email={} удалён", email);

        } catch (SQLException e) {
            log.error("Ошибка при удалении пользователя: {}", email, e);
            throw new RuntimeException("Ошибка удаления пользователя", e);
        }
    }

    @Override
    public void update(User user) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().name());
            stmt.setBoolean(4, user.isBlocked());
            stmt.setString(5, user.getEmail());
            stmt.setLong(6, user.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Пользователь не найден для обновления: " + user.getEmail());
            }

            log.info("Пользователь обновлён: {}", user);

        } catch (SQLException e) {
            log.error("Ошибка при обновлении пользователя: {}", user, e);
            throw new RuntimeException("Ошибка обновления пользователя", e);
        }
    }

    @Override
    public void blockUser(String email) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_BLOCK_USER)) {

            stmt.setString(1, email);
            stmt.executeUpdate();
            log.info("Пользователь заблокирован: {}", email);

        } catch (SQLException e) {
            log.error("Ошибка при блокировке пользователя: {}", email, e);
            throw new RuntimeException("Ошибка блокировки пользователя", e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                UserRole.valueOf(rs.getString("user_role")),
                rs.getBoolean("is_blocked")
        );
    }
}