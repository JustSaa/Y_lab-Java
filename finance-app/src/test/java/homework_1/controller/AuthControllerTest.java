package homework_1.controller;

import audit.aspect.AuditAspect;
import audit.logger.AuditLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import homework_1.domain.User;
import homework_1.domain.UserRole;
import homework_1.dto.*;
import homework_1.mapper.UserMapper;
import homework_1.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @MockBean
    private AuditLogger auditLogger;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /register — успешная регистрация")
    void testRegister() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto("Test", "test@mail.com", "123456", UserRole.USER);
        User user = new User(1L, "Test", "test@mail.com", "123456", UserRole.USER, false);
        UserResponseDto responseDto = new UserResponseDto(1L, "Test", "test@mail.com", UserRole.USER, false);

        when(authService.register(any(), any(), any(), any())).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@mail.com"));
    }

    @Test
    @DisplayName("POST /login — успешный логин")
    void testLogin() throws Exception {
        UserLoginDto dto = new UserLoginDto("test@mail.com", "1234");
        User user = new User(1L, "Test", "test@mail.com", "1234", UserRole.USER, false);
        UserResponseDto responseDto = new UserResponseDto(1L, "Test", "test@mail.com", UserRole.USER, false);

        when(authService.login(dto.getEmail(), dto.getPassword())).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@mail.com"));
    }

    @Test
    @DisplayName("POST /block — блокировка пользователя")
    void testBlockUser() throws Exception {
        AdminActionDto dto = new AdminActionDto("admin@mail.com", "user@mail.com");
        doNothing().when(authService).blockUser(any(), any());

        mockMvc.perform(post("/api/auth/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Пользователь заблокирован"));
    }

    @Test
    @DisplayName("PUT /profile — обновление профиля")
    void testUpdateProfile() throws Exception {
        UserUpdateDto dto = new UserUpdateDto("old@mail.com", "New Name", "new@mail.com", "newpass", UserRole.USER);
        doNothing().when(authService).updateUser(any(), any(), any(), any(), any());

        mockMvc.perform(put("/api/auth/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Профиль обновлён"));
    }

    @Test
    @DisplayName("DELETE /profile — удаление своего профиля")
    void testDeleteSelf() throws Exception {
        doNothing().when(authService).deleteUser("test@mail.com");

        mockMvc.perform(delete("/api/auth/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "test@mail.com"))))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /user — удаление админом")
    void testDeleteByAdmin() throws Exception {
        AdminActionDto dto = new AdminActionDto("admin@mail.com", "user@mail.com");
        doNothing().when(authService).deleteUser(any(), any());

        mockMvc.perform(delete("/api/auth/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /users — список пользователей")
    void testGetAllUsers() throws Exception {
        User user = new User(1L, "Test", "test@mail.com", "pass", UserRole.USER, false);
        UserResponseDto dto = new UserResponseDto(1L, "Test", "test@mail.com", UserRole.USER, false);

        when(authService.getAllUsers()).thenReturn(List.of(user));
        when(userMapper.toDto(any())).thenReturn(dto);

        mockMvc.perform(get("/api/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("test@mail.com"));
    }
}