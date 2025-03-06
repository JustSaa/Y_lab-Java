package homework_1.services.adapters.out;

import homework_1.adapters.out.InMemoryUserRepository;
import homework_1.domain.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class InMemoryUserRepositoryTest {

    private final InMemoryUserRepository repository = new InMemoryUserRepository();

    @Test
    void saveAndFindByEmail_Success() {
        User user = new User("Иван Иванов", "ivan@mail.com", "pass");
        repository.save(user);

        Optional<User> foundUser = repository.findByEmail(user.getEmail());

        assertThat(foundUser).isPresent().contains(user);
    }

    @Test
    void findById_UserExists_ReturnsUser() {
        User user = new User("Иван", "ivan@mail.com", "pass");
        repository.save(user);

        Optional<User> found = repository.findById(user.getId());

        assertThat(found).contains(user);
    }

    @Test
    void delete_User_RemovedSuccessfully() {
        User user = new User("Иван", "ivan@mail.com", "pass");
        repository.save(user);

        repository.delete(user.getId());

        assertThat(repository.findById(user.getId())).isEmpty();
    }
}