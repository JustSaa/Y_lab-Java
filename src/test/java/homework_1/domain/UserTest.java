package homework_1.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void gettersAndSetters_WorkCorrectly() {
        User user = new User("Иван", "ivan@mail.com", "pass", false);

        user.setName("Пётр");
        user.setEmail("petr@mail.com");

        assertThat(user.getName()).isEqualTo("Пётр");
        assertThat(user.getEmail()).isEqualTo("petr@mail.com");
    }
}