package homework_1.ui;

import homework_1.common.exceptions.AuthenticationException;
import homework_1.domain.User;
import homework_1.domain.UserRole;
import homework_1.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthConsoleHandlerTest {

    private AuthService authService;
    private AuthConsoleHandler authConsoleHandler;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        authConsoleHandler = new AuthConsoleHandler(authService, new Scanner(System.in));
    }

    @Test
    void register_ShouldCreateUser_WhenInputIsValid() {
        String input = "Иван Иванов\nivan@mail.com\npassword123\nда\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        authConsoleHandler = new AuthConsoleHandler(authService, new Scanner(System.in));

        User mockUser = new User("Иван Иванов", "ivan@mail.com", "password123", UserRole.ADMIN);
        when(authService.register("Иван Иванов", "ivan@mail.com", "password123", UserRole.ADMIN)).thenReturn(mockUser);

        User registeredUser = authConsoleHandler.register();

        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getEmail()).isEqualTo("ivan@mail.com");
        verify(authService, times(1)).register("Иван Иванов", "ivan@mail.com", "password123", UserRole.ADMIN);
    }

    @Test
    void register_ShouldReturnNull_WhenEmailIsInvalid() {
        String input = "Иван Иванов\ninvalid-email\npassword123\nда\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        authConsoleHandler = new AuthConsoleHandler(authService, new Scanner(System.in));

        User registeredUser = authConsoleHandler.register();

        assertThat(registeredUser).isNull();
        verify(authService, never()).register(anyString(), anyString(), anyString(), any(UserRole.class));
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreCorrect() throws AuthenticationException {
        String input = "ivan@mail.com\npassword123\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        authConsoleHandler = new AuthConsoleHandler(authService, new Scanner(System.in));

        User mockUser = new User("Иван Иванов", "ivan@mail.com", "password123", UserRole.USER);
        when(authService.login("ivan@mail.com", "password123")).thenReturn(mockUser);

        User loggedInUser = authConsoleHandler.login();

        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getEmail()).isEqualTo("ivan@mail.com");
        verify(authService, times(1)).login("ivan@mail.com", "password123");
    }

    @Test
    void login_ShouldReturnNull_WhenCredentialsAreIncorrect() throws AuthenticationException {
        String input = "ivan@mail.com\nwrongpassword\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        authConsoleHandler = new AuthConsoleHandler(authService, new Scanner(System.in));

        when(authService.login("ivan@mail.com", "wrongpassword")).thenThrow(new IllegalArgumentException("Ошибка входа"));

        User loggedInUser = authConsoleHandler.login();

        assertThat(loggedInUser).isNull();
        verify(authService, times(1)).login("ivan@mail.com", "wrongpassword");
    }

    @Test
    void editProfile_ShouldNotUpdate_WhenEmailIsInvalid() {
        User mockUser = new User("Иван Иванов", "ivan@mail.com", "password123", UserRole.USER);
        authConsoleHandler = new AuthConsoleHandler(authService, new Scanner(System.in));

        String input = "Новый Иван\ninvalid-email\nnewpassword\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        authConsoleHandler = new AuthConsoleHandler(authService, new Scanner(System.in));

        authConsoleHandler.editProfile();

        verify(authService, never()).updateUser(anyString(), anyString(), anyString(), anyString(), any(UserRole.class));
    }
}