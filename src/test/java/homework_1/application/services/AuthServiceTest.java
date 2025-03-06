package homework_1.application.services;

import homework_1.application.ports.out.UserRepository;
import homework_1.common.exceptions.AuthenticationException;
import homework_1.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Иван Иванов", "ivan@mail.com", "password123");
    }

    @Test
    void register_NewUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        User createdUser = authService.register(user.getName(), user.getEmail(), user.getPassword());

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ExistingEmail_ThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.register(user.getName(), user.getEmail(), user.getPassword()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь с таким email уже существует");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_SuccessfulLogin_ReturnsUser() throws AuthenticationException {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User loggedInUser = authService.login(user.getEmail(), user.getPassword());

        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(user.getEmail(), "wrongpassword"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Неверный email или пароль");
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(user.getEmail(), user.getPassword()))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Неверный email или пароль");
    }
}