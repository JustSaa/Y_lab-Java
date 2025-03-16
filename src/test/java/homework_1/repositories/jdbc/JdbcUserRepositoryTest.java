package homework_1.repositories.jdbc;

import homework_1.domain.User;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcUserRepositoryTest extends AbstractTestContainerTest {

    private JdbcUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new JdbcUserRepository(connection);
    }

    @Test
    void shouldSaveAndFindUserByEmail() {
        User user = new User("John Doe", "john@example.com", "password123", false);

        userRepository.save(user);
        Optional<User> foundUser = userRepository.findByEmail("john@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void shouldDeleteUser() {
        User user = new User("JoonaSnow", "JoonaSnow@example.com", "pass", false);
        userRepository.save(user);

        userRepository.delete("JoonaSnow@example.com");
        Optional<User> foundUser = userRepository.findByEmail("JoonaSnow@example.com");

        assertThat(foundUser).isEmpty();
    }

    @Test
    void shouldUpdateUser() {
        User user = new User("Jane Doe", "jane@example.com", "pass", false);
        User userUpdate = new User("Ivan", "jane@example.com", "pass", false);
        userRepository.save(user);
        userRepository.update(userUpdate);

        Optional<User> foundUser = userRepository.findByEmail("jane@example.com");
        assertThat(foundUser.get()).isEqualTo(userUpdate);
    }
}