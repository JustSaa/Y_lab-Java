package homework_1.services;

import homework_1.repositories.UserRepository;
import homework_1.common.exceptions.AuthenticationException;
import homework_1.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImplementation authServiceImplementation;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Иван Иванов", "ivan@mail.com", "password123");
    }

    @Test
    void register_NewUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        User createdUser = authServiceImplementation.register(user.getName(), user.getEmail(), user.getPassword());

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ExistingEmail_ThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authServiceImplementation.register(user.getName(), user.getEmail(), user.getPassword()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь с таким email уже существует");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_SuccessfulLogin_ReturnsUser() throws AuthenticationException {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User loggedInUser = authServiceImplementation.login(user.getEmail(), user.getPassword());

        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authServiceImplementation.login(user.getEmail(), "wrongpassword"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Неверный email или пароль");
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authServiceImplementation.login(user.getEmail(), user.getPassword()))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Неверный email или пароль");
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        authServiceImplementation.updateUser(user.getEmail(), "Новый Иван",
                "new@mail.com", "newpassword");

        verify(userRepository).update(argThat(updatedUser ->
                updatedUser.getName().equals("Новый Иван") &&
                        updatedUser.getEmail().equals("new@mail.com") &&
                        updatedUser.getPassword().equals("newpassword")
        ));
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authServiceImplementation.updateUser(user.getEmail(), "Новый Иван",
                        "new@mail.com", "newpassword"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        authServiceImplementation.deleteUser(user.getEmail());

        verify(userRepository).delete(user.getEmail());
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authServiceImplementation.deleteUser(user.getEmail()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void getAllUsers_ShouldReturnUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = authServiceImplementation.getAllUsers();

        assertThat(users).containsExactly(user);
    }

    @Test
    void blockUser_AsAdmin_ShouldBlockUser() {
        User admin = new User("Admin", "admin@mail.com", "password");
        admin.setAdmin(true);

        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        authServiceImplementation.blockUser(admin.getEmail(), user.getEmail());

        verify(userRepository, times(1)).blockUser(user.getEmail());
    }

    @Test
    void deleteUser_AsAdmin_ShouldDeleteUser() {
        User admin = new User("Admin", "admin@mail.com", "password");
        admin.setAdmin(true);

        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        authServiceImplementation.deleteUser(admin.getEmail(), user.getEmail());

        verify(userRepository, times(1)).delete(user.getEmail());
    }
}