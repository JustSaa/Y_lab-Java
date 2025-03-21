package homework_1.repositories.jdbc;

import homework_1.domain.User;
import homework_1.domain.UserRole;
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
        User user = new User("John Doe", "john@example.com", "password123", UserRole.USER);

        userRepository.save(user);
        Optional<User> foundUser = userRepository.findByEmail("john@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void shouldDeleteUser() {
        User user = new User("JoonaSnow", "JoonaSnow@example.com", "pass", UserRole.USER);
        userRepository.save(user);

        userRepository.delete("JoonaSnow@example.com");
        Optional<User> foundUser = userRepository.findByEmail("JoonaSnow@example.com");

        assertThat(foundUser).isEmpty();
    }

    @Test
    void shouldUpdateUser() {
        User user = new User(1, "Jane Doe", "jane@example.com", "pass", UserRole.USER, true);
        User userUpdate = new User(1, "Ivan", "ivan@example.com", "pass", UserRole.USER, true);
        userRepository.save(user);
        userRepository.update(userUpdate);

        Optional<User> foundUser = userRepository.findByEmail("ivan@example.com");
        assertThat(foundUser.get()).isEqualTo(userUpdate);
    }
}