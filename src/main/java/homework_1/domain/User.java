package homework_1.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Сущность пользователя.
 */
public class User {
    /**
     * Уникальный идентификатор пользователя
     */
    private final UUID id;

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
     * Установленный месячный бюджет пользователя
     */
    private double monthlyBudget;

    /**
     * Конструктор с основными полями (без monthlyBudget).
     *
     * @param name     имя пользователя
     * @param email    email
     * @param password пароль
     */
    public User(String name, String email, String password) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Конструктор со всеми полями, кроме ID.
     *
     * @param name          имя пользователя
     * @param email         email
     * @param password      пароль
     * @param monthlyBudget месячный бюджет
     */
    public User(String name, String email, String password, double monthlyBudget) {
        this(UUID.randomUUID(), name, email, password, monthlyBudget);
    }

    /**
     * Конструктор с полным списком полей (для восстановления пользователя из репозитория).
     *
     * @param id            ID пользователя
     * @param name          имя пользователя
     * @param email         email
     * @param password      пароль
     * @param monthlyBudget месячный бюджет
     */
    public User(UUID id, String name, String email, String password, double monthlyBudget) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.monthlyBudget = monthlyBudget;
    }

    public UUID getId() {
        return id;
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

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
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
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", monthlyBudget=" + monthlyBudget +
                '}';
    }
}