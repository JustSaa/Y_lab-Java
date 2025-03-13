package homework_1.domain;

import java.util.Objects;

/**
 * Сущность пользователя.
 */
public class User {
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
     * Является ли пользователь админом
     */
    private boolean isAdmin = false;
    /**
     * Заблокирован ли пользователь
     */
    private boolean isBlocked = false;

    /**
     * Конструктор со всеми полями.
     *
     * @param name     имя пользователя
     * @param email    email
     * @param password пароль
     */
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return email.equals(user.email);
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
                '}';
    }
}