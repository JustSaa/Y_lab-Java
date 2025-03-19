package homework_1.repositories.jdbc;

import homework_1.domain.User;
import homework_1.repositories.UserRepository;

import java.sql.*;
import java.util.*;

/**
 * Реализация {@link UserRepository} для работы с пользователями
 * с использованием JDBC и базы данных PostgreSQL.
 */
public class JdbcUserRepository implements UserRepository {

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
        String sql = """
            INSERT INTO finance.users (name, email, password, is_admin, is_blocked)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setBoolean(4, user.isAdmin());
            stmt.setBoolean(5, user.isBlocked());

            stmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong("id")); // Устанавливаем ID после сохранения
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения пользователя", e);
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
        String sql = "SELECT * FROM finance.users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM finance.users";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
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
        String sql = "DELETE FROM finance.users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления пользователя", e);
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

    /**
     * Блокирует пользователя по email.
     *
     * @param email email пользователя.
     * @throws RuntimeException если произошла ошибка при обновлении в БД.
     */
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

    /**
     * Метод для маппинга ResultSet на сущность User.
     */
    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getBoolean("is_admin"),
                rs.getBoolean("is_blocked")
        );
    }
}