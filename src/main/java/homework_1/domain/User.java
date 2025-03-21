package homework_1.domain;

import java.util.Objects;

/**
 * Сущность пользователя.
 */
public class User {
    long id;
    /**
     * Имя пользователя
     */
    private String name;

    /**
     * Электронная почта пользователя (уникальна)
     */
    private String email;

    /**
     * Пароль пользователя
     */
    private String password;
    /**
     * Роль пользователя
     */
    private UserRole role;
    /**
     * Заблокирован ли пользователь
     */
    private boolean isBlocked = false;

    /**
     * Конструктор для создания нового пользователя (без ID).
     *
     * @param name     имя пользователя
     * @param email    email
     * @param password пароль
     * @param role     Роль пользователя (например, ADMIN, USER)
     */
    public User(String name, String email, String password, UserRole role) {
        this.id = 0;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isBlocked = false;
    }

    /**
     * Конструктор для загрузки пользователя из БД.
     */
    public User(long id, String name, String email, String password, UserRole role, boolean isBlocked) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isBlocked = isBlocked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Проверяет, является ли пользователь администратором.
     *
     * @return true, если у пользователя роль ADMIN.
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}