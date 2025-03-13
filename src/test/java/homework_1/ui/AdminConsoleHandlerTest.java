package homework_1.ui;

import homework_1.domain.User;
import homework_1.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class AdminConsoleHandlerTest {
    private AuthService authService;
    private AdminConsoleHandler adminConsoleHandler;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        adminConsoleHandler = new AdminConsoleHandler(authService, new Scanner(System.in));
    }

    @Test
    void showAllUsers_ShouldDisplayUsers() {
        List<User> users = List.of(
                new User("Admin", "admin@mail.com", "password123"),
                new User("User1", "user1@mail.com", "password123")
        );

        when(authService.getAllUsers()).thenReturn(users);

        adminConsoleHandler.showAllUsers();

        verify(authService, times(1)).getAllUsers();
    }

    @Test
    void blockUser_ShouldBlockUser_WhenAdmin() {
        User admin = new User("Admin", "admin@mail.com", "password");
        admin.setAdmin(true);

        String userEmail = "user1@mail.com";
        String input = userEmail + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        adminConsoleHandler = new AdminConsoleHandler(authService, new Scanner(System.in));
        adminConsoleHandler.blockUser(admin);

        verify(authService, times(1)).blockUser(admin.getEmail(), userEmail);
    }

    @Test
    void blockUser_ShouldNotBlockUser_WhenNotAdmin() {
        User user = new User("User1", "user1@mail.com", "password");

        adminConsoleHandler.blockUser(user);

        verify(authService, never()).blockUser(anyString(), anyString());
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenAdmin() {
        User admin = new User("Admin", "admin@mail.com", "password");
        admin.setAdmin(true);

        String userEmail = "user2@mail.com";
        String input = userEmail + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        adminConsoleHandler = new AdminConsoleHandler(authService, new Scanner(System.in));
        adminConsoleHandler.deleteUser(admin);

        verify(authService, times(1)).deleteUser(admin.getEmail(), userEmail);
    }

    @Test
    void deleteUser_ShouldNotDeleteUser_WhenNotAdmin() {
        User user = new User("User1", "user1@mail.com", "password");

        adminConsoleHandler.deleteUser(user);

        verify(authService, never()).deleteUser(anyString(), anyString());
    }
}