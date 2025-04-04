package homework_1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.domain.User;
import homework_1.domain.UserRole;
import homework_1.dto.UserLoginDto;
import homework_1.dto.UserRegistrationDto;
import homework_1.mapper.UserMapper;
import homework_1.services.AuthService;
import homework_1.exceptions.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;
    private AuthService authService;
    private ObjectMapper objectMapper;
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        objectMapper = new ObjectMapper();
        userMapper = Mappers.getMapper(UserMapper.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
    }

    @Test
    void register_ShouldReturnCreatedUser() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto("John", "john@example.com", "password123", UserRole.USER);
        User user = new User(1L, "John", "john@example.com", "password123", UserRole.USER, false);

        when(authService.register(any(), any(), any(), any())).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreValid() throws Exception {
        UserLoginDto dto = new UserLoginDto("john@example.com", "password123");
        User user = new User(1L, "John", "john@example.com", "password123", UserRole.USER, false);

        when(authService.login(dto.getEmail(), dto.getPassword())).thenReturn(user);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        UserLoginDto dto = new UserLoginDto("wrong@example.com", "wrongpass");

        when(authService.login(dto.getEmail(), dto.getPassword()))
                .thenThrow(new AuthenticationException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        List<User> users = List.of(
                new User(1L, "John", "john@example.com", "pass", UserRole.USER, false),
                new User(2L, "Alice", "alice@example.com", "pass", UserRole.ADMIN, false)
        );

        when(authService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].email").value("alice@example.com"));
    }
}