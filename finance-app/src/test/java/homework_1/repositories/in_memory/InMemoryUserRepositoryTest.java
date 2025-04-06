package homework_1.repositories.in_memory;

import homework_1.domain.User;
import homework_1.domain.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class InMemoryUserRepositoryTest {

    private final InMemoryUserRepository repository = new InMemoryUserRepository();

    @Test
    void saveAndFindByEmail_Success() {
        User user = new User("Иван Иванов", "ivan@mail.com", "pass", UserRole.USER);
        repository.save(user);

        Optional<User> foundUser = repository.findByEmail(user.getEmail());

        assertThat(foundUser).isPresent().contains(user);
    }

    @Test
    void findById_UserExists_ReturnsUser() {
        User user = new User("Иван", "ivan@mail.com", "pass", UserRole.USER);
        repository.save(user);

        Optional<User> found = repository.findByEmail(user.getEmail());

        assertThat(found).contains(user);
    }

    @Test
    void delete_User_RemovedSuccessfully() {
        User user = new User("Иван", "ivan@mail.com", "pass", UserRole.USER);
        repository.save(user);

        repository.delete(user.getEmail());

        assertThat(repository.findByEmail(user.getEmail())).isEmpty();
    }
}