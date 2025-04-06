package homework_1.repositories.jdbc;

import homework_1.domain.User;
import homework_1.domain.UserRole;
import homework_1.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
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

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole().name());
            stmt.setBoolean(5, user.isBlocked());
            return stmt;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            user.setId(key.longValue());
            log.info("Пользователь сохранён: {}", user);
        } else {
            log.warn("Не удалось получить сгенерированный ID для пользователя: {}", user);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        List<User> users = jdbcTemplate.query(SQL_SELECT_BY_EMAIL, userRowMapper(), email);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(users.get(0));
    }

    @Override
    public List<User> findAll() {
        List<User> users = jdbcTemplate.query(SQL_SELECT_ALL, userRowMapper());
        log.debug("Найдено пользователей: {}", users.size());
        return users;
    }

    @Override
    public void delete(String email) {
        int deleted = jdbcTemplate.update(SQL_DELETE_BY_EMAIL, email);
        if (deleted > 0) {
            log.info("Пользователь с email={} удалён", email);
        }
    }

    @Override
    public void update(User user) {
        int updated = jdbcTemplate.update(SQL_UPDATE,
                user.getName(),
                user.getPassword(),
                user.getRole().name(),
                user.isBlocked(),
                user.getEmail(),
                user.getId()
        );

        if (updated == 0) {
            throw new RuntimeException("Пользователь не найден для обновления: " + user.getEmail());
        }

        log.info("Пользователь обновлён: {}", user);
    }

    @Override
    public void blockUser(String email) {
        int updated = jdbcTemplate.update(SQL_BLOCK_USER, email);
        if (updated > 0) {
            log.info("Пользователь заблокирован: {}", email);
        }
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                UserRole.valueOf(rs.getString("user_role")),
                rs.getBoolean("is_blocked")
        );
    }
}