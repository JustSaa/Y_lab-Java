package homework_1.services;

import homework_1.domain.UserRole;
import homework_1.repositories.UserRepository;
import homework_1.common.exceptions.AuthenticationException;
import homework_1.domain.User;
import homework_1.services.impl.AuthServiceImpl;
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
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Иван Иванов", "ivan@mail.com", "password123", UserRole.USER);
    }

    @Test
    void register_NewUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        User createdUser = authServiceImpl.register(user.getName(), user.getEmail(), user.getPassword(), UserRole.USER);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ExistingEmail_ThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authServiceImpl.register(user.getName(), user.getEmail(), user.getPassword(), UserRole.USER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь с таким email уже существует");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_SuccessfulLogin_ReturnsUser() throws AuthenticationException {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User loggedInUser = authServiceImpl.login(user.getEmail(), user.getPassword());

        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authServiceImpl.login(user.getEmail(), "wrongpassword"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Неверный email или пароль");
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authServiceImpl.login(user.getEmail(), user.getPassword()))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Неверный email или пароль");
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        authServiceImpl.updateUser(user.getEmail(), "Новый Иван",
                "new@mail.com", "newpassword", UserRole.USER);

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
                authServiceImpl.updateUser(user.getEmail(), "Новый Иван",
                        "new@mail.com", "newpassword", UserRole.ADMIN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        authServiceImpl.deleteUser(user.getEmail());

        verify(userRepository).delete(user.getEmail());
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authServiceImpl.deleteUser(user.getEmail()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void getAllUsers_ShouldReturnUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = authServiceImpl.getAllUsers();

        assertThat(users).containsExactly(user);
    }

    @Test
    void blockUser_AsAdmin_ShouldBlockUser() {
        User admin = new User("Admin", "admin@mail.com", "password", UserRole.ADMIN);

        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        authServiceImpl.blockUser(admin.getEmail(), user.getEmail());

        verify(userRepository, times(1)).blockUser(user.getEmail());
    }

    @Test
    void deleteUser_AsAdmin_ShouldDeleteUser() {
        User admin = new User("Admin", "admin@mail.com", "password", UserRole.ADMIN);

        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        authServiceImpl.deleteUser(admin.getEmail(), user.getEmail());

        verify(userRepository, times(1)).delete(user.getEmail());
    }
}