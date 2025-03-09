package homework_1.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void gettersAndSetters_WorkCorrectly() {
        User user = new User("Иван", "ivan@mail.com", "pass");

        user.setName("Пётр");
        user.setEmail("petr@mail.com");
        user.setMonthlyBudget(5000);

        assertThat(user.getName()).isEqualTo("Пётр");
        assertThat(user.getEmail()).isEqualTo("petr@mail.com");
        assertThat(user.getMonthlyBudget()).isEqualTo(5000);
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User("Ivan", "ivan@mail.com", "pass", 1000.00);
        User user2 = new User("Иван Иванов", "ivan@mail.com", "123", 3000);

        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }
}