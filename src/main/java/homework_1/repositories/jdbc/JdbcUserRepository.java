package homework_1.repositories.jdbc;

import homework_1.domain.User;
import homework_1.domain.UserRole;
import homework_1.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Реализация {@link UserRepository} для работы с пользователями
 * с использованием JDBC и базы данных PostgreSQL.
 */
public class JdbcUserRepository implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcUserRepository.class);

    private static final String SAVE = """
            INSERT INTO finance.users (name, email, password, user_role, is_blocked)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;
    private static final String FIND_BY_EMAIL = "SELECT * FROM finance.users WHERE email = ?";
    private static final String FIND_BY_ALL = "SELECT * FROM finance.users";
    private static final String DELETE = "DELETE FROM finance.users WHERE email = ?";
    private static final String UPDATE = """
            UPDATE finance.users
            SET name = ?, password = ?, user_role = ?, is_blocked = ?, email = ?
            WHERE id = ?
            """;
    private static final String BLOCK_USER = "UPDATE finance.users SET is_blocked = TRUE WHERE email = ?";


    private final Connection connection;

    /**
     * Конструктор репозитория пользователей.
     *
     * @param connection объект {@link Connection} для работы с базой данных.
     */
    public JdbcUserRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Сохраняет нового пользователя в базе данных.
     *
     * @param user объект {@link User}, содержащий данные пользователя.
     * @throws RuntimeException если произошла ошибка при сохранении в БД.
     */
    @Override
    public void save(User user) {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getRole().name());
                stmt.setBoolean(5, user.isBlocked());

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getLong("id"));
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                logger.error("Ошибка сохранения пользователя. Транзакция откатилась.", e);
                throw new RuntimeException("Ошибка сохранения пользователя", e);
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Ошибка при откате транзакции", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Ошибка при включении autoCommit", e);
            }
        }
    }

    /**
     * Ищет пользователя по email.
     *
     * @param email email пользователя.
     * @return {@link Optional} с объектом {@link User}, если пользователь найден, иначе пустой {@link Optional}.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public Optional<User> findByEmail(String email) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_EMAIL)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapUser(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска пользователя по email", e);
        }
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей.
     * @throws RuntimeException если произошла ошибка при выполнении запроса.
     */
    @Override
    public List<User> findAll() {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ALL);
             ResultSet rs = stmt.executeQuery()) {

            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка пользователей", e);
        }
    }

    /**
     * Удаляет пользователя по email.
     *
     * @param email email пользователя.
     * @throws RuntimeException если произошла ошибка при удалении из БД.
     */
    @Override
    public void delete(String email) {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(DELETE)) {
                stmt.setString(1, email);
                stmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                logger.error("Ошибка удаления пользователя. Транзакция откатилась.", e);
                throw new RuntimeException("Ошибка удаления пользователя", e);
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Ошибка при откате транзакции", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Ошибка при включении autoCommit", e);
            }
        }
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param user объект {@link User} с обновленными значениями.
     * @throws RuntimeException если произошла ошибка при обновлении в БД.
     */
    @Override
    public void update(User user) {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(UPDATE)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getPassword());
                stmt.setString(3, user.getRole().name());
                stmt.setBoolean(4, user.isBlocked());
                stmt.setString(5, user.getEmail());
                stmt.setLong(6, user.getId());

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new RuntimeException("Пользователь с email " + user.getEmail() + " не найден");
                }
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                logger.error("Ошибка обновления пользователя. Транзакция откатилась.", e);
                throw new RuntimeException("Ошибка обновления пользователя", e);
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Ошибка при откате транзакции", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Ошибка при включении autoCommit", e);
            }
        }
    }

    /**
     * Блокирует пользователя по email.
     *
     * @param email email пользователя.
     * @throws RuntimeException если произошла ошибка при обновлении в БД.
     */
    @Override
    public void blockUser(String email) {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(BLOCK_USER)) {
                stmt.setString(1, email);
                stmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                logger.error("Ошибка блокировки пользователя. Транзакция откатилась.", e);
                throw new RuntimeException("Ошибка блокировки пользователя", e);
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Ошибка при откате транзакции", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Ошибка при включении autoCommit", e);
            }
        }
    }

    /**
     * Метод для маппинга ResultSet на сущность User.
     */
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